package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import technicalservices.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

    @FXML
    private TextField teamInput;
    @FXML
    private TextField goalInput;
    @FXML
    private TextField againstInput;
    @FXML
    private TextField pointInput;


    public void saveLeagueAction(ActionEvent actionEvent) {
        String team = teamInput.getText();
        String goals = goalInput.getText();
        String against = againstInput.getText();
        String points = pointInput.getText();


        System.out.println("Team ->" + team + "<-");
        System.out.println("Goals ->" + goals + "<-");
        System.out.println("Against ->" + against + "<-");
        System.out.println("Points ->" + points + "<-");


        try {
            String sql1 = "INSERT INTO League VALUES " +
                    "(NULL, '" + team + "', '" + goals + "', '" + against + "', '" + points + "')";
            System.out.println(sql1);

            Connection con1 = DBConnection.getConnection();
            Statement stmt1 = con1.createStatement();
            stmt1.executeUpdate(sql1);
            con1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void btnScoreAction(){
        System.out.println("Score Logged In");
    }

    @FXML
    private TextField nameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField dateBirthInput;


    public void saveAction(ActionEvent actionEvent) {
        String name = nameInput.getText();
        String email = emailInput.getText();
        String birthday = dateBirthInput.getText();


        System.out.println("Name ->" + name + "<-");
        System.out.println("Email ->" + email + "<-");
        System.out.println("Birthday ->" + birthday + "<-");


        try {
            String sql = "INSERT INTO Players VALUES " +
                    "(NULL, '" + name + "', '" + email + "', '" + birthday + "')";
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
        String admin = adminLogin.getText();
        String pass = adminPassword.getText();
        System.out.println("Admin -> " + admin + " <-");
        System.out.println("Password -> " + pass + " <-");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/mainScene.fxml"));
            Scene scene = new Scene(root, 300, 275);
            Main.mainStage.setScene(scene);
            //Stage adminStage = new Stage();
            //adminStage.setScene(scene);
            //adminStage.show();
            //Main.mainStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnPlayersLoginAction(){
        System.out.println("Player Logged In");
    }

}


