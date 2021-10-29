package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private Stage _mainStage;
    private final Api _api;

    @FXML
    public TextField edt_username;

    @FXML
    public PasswordField edt_password;

    public MainController()
    {
        _api = new Api("http://dirkdrutschmann.de:61338/api");
    }

    @FXML
    public void loginUser(ActionEvent event)
    {
        event.consume();

        String msg = "Nutzername und Passwort müssen angegeben werden!";
        boolean loggedIn = false;
        User loginUser = new User();

        if (!edt_username.textProperty().get().equals("") && !edt_password.textProperty().get().equals(""))
        {
            loginUser.username = edt_username.textProperty().get().trim();
            loginUser.password = edt_password.textProperty().get();

            loginUser = _api.loginUser(loginUser);

            if (loginUser == null || loginUser.jwtToken == null)
            {
                msg = "Login fehlgeschlagen";
            }
            else
            {
                loggedIn = true;
            }
        }

        if (!loggedIn)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(msg);
            alert.showAndWait();

            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(PacBomb.class.getResource("overview.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 600);

            OverviewController controller = loader.getController();
            controller.setMainStage(_mainStage);
            controller.setApi(_api);
            controller.setUser(loginUser);
            controller.init();

            edt_password.textProperty().set("");

            _mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Util.showErrorMessageBox(e.getMessage());
        }
    }

    public void callRegistrationScene(ActionEvent event)
    {
        event.consume();

        try
        {
            FXMLLoader loader = new FXMLLoader(PacBomb.class.getResource("registerUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 600);

            RegisterUserController controller = loader.getController();
            controller.setMainStage(_mainStage);
            controller.setGoBackScene(_mainStage.getScene());
            controller.setApi(_api);

            _mainStage.setScene(scene);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Util.showErrorMessageBox(e.getMessage());
        }
    }

    public void setMainStage(Stage stage)
    {
        _mainStage = stage;
    }
}
