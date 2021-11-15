package de.bhtpaf.flagbomb.controllers;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class RegisterUserController
{
    private Scene _goBackScene;
    private Stage _mainStage;
    private Api _api;
    private boolean imageSet = false;
    private File _image = null;

    @FXML
    public TextField edt_username;

    @FXML
    public TextField edt_prename;

    @FXML
    public TextField edt_lastname;

    @FXML
    public TextField edt_email;

    @FXML
    public PasswordField edt_password;

    @FXML
    public ImageView filePickerImage;



    @FXML
    public void registerUser(ActionEvent event)
    {
        event.consume();

        User newUser = new User();
        newUser.username = edt_username.textProperty().getValue().trim();
        newUser.prename = edt_prename.textProperty().getValue().trim();
        newUser.lastname = edt_lastname.textProperty().getValue().trim();
        newUser.email = edt_email.textProperty().getValue().trim();
        newUser.password = edt_password.textProperty().getValue();
        newUser.encodeImage(_image);
        newUser = new User(_api.registerUser(newUser));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registrierung erfolgreich");
        alert.setHeaderText("Nutzer angelegt");
        alert.setContentText(newUser.username + " erfolgreich angelegt");

        alert.showAndWait();
        backToLogin(event);
    }

    @FXML
    public void backToLogin(ActionEvent event)
    {
        event.consume();
        _mainStage.setScene(_goBackScene);
    }

    @FXML
    public void dragDropped(DragEvent event){
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            if( db.getFiles().size() > 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText("Mehrere Dateien ausgewählt!");
                alert.setContentText("Bitte nur ein Bild auswählen");
                alert.showAndWait();
            }else {
            setImage(db.getFiles().get(0));
            }
        }
        event.consume();
    }



    @FXML
    public void filePicker(MouseEvent event) throws IOException {
        setImage(null);
        event.consume();
    }

    public void setImage(File file) {
        if(imageSet){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Löschen");
            alert.setHeaderText("Es ist bereits eine Datei vorhanden!");
            alert.setContentText("Datei löschen?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    imageSet = false;
                    Image image = new Image(FlagBomb.class.getResourceAsStream("icons/upload.png"));
                    filePickerImage.setImage(image);
                    _image = null;
                }
            });

        }else{
            if(file == null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Profilbild wählen");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg")
                );
                file = fileChooser.showOpenDialog(_mainStage);
            }
            if (file != null) {
                if (isImage(file)) {
                    Image image = new Image(file.toURI().toString());
                    filePickerImage.setImage(image);
                    imageSet = true;
                    _image = file;
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler");
                    alert.setHeaderText("Die ausgewählte Datei ist kein Bild!");
                    alert.setContentText("Bitte neue Datei auswählen.");
                    alert.showAndWait();
                }
            }
        }
}

    public void setGoBackScene(Scene goBackScene)
    {
        _goBackScene = goBackScene;
    }

    public void setMainStage(Stage mainStage)
    {
        _mainStage = mainStage;
        Image image = new Image(FlagBomb.class.getResourceAsStream("icons/upload.png"));
        filePickerImage.setImage(image);
    }

    public void setApi(Api api)
    {
        _api = api;
    }

    private boolean isImage(File file) {

        boolean b = false;
        try {
            b = (ImageIO.read(file) != null);
        } catch (IOException e) {
            System.out.println("Image cannot be found");
        }
        return b;
    }
}
