package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigUtility {


    public static Properties getProperties(){
        try {
            Properties properties = new Properties();

            try (FileInputStream fis = new FileInputStream("app-config.txt")) {
                properties.load(fis);
            }catch (Exception e){
                properties.setProperty("src.tools", "/Users/hello/Documents/Decompile/tools/");
                properties.setProperty("src.adb", "");
                properties.setProperty("src.jadx", "");
                properties.setProperty("src.vscode", "");
                properties.setProperty("src.keystore", "");
                properties.setProperty("src.keyAlias", "");
                properties.setProperty("src.keyPassword", "");
                properties.setProperty("src.keystorePassword", "");
                updateProperties(properties);
            }

            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateProperties( Properties properties){
        try {
            FileOutputStream out = new FileOutputStream("app-config.txt");
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getToolsDir(){
        Properties properties=getProperties();
        return properties.getProperty("src.tools");
    }


}
