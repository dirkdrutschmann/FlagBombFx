package de.bhtpaf.flagbomb.controllers;

import de.bhtpaf.flagbomb.FlagBomb;
import de.bhtpaf.flagbomb.helper.Util;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UserController
{
    private Scene _previousScene;
    private Stage _mainStage;
    private Api _api;
    private User _user;

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

    public void saveUser(ActionEvent event)
    {
        event.consume();
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

    public void setUser(User user)
    {
        _user = user;

        dsp_username.textProperty().set(_user.username);
        edt_prename.textProperty().set(_user.prename);
        edt_lastname.textProperty().set(_user.lastname);
        edt_email.textProperty().set(_user.email);

        img_user.setImage(new Image(Util.getImageOfBase64String(_user.userImageBase64)));
        img_pane.setCenter(img_user);
    }

}
