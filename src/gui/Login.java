package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Created by Chris on 13-Apr-17.
 */
public class Login {
    private static final String ADMIN_USERNAME = "antoniapinkdino";
    private static final String ADMIN_PASSWORD = "pinkpinkpink";

    public boolean grantAccess(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }


    @FXML
    private TextField adminLogin;
    @FXML
    private PasswordField adminPassword;

    @FXML
    private void btnLoginAction(){
        String admin = adminLogin.getText();
        String pass = adminPassword.getText();
        try {
            if (grantAccess(admin,pass)) {
                Parent root = FXMLLoader.load(getClass().getResource("/gui/mainScene.fxml"));
                Scene scene = new Scene(root, 300, 275);
                Main.mainStage.setScene(scene);
            } else {
                System.out.println("wrong username or password");
            }
            //Stage adminStage = new Stage();
            //adminStage.setScene(scene);
            //adminStage.show();
            //Main.mainStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnPlayersLoginAction(){
        System.out.println("Player Logged In");
    }
}
