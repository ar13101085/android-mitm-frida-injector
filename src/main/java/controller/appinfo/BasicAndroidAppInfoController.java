package controller.appinfo;

import infrustucture.ICallback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.KeyValue;

public class BasicAndroidAppInfoController {
    @FXML
    private ListView<KeyValue> listView;

    public void setData( ObservableList<KeyValue> items, Callback callback){
        listView.setItems(items);
        listView.setCellFactory(callback);
    }

}
