import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import utility.ConfigUtility;

import java.io.File;
import java.util.Properties;

public class settingController {

    @FXML
    private Button buttonSave;

    @FXML
    private Button jadxButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField jadxTextField;

    @FXML
    private VBox root;

    @FXML
    private TextField adbTextField;

    @FXML
    private TextField vscodeTextField;

    @FXML
    private Button vsCodeButton;

    @FXML
    private Button adbButton;


    @FXML
    private TextField keystoreFilePathTextField;

    @FXML
    private Button vsCodeButton1;

    @FXML
    private TextField keyAliasTextField;

    @FXML
    private TextField keyPasswordTextField;

    @FXML
    private TextField keyStorePasswordTextField;

    @FXML
    private Button cancelButton1;


    @FXML
    void chooseAdbFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File adb = fileChooser.showOpenDialog(null);
        if(adb==null)
            return;
        adbTextField.setText(adb.getAbsolutePath());
    }

    @FXML
    void chooseJadxFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File jadx = fileChooser.showOpenDialog(null);
        if(jadx==null)
            return;
        jadxTextField.setText(jadx.getAbsolutePath());
    }

    @FXML
    void chooseVSCodeFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File code = fileChooser.showOpenDialog(null);
        if(code==null)
            return;
        vscodeTextField.setText(code.getAbsolutePath());
    }

    @FXML
    void cancelButtonClick(ActionEvent event) {
        FxApp.switchScreen(buttonSave,"FxMain");
    }

    @FXML
    void chooseKeystoreFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File keystore = fileChooser.showOpenDialog(null);
        if(keystore==null)
            return;
        keystoreFilePathTextField.setText(keystore.getAbsolutePath());
    }

    @FXML
    void signingCancelButtonClick(ActionEvent event) {
        FxApp.switchScreen(buttonSave,"FxMain");
    }

    @FXML
    void signingSaveButtonClick(ActionEvent event) {
        Properties properties=ConfigUtility.getProperties();
        properties.setProperty("src.keystore", keystoreFilePathTextField.getText());
        properties.setProperty("src.keyAlias", keyAliasTextField.getText());
        properties.setProperty("src.keyPassword", keyPasswordTextField.getText());
        properties.setProperty("src.keystorePassword", keyStorePasswordTextField.getText());
        ConfigUtility.updateProperties(properties);
        FxApp.switchScreen(buttonSave,"FxMain");
    }


    @FXML
    void saveButtonClick(ActionEvent event) {
        Properties properties=ConfigUtility.getProperties();
        properties.setProperty("src.adb", adbTextField.getText());
        properties.setProperty("src.jadx",jadxTextField.getText());
        properties.setProperty("src.vscode", vscodeTextField.getText());
        ConfigUtility.updateProperties(properties);
        FxApp.switchScreen(buttonSave,"FxMain");
    }

    @FXML
    private void initialize() {
        Properties properties=ConfigUtility.getProperties();
        adbTextField.setText(properties.getOrDefault("src.adb","").toString());
        jadxTextField.setText(properties.getOrDefault("src.jadx","").toString());
        vscodeTextField.setText(properties.getOrDefault("src.vscode","").toString());

        keystoreFilePathTextField.setText(properties.getOrDefault("src.keystore","").toString());
        keyAliasTextField.setText(properties.getOrDefault("src.keyAlias","").toString());
        keyPasswordTextField.setText(properties.getOrDefault("src.keyPassword","").toString());
        keyStorePasswordTextField.setText(properties.getOrDefault("src.keystorePassword","").toString());



    }


}
