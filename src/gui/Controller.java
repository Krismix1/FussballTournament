package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

    @FXML
    private TextField nameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField dateBirthInput;


    public void saveAction(ActionEvent actionEvent) {
        System.out.println("Hello ...");
        String name = nameInput.getText();
        String email = emailInput.getText();
        String birthday = dateBirthInput.getText();


        System.out.println("Name ->" + name + "<-");
        System.out.println("Email ->" + email + "<-");
        System.out.println("Birthday ->" + birthday + "<-");


        try {
            String sql = "INSERT INTO Players VALUES " +
                    "(NULL, '" + name + "', '" + email + "','" + birthday + "')";
            System.out.println(sql);

            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private TextField adminLogin;
    @FXML
    private PasswordField adminPassword;

    public void btnLoginAction(){
        System.out.println("hh");
        String admin = adminLogin.getText();
        String pass = adminPassword.getText();
        System.out.println("Admin ->" + admin + "<-");
        System.out.println("Password ->" + pass + "<-");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/mainScene.fxml"));
            Scene scene = new Scene(root, 300, 275);
            Stage adminWindow = new Stage();
            adminWindow.setScene(scene);
            adminWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnPlayersLoginAction(){
        System.out.println("Player Logged Inz");
    }

}


