package de.bhtpaf.flagbomb.controllers;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.Util;
import de.bhtpaf.flagbomb.helper.classes.JWT;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.UserChangedListener;
import de.bhtpaf.flagbomb.helper.responses.StdResponse;
import de.bhtpaf.flagbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserController
{
    private Scene _previousScene;
    private Stage _mainStage;
    private Api _api;
    private User _user;
    private File _image;
    private boolean _imageSet = false;

    private List<UserChangedListener> _userChangedListeners = new ArrayList<>();

    private final String RED_BORDER_STYLE = "-fx-text-box-border: red;";

    @FXML
    public TextField dsp_username;

    @FXML
    public TextField edt_prename;

    @FXML
    public TextField edt_lastname;

    @FXML
    public TextField edt_email;

    @FXML
    public TextField edt_password;

    @FXML
    public TextField edt_password_confirm;

    @FXML
    public BorderPane img_pane;

    @FXML
    public ImageView img_user;

    public void backToOverview(ActionEvent event)
    {
        event.consume();
        _mainStage.setScene(_previousScene);
    }

    public void saveUser(ActionEvent event) throws IOException
    {
        event.consume();

        edt_email.setStyle("");
        edt_password.setStyle("");
        edt_password_confirm.setStyle("");

        if (!_user.prename.equals(edt_prename.textProperty().get().trim()))
        {
            _user.prename = edt_prename.textProperty().get().trim();
        }

        if (!_user.lastname.equals(edt_lastname.textProperty().get().trim()))
        {
            _user.lastname = edt_lastname.textProperty().get().trim();
        }

        if (edt_email.textProperty().get().trim().equals(""))
        {
            edt_email.setStyle(RED_BORDER_STYLE);
            return;
        }
        else if (!_user.email.equals(edt_email.textProperty().get().trim()))
        {
            _user.email = edt_email.textProperty().get().trim();
        }

        if (!edt_password.textProperty().get().trim().equals(""))
        {
            if (!edt_password.textProperty().get().trim().equals(edt_password_confirm.textProperty().get().trim()))
            {
                edt_password.setStyle(RED_BORDER_STYLE);
                edt_password_confirm.setStyle(RED_BORDER_STYLE);
                return;
            }

            _user.password = edt_password.textProperty().get().trim();
        }

        if (_image != null)
        {
            _user.userImageBase64 = Util.getBase64StringOfImage(_image);
        }

        StdResponse resp = _api.setUser(_user);
        if (!resp.success)
        {
            Util.showErrorMessageBox(resp.message);
            return;
        }

        for (UserChangedListener listener : _userChangedListeners)
        {
            listener.onUserChanged(_user);
        }

        backToOverview(event);
    }



    public void setApi(Api api)
    {
        _api = api;
    }

    public void setMainStage(Stage mainStage)
    {
        _mainStage = mainStage;
        _previousScene = _mainStage.getScene();
        Image image = new Image(FlagBomb.class.getResourceAsStream("icons/upload.png"));
        img_user.setImage(image);
    }

    public void filePicker(MouseEvent event)
    {
        event.consume();
        setImage(null);
    }

    public void setImage(File file)
    {
        if(_imageSet)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Löschen");
            alert.setHeaderText("Es ist bereits eine Datei vorhanden!");
            alert.setContentText("Datei löschen?");
            alert.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    _imageSet = false;
                    Image image = new Image(FlagBomb.class.getResourceAsStream("icons/upload.png"));
                    img_user.setImage(image);
                    _image = null;
                }
            });

        }
        else
        {
            if (file == null)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Profilbild wählen");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));
                file = fileChooser.showOpenDialog(_mainStage);
            }

            if (file != null)
            {
                if (Util.isImage(file))
                {
                    Image image = new Image(file.toURI().toString());
                    img_user.setImage(image);
                    _imageSet = true;
                    _image = file;
                }
                else
                {
                    Util.showErrorMessageBox("Die ausgewählte Datei ist kein Bild!");
                }
            }
        }
    }

    public void setUser(User user)
    {
        _user = new User(user);
        _user.jwtToken = new JWT();
        _user.jwtToken.token = user.jwtToken.token;

        dsp_username.textProperty().set(_user.username);
        edt_prename.textProperty().set(_user.prename);
        edt_lastname.textProperty().set(_user.lastname);
        edt_email.textProperty().set(_user.email);

        img_user.setImage(new Image(Util.getImageOfBase64String(_user.userImageBase64)));
        img_pane.setCenter(img_user);
    }

    public void addUserChangedListener(UserChangedListener listener)
    {
        _userChangedListeners.add(listener);
    }

}
