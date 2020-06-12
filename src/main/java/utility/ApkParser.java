package utility;

import controller.ApkTweeksController;
import infrustucture.ICallback;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class ApkParser {
    private ApkFileManager _apkFileManager;
    private ICallback _logger;
    public ApkParser(ApkFileManager apkFileManager, ICallback logger){
        this._apkFileManager=apkFileManager;
        this._logger=logger;
    }

    public File getManifestFile(){
        return new File(_apkFileManager.getDecompileDir()+"/AndroidManifest.xml");
    }
    public Document getManifestXml(){
        try {
            File fXmlFile = getManifestFile();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.normalizeDocument();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean setToDebugAble(){
        Document doc= getManifestXml();
        Element element= (Element) doc.getElementsByTagName("application").item(0);
        element.setAttribute("android:debuggable","true");
        String finalXml=xmlDocToString(doc);
        FileUtility.writeToFile(getManifestFile(),finalXml);
        return true;
    }

    public boolean isDebugableEnable(){
        Document doc= getManifestXml();
        Element element= (Element) doc.getElementsByTagName("application").item(0);
        return element.hasAttribute("android:debuggable") && element.getAttribute("android:debuggable").equalsIgnoreCase("true")?true:false;
    }

    public boolean mitmInject(){
        try {
            Document doc= getManifestXml();
            Element element= (Element) doc.getElementsByTagName("application").item(0);
            element.setAttribute("android:networkSecurityConfig","@xml/ar_net_config");
            configWrite();
            String finalXml=xmlDocToString(doc);
            FileUtility.writeToFile(getManifestFile(),finalXml);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMitmInjected(){
        Document doc= getManifestXml();
        Element element= (Element) doc.getElementsByTagName("application").item(0);
        return element.hasAttribute("android:networkSecurityConfig") && element.getAttribute("android:networkSecurityConfig").equalsIgnoreCase("@xml/ar_net_config")?true:false;
    }



    //region Frida
    public boolean fridaInject(){

        String className = getEntryClassName();

        //className=rootPath+"smali/"+className.replace(".","/")+".smali";
        fridaContentInject(className);
        //frida update
        fridaLibCopy();
        return true;

    }

    private String getEntryClassName() {
        Document doc= getManifestXml();
        Element element= (Element) doc.getElementsByTagName("application").item(0);

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

        File file=new File(_apkFileManager.getDecompileDir());

        //smalli update
        for (File singleFile:file.listFiles()
        ) {
            if(singleFile.isDirectory() && singleFile.getName().startsWith("smali")){
                File classFile=new File(_apkFileManager.getDecompileDir()+"/"+singleFile.getName()+"/"+className.replace(".","/")+".smali");
                if(classFile.exists()){
                    className=classFile.getAbsolutePath();
                    CmdExecutor.debugLog("Class name "+className);
                    break;
                }
            }
        }
        return className;
    }

    public boolean isAvailableFrida(){
        String className=getEntryClassName();
        System.out.println(className);
        String smaliData=FileUtility.readFileData(className);
        if(smaliData.contains("frida-gadget")){
            return true;
        }
        return false;
    }
    public void fridaContentInject(String className) {
        String injectCode="# direct methods\n" +
                ".method static constructor <clinit>()V\n" +
                "    .locals 3\n" +
                "    const-string v2, \"frida-gadget\"\n" +
                "    invoke-static {v2}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V\n" +
                "\n" +
                "    return-void\n" +
                ".end method\n\n";
        boolean isFirstTime=false;
        String smaliData=FileUtility.readFileData(className);
        if(smaliData.contains("frida-gadget")){
            CmdExecutor.debugLog("frida-gadget already injected..");
            return;
        }
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
        FileUtility.writeToFile(new File(className),modifiedContent.toString());
    }

    public void fridaLibCopy() {
        String armeabi7FolderPath=_apkFileManager.getDecompileDir()+"/lib/armeabi-v7a";
        File armeabi7Folder=new File(armeabi7FolderPath);
        if(!armeabi7Folder.exists()){
            armeabi7Folder.mkdir();
        }

        String arm64abi8FolderPath=_apkFileManager.getDecompileDir()+"/lib/arm64-v8a";
        File arm64abi8Folder=new File(arm64abi8FolderPath);
        if(!arm64abi8Folder.exists()){
            arm64abi8Folder.mkdir();
        }

        String armeabi7Frida=ConfigUtility.getToolsDir()+"armeabi-v7a/libfrida-gadget.so";
        String arm64abi8Frida=ConfigUtility.getToolsDir()+"arm64-v8a/libfrida-gadget.so";

        try {
            FileUtils.copyFileToDirectory(new File(armeabi7Frida),armeabi7Folder);
            FileUtils.copyFileToDirectory(new File(arm64abi8Frida),arm64abi8Folder);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    //endregion



    public boolean decompileApk(){
        ApkTweeksController tweeksController=new ApkTweeksController(_logger);
        tweeksController.runCmd("java -jar "+ ConfigUtility.getToolsDir() +"apktool.jar d "+_apkFileManager.getApkPath()+" -o "+_apkFileManager.getDecompileDir());
        return isDecompileComplete();
    }
    public boolean isDecompileComplete(){
        File file=new File(_apkFileManager.getDecompileDir());
        return file.exists();
    }


    public void installApkToDevice(){
        Properties properties=ConfigUtility.getProperties();
        String adbPath=properties.getProperty("src.adb");
        String cmd=adbPath + " install "+_apkFileManager.getFinalBuildSignedPath();
        ApkTweeksController tweeksController=new ApkTweeksController(_logger);
        tweeksController.runCmd(cmd);
    }

    public void buildApk(){
        //--use-aapt2
        String buildCmd= "java -jar tools/apktool.jar b "+_apkFileManager.getDecompileDir()+" -o "+_apkFileManager.getBuildApkPath();
        ApkTweeksController tweeksController=new ApkTweeksController(_logger);
        int result=tweeksController.runCmd(buildCmd);
        if(result!=0){
            buildCmd +=" --use-aapt2";
            tweeksController.runCmd(buildCmd);
        }

        String signingCmd="java -jar "+ConfigUtility.getToolsDir()+"uber-apk-signer.jar -a "+_apkFileManager.getBuildApkPath();
        tweeksController.runCmd(signingCmd);
    }



    public void apkSignatureBypass(){
        String basePath=ConfigUtility.getToolsDir()+"SignatureBypass/";
        String value=FileUtility.readFileData(basePath+"config_backup.txt");

        value=value.replace("src.apk",_apkFileManager.getApkPath());
        value=value.replace("src.apk",_apkFileManager.getApkPath());
        value=value.replace("out.apk",_apkFileManager.getSignatureBypassApkPath());
        value=value.replace("test.keystore",basePath+"test.keystore");

        //writeToFile(new File(basePath+"config.txt"),value);
        FileUtility.writeToFile(new File("config.txt"),value);

        ApkTweeksController tweeksController=new ApkTweeksController(_logger);
        String cmd="java -jar "+basePath+"nkstool.jar";
        System.out.println(cmd);
        tweeksController.runCmd(cmd);
    }

    public boolean isApkSignatureInjected(){
        if(new File(_apkFileManager.getSignatureBypassApkPath()).exists()){
            return true;
        }

        Document doc= getManifestXml();
        if(doc==null)
            return false;
        Element element= (Element) doc.getElementsByTagName("application").item(0);

        String className="";
        if(element.hasAttribute("android:name")){
            //System.out.println(element.getAttribute("android:name"));
            className=element.getAttribute("android:name");
        }
        if(className.equalsIgnoreCase("cc.binmt.signature.PmsHookApplication")){
            return true;
        }
        return false;
    }


    //region utility
    public void configWrite() throws IOException {
        File xmlDir=new File(_apkFileManager.getDecompileDir()+"/res/xml");
        if(!xmlDir.exists()){
            xmlDir.mkdir();
        }
        File xmlFile=new File(xmlDir.getAbsolutePath(),"ar_net_config.xml");
        String netConfig="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<network-security-config>\n" +
                "    <base-config cleartextTrafficPermitted=\"true\">\n" +
                "        <trust-anchors>\n" +
                "            <certificates src=\"system\" />\n" +
                "        </trust-anchors>\n" +
                "    </base-config>\n" +
                "</network-security-config>";
        FileUtility.writeToFile(xmlFile, netConfig);
    }



    public String xmlDocToString(Document xmlDocument)
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
    //endregion
}