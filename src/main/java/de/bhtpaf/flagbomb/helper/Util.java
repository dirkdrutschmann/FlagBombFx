package de.bhtpaf.flagbomb.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
        List<ButtonType> buttons = new ArrayList<>();
        buttons.add(new ButtonType("Ja", ButtonBar.ButtonData.YES));
        buttons.add(new ButtonType("Nein", ButtonBar.ButtonData.NO));

        return getCustomMessageBox(msg, title, buttons);
    }

    public static Alert getCustomMessageBox(String msg, String title, List<ButtonType> buttons)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (title == null)
        {
            title = "Information";
        }

        alert.setTitle(title);
        alert.setContentText(msg);

        alert.getButtonTypes().setAll(buttons);
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

    public static String getBase64StringOfImage(File file)
    {
        String returnString = "";

        if (file == null)
        {
            return returnString;
        }

        try
        {
            FileInputStream fileInputStreamReader = new FileInputStream(file);

            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);

            returnString = Base64.getEncoder().encodeToString(bytes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnString;
    }

    public static boolean isImage(File file)
    {
        boolean b = false;

        try
        {
            b = (ImageIO.read(file) != null);
        }
        catch (IOException e)
        {
            System.out.println("Image cannot be found");
        }

        return b;
    }
}
