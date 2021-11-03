package de.bhtpaf.pacbomb.helper;

import javafx.scene.control.Alert;
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
