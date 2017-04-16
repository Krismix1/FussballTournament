package gui;


import domain.Player;
import domain.Team;
import domain.Tournament;

import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import technicalservices.DBConnection;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

    /*@FXML
    private TextField team1Input;
    @FXML
    private TextField team2Input;
    @FXML
    private TextField dateInput;

    @FXML
    private void saveSchedule(ActionEvent actionEvent) {
        String team1 = team1Input.getText();
        String team2 = team2Input.getText();
        String date = dateInput.getText();

        System.out.println("Team1 ->" + team1 + "<-");
        System.out.println("Team2 ->" + team2 + "<-");
        System.out.println("Date->" + date + "<-");

        try {
            String sql2 = "INSERT INTO Schedule VALUES " +
                    "('" + team1 + "', '" + team2 + "', '" + date + "')";
            System.out.println(sql2);

            Connection con2 = DBConnection.getConnection();
            Statement stmt2 = con2.createStatement();
            stmt2.executeUpdate(sql2);
            con2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnScoreAction2() {
        System.out.println("Schedule Logged In");
    }*/


    /*@FXML
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
                    "('" + team + "', '" + goals + "', '" + against + "', '" + points + "')";
            System.out.println(sql1);

            Connection con1 = DBConnection.getConnection();
            Statement stmt1 = con1.createStatement();
            stmt1.executeUpdate(sql1);
            con1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnScoreAction() {
        System.out.println("Score Logged In");
    }*/


    @FXML
    private TextField nameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField dateBirthInput;


    @FXML
    private void saveAction(ActionEvent actionEvent) {
        String name = nameInput.getText();
        String email = emailInput.getText();
        String birthday = dateBirthInput.getText();

        try {
            String sql = "INSERT INTO players VALUES " +
                    "(NULL, '" + name + "', '" + email + "', '" + birthday + "')";

            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
            nameInput.setText("");
            emailInput.setText("");
            dateBirthInput.setText("");
            displayInformation("Player saved!", null, "Player was saved!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private ListView<Player> playersListView;
    @FXML
    private TextField player1TextField;
    @FXML
    private TextField player2TextField;

    private Player player1Selected;
    private Player player2Selected;
    @FXML
    private TableView<Team> teamTableView;
    @FXML
    private TableColumn<Team, String> teamNameColumn;
    @FXML
    private TableColumn<Team, String> playerOneName;
    @FXML
    private TableColumn<Team, String> playerTwoName;

    @FXML
    private void registerTeamTabChanged() {
        playersListView.setItems(FXCollections.observableArrayList(Tournament.getInstance().getPlayersWithoutTeam()));
        teamTableView.setItems(FXCollections.observableArrayList(Tournament.getInstance().getTeamsList()));

        addProperties();
    }

    private void addProperties() {
        playersListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2 && click.getButton().equals(MouseButton.PRIMARY)) {
                    Player p = playersListView.getSelectionModel()
                            .getSelectedItem();
                    if (p != null) {
                        if (player1TextField.getText().isEmpty()) {
                            player1Selected = p;
                            player1TextField.setText(p.getPlayerName());
                        } else if (player2TextField.getText().isEmpty()) {
                            player2TextField.setText(p.getPlayerName());
                            player2Selected = p;
                        }
                    } else System.out.println("select item");
                }
            }
        });

        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        playerOneName.setCellValueFactory(new PropertyValueFactory<>("firstPlayerName"));
        playerTwoName.setCellValueFactory(new PropertyValueFactory<>("secondPlayerName"));

    }

    @FXML
    private TextField teamNameTextField;

    @FXML
    private void registerTeam() {
        String teamName = teamNameTextField.getText();

        //Check for team name, empty player 1 and player 2

        try {
            Tournament.getInstance().createAndSaveTeam(teamName, player1Selected, player2Selected);

            registerTeamTabChanged();

            displayInformation("Team creation", null, "Team created!");
            player1Selected = null;
            player2Selected = null;

            teamNameTextField.setText("");
            player1TextField.setText("");
            player2TextField.setText("");
        } catch (SQLException e) {
            displayError("Error Dialog", null, "Ooops, there was an error!\n Try again");
            e.printStackTrace();
        } catch (NullPointerException nullPointer) {
            displayError("Error Dialog", null, "Ooops, you need to select the players first!");
            nullPointer.printStackTrace();
        }
    }

    /**
     * Display an error alert with the given information.
     *
     * @param title   the title of the error window.
     * @param header  the message which will be showed in the header. If headers is not wanted,
     *                a null value can be sent as parameter.
     * @param content the message of the error window.
     */
    private void displayError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private void displayInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}


