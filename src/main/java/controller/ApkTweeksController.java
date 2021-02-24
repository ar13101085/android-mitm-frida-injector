package controller;

import infrustucture.ICallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ApkTweeksController {
    private ICallback _logWriter;
    public ApkTweeksController(ICallback logWriter){
        this._logWriter=logWriter;
    }

    public int runCmd(String cmd){
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader outputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));



            String line;
            while ((line = outputReader.readLine()) != null) {
                _logWriter.receivedData(1,true,line);
            }

            String error;
            while ((error = errorReader.readLine()) != null) {
                _logWriter.receivedData(1,false,error);
            }


            int exitVal = process.waitFor();
            return exitVal;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void runCmdAsync(String cmd){
        try {
            Process process = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
