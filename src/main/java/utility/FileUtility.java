package utility;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileUtility {
    public static void writeToFile(File file, String data) {
        try {
            FileWriter writer=new FileWriter(file);
            PrintWriter printWriter=new PrintWriter(writer);
            printWriter.print(data);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFileData(String path){
        try {
            FileReader fileReader=new FileReader(path);
            StringBuilder sb=new StringBuilder();
            int i;
            while((i=fileReader.read())!=-1)
                sb.append((char)i);
            return sb.toString();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return "";
    }
}
