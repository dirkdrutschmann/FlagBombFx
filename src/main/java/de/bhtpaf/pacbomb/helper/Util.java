package de.bhtpaf.pacbomb.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class Util
{
    public static void showErrorMessageBox(String msg)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        _showAlert(alert, msg);
    }

    public static void showMessageBox(String msg)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        _showAlert(alert, msg);
    }

    public static Alert getYesNoMessageBox(String msg, String title)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (title == null)
        {
            title = "Information";
        }

        alert.setTitle(title);
        alert.setContentText(msg);

        ButtonType yesButton = new ButtonType("Ja", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Nein", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);
        return alert;

    }

    private static void _showAlert(Alert alert, String msg)
    {
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static ByteArrayInputStream getImageOfBase64String(String base64)
    {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);

        return bis;
    }
}
