package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterUserController
{
    private Scene _goBackScene;
    private Stage _mainStage;
    private Api _api;

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
    public void registerUser(ActionEvent event)
    {
        event.consume();

        User newUser = new User();
        newUser.username = edt_username.textProperty().getValue().trim();
        newUser.prename = edt_prename.textProperty().getValue().trim();
        newUser.lastname = edt_lastname.textProperty().getValue().trim();
        newUser.email = edt_email.textProperty().getValue().trim();
        newUser.password = edt_password.textProperty().getValue();

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

    public void setGoBackScene(Scene goBackScene)
    {
        _goBackScene = goBackScene;
    }

    public void setMainStage(Stage mainStage)
    {
        _mainStage = mainStage;
    }

    public void setApi(Api api)
    {
        _api = api;
    }
}
