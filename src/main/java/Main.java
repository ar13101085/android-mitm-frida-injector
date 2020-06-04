import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import utility.CmdExecutor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String apk="";
        boolean isDebugRemove=false;
        boolean isDontInjectFrida=false;
        boolean isMitmDisable=false;
        for (int i = 0; i < args.length; i++) {
            String arg=args[i];
            if(i==0){
                apk=arg;
                continue;
            }
            if(arg.equalsIgnoreCase("-d")){
                isDebugRemove=true;
            }else if(arg.equalsIgnoreCase("-f")){
                isDontInjectFrida=true;
            }else if(arg.equalsIgnoreCase("-m")){
                isMitmDisable=true;
            }

        }

        if(apk.length()==0){
            CmdExecutor.debugLog("Apk can not be empty.");
            return;
        }

        String basePath=CmdExecutor.runCmd("pwd")+"/";
        String apkName=apk.replaceFirst("[.][^.]+$", "");
        String rootPath=basePath+apkName+"/";


        //apk decompile
        String cmd1="java -jar tools/apktool.jar d "+basePath+apk;
        CmdExecutor.runCmd(cmd1,true);

        try {
            File fXmlFile = new File(basePath+apkName+"/AndroidManifest.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.normalizeDocument();
            NamedNodeMap map=doc.getElementsByTagName("application").item(0).getAttributes();
            Element element= (Element) doc.getElementsByTagName("application").item(0);
            if(!isDebugRemove)
                element.setAttribute("android:debuggable","true");
            if(!isMitmDisable)
                element.setAttribute("android:networkSecurityConfig","@xml/net_config");

            String className="";
            if(element.hasAttribute("android:name")){
                //System.out.println(element.getAttribute("android:name"));
                className=element.getAttribute("android:name");
            }else{
                NodeList launcherList=doc.getElementsByTagName("category");
                for (int i = 0; i <launcherList.getLength() ; i++) {
                    Element launcherElement= (Element) launcherList.item(i);
                    if(launcherElement.hasAttribute("android:name") && launcherElement.getAttribute("android:name").equalsIgnoreCase("android.intent.category.LAUNCHER")){
                        Element activityNode= (Element) launcherElement.getParentNode().getParentNode();
                        //System.out.println(activityNode.getAttribute("android:name"));
                        className=activityNode.getAttribute("android:name");
                        break;
                    }
                }
            }

            //config write
            configWrite(basePath, apkName);

            if(!isDontInjectFrida){
                File file=new File(rootPath);

                //smalli update
                for (File singleFile:file.listFiles()
                     ) {
                    if(singleFile.isDirectory() && singleFile.getName().startsWith("smali")){
                        File classFile=new File(rootPath+singleFile.getName()+"/"+className.replace(".","/")+".smali");
                        if(classFile.exists()){
                            className=classFile.getAbsolutePath();
                            CmdExecutor.debugLog("Class name "+className);
                            break;
                        }
                    }
                }

                //className=rootPath+"smali/"+className.replace(".","/")+".smali";
                fridaContentInject(className);
                //frida update
                fridaLibCopy(rootPath);
            }



            //final manifest
            String manifestFinalOutput = writeXmlDocumentToXmlFile(doc);
            writeToFile(fXmlFile,manifestFinalOutput);


            //Build apk
            String cmd2="java -jar tools/apktool.jar b "+basePath+apkName+" -o "+apkName+"_debug.apk";
            int result=CmdExecutor.runCmd(cmd2,true);


            //apk-signer
            String cmd3="java -jar tools/uber-apk-signer.jar -a "+basePath+apkName+"_debug.apk";
            int signedResult=CmdExecutor.runCmd(cmd3,true);
            CmdExecutor.debugLog("Successfully complete all operation");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void fridaContentInject(String className) {
        String injectCode="# direct methods\n" +
                ".method static constructor <clinit>()V\n" +
                "    .locals 3\n" +
                "    const-string v2, \"frida-gadget\"\n" +
                "    invoke-static {v2}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V\n" +
                "\n" +
                "    return-void\n" +
                ".end method\n\n";
        boolean isFirstTime=false;
        String smaliData=readFileData(className);
        Scanner scanner=new Scanner(smaliData);
        StringBuilder modifiedContent=new StringBuilder();
        while (scanner.hasNext()){
            String line=scanner.nextLine();
            if(!isFirstTime && line.equalsIgnoreCase("# direct methods")){
                modifiedContent.append(injectCode);
                isFirstTime=true;
            }
            modifiedContent.append(line+"\n");
        }
        writeToFile(new File(className),modifiedContent.toString());
    }

    private static void fridaLibCopy(String basePath) {
        String armeabi7FolderPath=basePath+"lib/armeabi-v7a";
        File armeabi7Folder=new File(armeabi7FolderPath);
        if(!armeabi7Folder.exists()){
            armeabi7Folder.mkdir();
        }

        String arm64abi8FolderPath=basePath+"lib/arm64-v8a";
        File arm64abi8Folder=new File(arm64abi8FolderPath);
        if(!arm64abi8Folder.exists()){
            arm64abi8Folder.mkdir();
        }

        String armeabi7Frida="tools/armeabi-v7a/libfrida-gadget.so";
        String arm64abi8Frida="tools/arm64-v8a/libfrida-gadget.so";

        try {
            FileUtils.copyFileToDirectory(new File(armeabi7Frida),armeabi7Folder);
            FileUtils.copyFileToDirectory(new File(arm64abi8Frida),arm64abi8Folder);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void configWrite(String basePath, String apkName) throws IOException {
        File xmlDir=new File(basePath+apkName+"/res/xml");
        if(!xmlDir.exists()){
            xmlDir.mkdir();
        }
        File xmlFile=new File(xmlDir.getAbsolutePath(),"net_config.xml");
        String netConfig="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<network-security-config>\n" +
                "    <base-config cleartextTrafficPermitted=\"true\">\n" +
                "        <trust-anchors>\n" +
                "            <certificates src=\"system\" />\n" +
                "        </trust-anchors>\n" +
                "    </base-config>\n" +
                "</network-security-config>";
        writeToFile(xmlFile, netConfig);
    }

    private static void writeToFile(File file, String data) {
        try {
            FileWriter writer=new FileWriter(file);
            PrintWriter printWriter=new PrintWriter(writer);
            printWriter.print(data);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFileData(String path){
        try {
            FileReader fileReader=new FileReader(path);
            StringBuilder sb=new StringBuilder();
            int i;
            while((i=fileReader.read())!=-1)
                sb.append((char)i);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String writeXmlDocumentToXmlFile(Document xmlDocument)
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();

            // Uncomment if you do not require XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            //A character stream that collects its output in a string buffer,
            //which can then be used to construct a string.
            StringWriter writer = new StringWriter();

            //transform document to string
            transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));

            String xmlString = writer.getBuffer().toString();
            return xmlString;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
