package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.helper.interfaces.LogoutEventListener;
import de.bhtpaf.pacbomb.services.Api;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController implements LogoutEventListener
{
    private Stage _mainStage;
    private User _user;
    private final Api _api;

    @FXML
    public TextField edt_username;

    @FXML
    public PasswordField edt_password;

    @FXML
    public Button bt_login;

    @FXML
    public ImageView img_loading;

    public MainController()
    {
        _api = new Api("http://dirkdrutschmann.de:61338/api");
        //_api = new Api("http://localhost:61339/api");
    }

    @FXML
    public void loginUser(ActionEvent event)
    {
        event.consume();

        edt_username.setDisable(true);
        edt_password.setDisable(true);
        bt_login.setDisable(true);

        if (img_loading.getImage() == null)
        {
            img_loading.setImage(new Image(PacBomb.class.getResourceAsStream("loading.gif")));
        }

        img_loading.setVisible(true);

        Runnable login = () ->
        {
            String msg = "Nutzername und Passwort müssen angegeben werden!";
            User loginUser = null;

            if (!edt_username.textProperty().get().equals("") && !edt_password.textProperty().get().equals(""))
            {
                loginUser = new User();
                loginUser.username = edt_username.textProperty().get().trim();
                loginUser.password = edt_password.textProperty().get();

                loginUser = _api.loginUser(loginUser);

                if (loginUser == null || loginUser.jwtToken == null)
                {
                    msg = "Login fehlgeschlagen";
                }
                else
                {
                    loginUser.userImageBase64 = _api.getUserImage(loginUser);
                }
            }

            _user = loginUser;
            String finalMsg = msg;

            Platform.runLater(() -> {
                callOverviewScene(finalMsg);
            });
        };

        new Thread(login).start();
    }

    private void callOverviewScene(String msg)
    {
        edt_username.setDisable(false);
        edt_password.setDisable(false);
        bt_login.setDisable(false);
        img_loading.setVisible(false);

        if (_user == null)
        {
            Util.showErrorMessageBox(msg);

            edt_password.textProperty().set("");
            return;
        }

        _mainStage.setOnCloseRequest(ev -> {
            if (_user == null)
            {
                return;
            }

            if (_api.logoutUser(_user))
            {
                System.out.println("logged out");
            }
            else
            {
                System.out.println("logout failed");
            }
        });

        try {
            FXMLLoader loader = new FXMLLoader(PacBomb.class.getResource("overview.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 600);

            OverviewController controller = loader.getController();
            controller.setMainStage(_mainStage);
            controller.setApi(_api);
            controller.setUser(_user);
            controller.init();

            controller.addLogoutEventListener(this);

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

    @Override
    public void onUserLoggedOut(User user)
    {
        if (_user.id == user.id)
        {
            _user = null;
        }
    }
}
