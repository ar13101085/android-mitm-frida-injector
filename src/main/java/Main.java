import javafx.application.Application;
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

        if(System.out.printf("%s","hello world")!=null){

        }
       /* String apk="";
        //String apk="MX_Player_1.23.5.apk";
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
            System.out.println(cmd1);
            System.out.println(cmd2);
            System.out.println(cmd3);
            int signedResult=CmdExecutor.runCmd(cmd3,true);
            CmdExecutor.debugLog("Successfully complete all operation");

        } catch (Exception e) {
            e.printStackTrace();
        }
*/

    }


}
