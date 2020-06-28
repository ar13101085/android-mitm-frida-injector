import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class FxApp extends Application {
    @FXML
    FxMainController mainController;
    private Stage primaryStage;
    VBox primaryLayout;
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage=stage;
        this.primaryStage.setTitle("AR-MITM-FRIDA");
        System.out.println("App is running..");
        showWindow();
        //changeScreen("FxMain.fxml");
    }

    private void showWindow() {
        try {
            FXMLLoader fxmlLoader= new FXMLLoader();
            fxmlLoader.setLocation(FxApp.class.getResource("FxMain.fxml"));
            primaryLayout=fxmlLoader.load();
            Scene scene=new Scene(primaryLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*public void changeScreen(String name){
        try {
            Parent pane = FXMLLoader.load(
                    getClass().getResource(name));

            if(primaryStage.getScene()==null){
                Scene scene=new Scene(pane);
                primaryStage.setScene(scene);
                primaryStage.show();
            }else{
                primaryStage.getScene().setRoot(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void switchScreen(Control view,String name){
        try {
            Stage stage = (Stage) view.getScene().getWindow();
            Parent pane = FXMLLoader.load(
                    FxApp.class.getResource(name+".fxml"));
            stage.getScene().setRoot(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Parent getParentFromXml(String name){

        try {
            Parent pane = FXMLLoader.load(
                    FxApp.class.getResource(name+".fxml"));
            return pane;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
