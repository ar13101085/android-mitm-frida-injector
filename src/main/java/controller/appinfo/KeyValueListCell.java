package controller.appinfo;

import infrustucture.ICallback;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import model.KeyValue;

public class KeyValueListCell extends ListCell<KeyValue> {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsButton;

    ICallback _eventCallback;
    public KeyValueListCell(ICallback callback){
        this._eventCallback=callback;
    }

    FXMLLoader mLLoader;
    @Override
    protected void updateItem(KeyValue item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item==null){
            setText(null);
            setGraphic(null);
            return;
        }
        System.out.println("Else Call...");
        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("/appInfo/two_item_row.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
                System.out.println("Successfully render..");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //System.out.println(item.keyName+" "+item.value);
        nameLabel.setText(item.keyName);
        detailsButton.setText(item.value);

        detailsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _eventCallback.receivedData(1,true,item);
            }
        });

        setText(null);
        setStyle("-fx-background-color: #fff;");
        nameLabel.setTextFill(Color.BLACK);
        setGraphic(rootPane);

    }
}
