package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdExecutor {
    public static int runCmd(String cmd,boolean isShowOutput){
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader outputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));



            if(isShowOutput){
                String line;
                while ((line = outputReader.readLine()) != null) {
                    System.out.println(line);
                }

                String error;
                while ((error = errorReader.readLine()) != null) {
                    System.out.println(error);
                }
            }
            int exitVal = process.waitFor();
            return exitVal;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void debugLog(String data){
        runCmd("echo "+data,true);
    }

    public static String runCmd(String cmd){
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));


            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if(exitVal!=0){
                System.out.println("Failed...");
            }
            return output.toString().trim();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
