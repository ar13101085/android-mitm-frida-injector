package utility;

import java.io.File;

public class ApkFileManager {
    private File apkFile;
    public ApkFileManager(String apkPath,boolean isCheckSignature){
        apkFile=new File(apkPath);
        if(isCheckSignature){
            updateApkToSignatureApk();
        }
    }

    public void updateApkToSignatureApk(){
        if(new File(getSignatureBypassApkPath()).exists()){
            apkFile = new File(getSignatureBypassApkPath());
        }
    }

    public String getRootDir(){
        return apkFile.getParent();
    }

    public String getBuildApkPath(){
        return getRootDir()+File.separator+getApkName(apkFile)+"_out.apk";
    }

    public String getFinalBuildSignedPath(){
        String buildApk=getRootDir()+File.separator+getApkName(apkFile)+"_out-aligned-debugSigned.apk";
        if(new File(buildApk).exists()){
            return buildApk;
        }else{
            return getRootDir()+File.separator+getApkName(apkFile)+"_out-aligned-signed.apk";
        }

    }

    public String getSignatureBypassApkPath(){
        String file=apkFile.getAbsolutePath().replace("_out_sb","");
        return getRootDir()+File.separator+getApkName(new File(file))+"_out_sb.apk";
    }

    public String getDecompileDir(){
       // return getRootDir()+File.separator+getApkName(apkFile)+"-decompiled";
        return new File(getRootDir()+File.separator+getApkName(apkFile)+"-decompiled").getAbsolutePath();
    }

    public String getApkPath(){
        return apkFile.getAbsolutePath();
    }

    public String getApkName(File file){
        String name=file.getName();
        String apkName=name.replaceFirst("[.][^.]+$", "");
        return apkName;
    }
}
