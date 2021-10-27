package de.bhtpaf.pacbomb.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Util
{
    public static void showErrorMessageBox(String msg)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
