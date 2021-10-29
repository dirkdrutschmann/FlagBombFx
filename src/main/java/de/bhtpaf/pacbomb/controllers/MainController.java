package de.bhtpaf.pacbomb.controllers;

import de.bhtpaf.pacbomb.PacBomb;
import de.bhtpaf.pacbomb.helper.Game;
import de.bhtpaf.pacbomb.helper.Util;
import de.bhtpaf.pacbomb.helper.classes.User;
import de.bhtpaf.pacbomb.services.Api;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.controlsfx.control.action.Action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainController {
    private Stage _mainStage;
    private Api _api;

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

        try
        {
            FXMLLoader loader = new FXMLLoader(PacBomb.class.getResource("overview.fxml"));
            Parent root = (Parent)loader.load();
            Scene scene = new Scene(root, 1000, 600);

            OverviewController controller = (OverviewController)loader.getController();
            controller.setMainStage(_mainStage);
            controller.setApi(_api);
            controller.setUser(loginUser);
            controller.init();

            edt_password.textProperty().set("");

            _mainStage.setScene(scene);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Util.showErrorMessageBox(e.getMessage());
        }
    }

    public void callRegistraionScene(ActionEvent event)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(PacBomb.class.getResource("registerUser.fxml"));
            Parent root = (Parent)loader.load();
            Scene scene = new Scene(root, 1000, 600);

            RegisterUserController controller = (RegisterUserController)loader.getController();
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
