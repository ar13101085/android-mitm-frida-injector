import infrustucture.ICallback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import utility.ApkFileManager;
import utility.ApkParser;
import utility.ResourceManager;

import java.io.File;

public class FxMainController {


    @FXML
    private Button apkChooserButton;

    @FXML
    private ImageView installApkIV;

    @FXML
    private Button signatureBypassButton;

    @FXML
    private ImageView decompileButtonImageView;

    @FXML
    private ImageView enableDebugButtonIV;

    @FXML
    private Button sslBypassButton;

    @FXML
    private Button appSetting;

    @FXML
    private ImageView signatureBypassButtonIV;

    @FXML
    private Button fridaInjectButton;

    @FXML
    private Button decompileButton;

    @FXML
    private Button enableDebugButton;

    @FXML
    private ImageView buildApkIV;

    @FXML
    private Button buildApkButton;

    @FXML
    private ImageView fridaInjectButtonIV;

    @FXML
    private Button installApkButton;

    @FXML
    private Button enableMitmButton;

    @FXML
    private TextFlow textFLow;

    @FXML
    private ImageView sslBypassButtonIV;

    @FXML
    private ImageView enableMitmButtonIV;

    @FXML
    void settingClick(ActionEvent event) {
        FxApp.switchScreen(appSetting,"root");
    }

    @FXML
    void buildApkButtonClick(ActionEvent event) {
        changeImage(buildApkIV,ResourceManager.WAIT_IMAGE);
        new Thread(()->{
            _apkParser.buildApk();
            Platform.runLater(()->{
                changeImage(buildApkIV,ResourceManager.BUILD_IMAGE);
            });

        }).start();
    }

    @FXML
    void installApkButtonClick(ActionEvent event) {
        changeImage(installApkIV,ResourceManager.WAIT_IMAGE);
        new Thread(()-> {
            _apkParser.installApkToDevice();
            changeImage(installApkIV,ResourceManager.INSTALL_IMAGE);

        }
        ).start();

    }

    @FXML
    void decompile(ActionEvent event) {
        changeImage(decompileButtonImageView,ResourceManager.WAIT_IMAGE);
        new Thread(()->{
            _apkParser.decompileApk();
            Platform.runLater(()->{
                checkApkStatus();
            });
        }).start();


    }

    @FXML
    void signatureBypass(ActionEvent event) {
        changeImage(signatureBypassButtonIV,ResourceManager.WAIT_IMAGE);
        new Thread(()->{
            _apkParser.apkSignatureBypass();

            Platform.runLater(()->{
                if(_apkParser.isApkSignatureInjected()){
                    apkSignatureBypassLoadApkDialog(new File(_apkManager.getApkPath()),new File(_apkManager.getSignatureBypassApkPath()));
                }
                checkApkStatus();
            });
        }).start();
    }

    @FXML
    void fridaInject(ActionEvent event) {
        changeImage(fridaInjectButtonIV,ResourceManager.WAIT_IMAGE);
        if(!_apkParser.isAvailableFrida()){
            new Thread(()->{
                _apkParser.fridaInject();
                Platform.runLater(()->{
                    checkApkStatus();
                });
            }).start();

        }else{
            changeImage(fridaInjectButtonIV,ResourceManager.DONE_IMAGE);
        }


    }

    @FXML
    void enableDebuggingClick(ActionEvent event) {
        changeImage(enableDebugButtonIV,ResourceManager.WAIT_IMAGE);
        if(!_apkParser.isDebugableEnable()){
            _apkParser.setToDebugAble();
        }
        checkApkStatus();
    }

    @FXML
    void enableMitmButtonClick(ActionEvent event) {
        changeImage(enableMitmButtonIV,ResourceManager.WAIT_IMAGE);
        if(!_apkParser.isMitmInjected()){
            _apkParser.mitmInject();
        }
        checkApkStatus();
    }

    @FXML
    void sslBypassButtonClick(ActionEvent event) {

    }


    @FXML
    void chooseApkFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File apkFile = fileChooser.showOpenDialog(null);
        if(apkFile==null)
            return;
        _apkManager=new ApkFileManager(apkFile.getAbsolutePath(),false);

        File signatureBypassApk=new File(_apkManager.getSignatureBypassApkPath());
        if(signatureBypassApk.exists()){
            apkSignatureBypassLoadApkDialog(apkFile,signatureBypassApk);
        }else
            apkLoad(apkFile);

    }

    private void apkLoad(File apkFile) {
        apkChooserButton.setText(apkFile.getAbsolutePath());
        _apkManager=new ApkFileManager(apkFile.getAbsolutePath(),false);
        _apkParser=new ApkParser(_apkManager,_consoleLogger);
        checkApkStatus();
    }

    private void apkSignatureBypassLoadApkDialog(File mainApk,File bypassApk){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Signature bypass apk found.Do you want to load this apk?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            apkLoad(bypassApk);
        }else{
            apkLoad(mainApk);
        }
    }

    private void checkApkStatus(){


        if(_apkParser.isDecompileComplete()){
            if (_apkParser.isDecompileComplete()) {
                changeImage(decompileButtonImageView, ResourceManager.DONE_IMAGE);
            } else {
                changeImage(decompileButtonImageView, ResourceManager.WHAT_IMAGE);
            }

            if (_apkParser.isDebugableEnable()) {
                changeImage(enableDebugButtonIV, ResourceManager.DONE_IMAGE);
            } else {
                changeImage(enableDebugButtonIV, ResourceManager.WHAT_IMAGE);
            }

            if (_apkParser.isMitmInjected()) {
                changeImage(enableMitmButtonIV, ResourceManager.DONE_IMAGE);
            } else {
                changeImage(enableMitmButtonIV, ResourceManager.WHAT_IMAGE);
            }

            if (_apkParser.isAvailableFrida()) {
                changeImage(fridaInjectButtonIV, ResourceManager.DONE_IMAGE);
            } else {
                changeImage(fridaInjectButtonIV, ResourceManager.WHAT_IMAGE);
            }


        }else{
            changeImage(decompileButtonImageView, ResourceManager.WHAT_IMAGE);
            changeImage(enableDebugButtonIV, ResourceManager.WHAT_IMAGE);
            changeImage(enableMitmButtonIV, ResourceManager.WHAT_IMAGE);
            changeImage(fridaInjectButtonIV, ResourceManager.WHAT_IMAGE);
        }

        if (_apkParser.isApkSignatureInjected()) {
            changeImage(signatureBypassButtonIV, ResourceManager.DONE_IMAGE);
        } else {
            changeImage(signatureBypassButtonIV, ResourceManager.WHAT_IMAGE);
        }


    }



    ApkParser _apkParser;
    ApkFileManager _apkManager;
    ICallback _consoleLogger= new ICallback() {
        @Override
        public <T> void receivedData(int code, boolean isSuccess, T data) {

            Text text_1 = new Text(String.valueOf(data)+"\n");
            text_1.setTextAlignment(TextAlignment.LEFT);

            if(isSuccess){
                text_1.setFill(Color.BLACK);
                text_1.setFont(Font.font("Verdana", 17));
            }else{
                text_1.setFill(Color.RED);
                text_1.setFont(Font.font("Verdana", 17));
            }
            Platform.runLater(()->{
                textFLow.getChildren().add(text_1);
            });


        }
    };

    private void changeImage(ImageView imageView, String imageName){
        Image image=new Image(imageName);
        imageView.setImage(image);
    }


}
