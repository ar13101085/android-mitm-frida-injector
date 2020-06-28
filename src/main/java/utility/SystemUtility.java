package utility;

import javafx.scene.Node;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class SystemUtility {
    public static void copyToClipboard(String text, Node node){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        if(node!=null)
            Toast.show("Copy to clipboard..",node);
    }
}
