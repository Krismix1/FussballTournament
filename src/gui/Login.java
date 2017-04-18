package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Chris on 13-Apr-17.
 */
public class Login {
    private static final String ADMIN_USERNAME = "antoniapinkdino";
    private static final String ADMIN_PASSWORD = "pinkpinkpink";

    /**
     * Checks the entered username and password for the administrator. If the information is correct,
     * it grants access to the administration part of the system.
     * @param username the admin account username
     * @param password the admin account password
     * @return true if information is correct, false otherwise
     */
    public boolean grantAccess(String username, String password) {
        return (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) || username.isEmpty();
    }


    @FXML
    private TextField adminLogin;
    @FXML
    private PasswordField adminPassword;

    /**
     * The action for the admin login button. Loads the mainScene.fxml file if the account information is correct,
     * otherwise displays an error to inform of wrong username or/and password.
     */
    @FXML
    private void btnLoginAction(){
        String admin = adminLogin.getText();
        String pass = adminPassword.getText();
        try {
            if (grantAccess(admin,pass)) {
                Parent root = FXMLLoader.load(getClass().getResource("/gui/mainScene.fxml"));
                Scene scene = new Scene(root, 900, 575);
                Main.mainStage.setScene(scene);
            } else {
                Alert wrongCredentials = new Alert(Alert.AlertType.ERROR);
                wrongCredentials.setTitle("Invalid login");
                wrongCredentials.setHeaderText("Invalid username or password.");
                wrongCredentials.setContentText(null);
                wrongCredentials.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The action for the players login button. Loads the playerView.fxml file.
     */
    @FXML
    private void btnPlayersLoginAction(){
        try {
            Parent playerRoot = FXMLLoader.load(getClass().getResource("/gui/playerView.fxml"));
            Scene playerScene = new Scene(playerRoot, 900, 575);
            Main.mainStage.setScene(playerScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
