package gui;


import domain.Match;
import domain.Player;
import domain.Team;
import domain.Tournament;

import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import technicalservices.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Controller {

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
        if (name.equalsIgnoreCase(""))
        {
            System.out.println("Enter player details and proceed!!!!!");
        }else
        {
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
    }

    @FXML
    private void btnBackAction(){
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
            Scene scene = new Scene(root, 900, 575);
            Main.mainStage.setScene(scene);
            Main.mainStage.show();
        } catch (IOException e) {
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

    @FXML
    private TableView matchTable;
    @FXML
    TableColumn<Match, String> matchColumn;
    @FXML
    TableColumn<Match, String> dateColumn; // FIXME: 14.04.2017 how to store date?

    public void schedule() {
        Tournament tournament = Tournament.getInstance();
        tournament.getTeamsList();

        tournament.createMatches();
        List<Match> matchList = tournament.getMatchList();
        ObservableList<Match> data = FXCollections.observableArrayList(matchList);
        matchColumn.setCellValueFactory(new PropertyValueFactory<>("matchName"));
        matchTable.setItems(data);
    }

}


