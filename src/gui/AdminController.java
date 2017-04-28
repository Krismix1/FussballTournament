package gui;


import domain.Match;
import domain.Player;
import domain.Team;
import domain.Tournament;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class AdminController {

    ///////////////////////////////////////////////////REGISTER PLAYER TAB////////////////////////////////////////////////////////

    @FXML
    private TextField nameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private DatePicker dateBirthInput;
    @FXML
    private Button savePlayerBtn;

    /**
     * The save player button action. Will check the entered information for all required fields, valid email, if provided.
     * If succeeds, sends a request to the Tournament class to save the player to the database.
     */
    @FXML
    private void savePlayer(ActionEvent actionEvent) {
        String name = nameInput.getText();
        String email = emailInput.getText();
        if (email.length() == 0) {
            email = null;
        }
        savePlayerBtn.defaultButtonProperty().bind(savePlayerBtn.focusedProperty());

        if (name.isEmpty() || dateBirthInput.getValue() == null) {
            SceneManager.getInstance().displayError("Error Dialog", null, "Please enter player details!");
            return;
        }
        if (!validEmail(email)) {
            SceneManager.getInstance().displayError("Invalid email", null, "Invalid email format.");
            return;
        }
        if (dateBirthInput.getValue().compareTo(LocalDate.now()) >= 0) {
            SceneManager.getInstance().displayError("Wrong birthday date", null, "Enter a valid date.");
            return;
        }
        LocalDate birthday = dateBirthInput.getValue();
        if (Tournament.getInstance().registerPlayer(new Player(name, birthday, email))) {
            nameInput.clear();
            emailInput.clear();
            dateBirthInput.setValue(null);
            SceneManager.getInstance().displayInformation("Player saved!", null, "Player was saved!");
            showData();
        } else {
            SceneManager.getInstance().displayError("Player registration", null, "An error occurred. Please try again.");
        }
    }

    // Will check if email has the '@' character only one time
    // And if so, it checks if the string after '@' contains '.'
    private boolean validEmail(String email) {
        if (email != null) {
            String[] emailParts = email.split("@");
            return emailParts.length == 2 && emailParts[1].contains(".");
        }
        return true; // empty email
    }

    /**
     * The logout button action. Gets the user to the login scene of the program.
     */
    @FXML
    private void btnBackAction() {
        try {
            SceneManager.getInstance().loadLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableColumn<Player, String> nameColumn;
    @FXML
    private TableColumn<Player, String> emailColumn;
    @FXML
    private TableColumn<Player, String> dobColumn;
    @FXML
    private TableView<Player> playersTable;

    /**
     *
     */
    @FXML
    private void showData() {
        ObservableList<Player> players = FXCollections.observableArrayList(Tournament.getInstance().getPlayersFromDB());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        playersTable.setItems(players);
    }

    private Player selectedPlayer;

    @FXML
    private void selectPlayer() {
        selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            String nameTbl = selectedPlayer.getPlayerName();
            String emailTbl = selectedPlayer.getEmail();
            LocalDate dob = selectedPlayer.getDateOfBirth();
            nameInput.setText(nameTbl);
            emailInput.setText(emailTbl);
            dateBirthInput.setValue(dob);
        }
    }

    @FXML
    private void deletePlayer() { // TODO: 19.04.2017 add confirmation message where it states in which team the player is in
        if (!Tournament.getInstance().checkTournamentStarted()) {
            if (selectedPlayer != null) {
                if (SceneManager.getInstance().displayConfirmation("Oops!",
                        "Are you sure you want to delete player " + selectedPlayer.getPlayerName() + "?", null)) {
                    int id = selectedPlayer.getPlayerID();
                    Tournament.getInstance().deletePlayerFromDB(selectedPlayer);
                    showData();
                    nameInput.setText("");
                    emailInput.setText("");
                    dateBirthInput.setValue(null);
                    selectedPlayer = null;
                }
            } else {
                SceneManager.getInstance().displayError("Oops!", "No Player selected", "Please select player to delete!");
            }
        } else {
            SceneManager.getInstance().displayError("Delete player", null,
                    "Players can not be deleted when tournament is running");
        }
    }

    @FXML
    private void editPlayer() {
        if (selectedPlayer != null) {
            String name = nameInput.getText();
            String email = emailInput.getText();
            LocalDate dob = dateBirthInput.getValue();
            int id = selectedPlayer.getPlayerID();
            Tournament.getInstance().editPlayerDB(new Player(name, dob, email, id));
            nameInput.setText("");
            emailInput.setText("");
            dateBirthInput.setValue(null);
            selectedPlayer = null;
        } else {
            SceneManager.getInstance().displayError("Oops", "No player selected", "You need to select a player first!");
        }
        showData();
    }


    ///////////////////////////////////////////////////REGISTER TEAM TAB////////////////////////////////////////////////////////

    @FXML
    private ListView<Player> playersListView;
    @FXML
    private TableView<Team> teamTableView;
    @FXML
    private TableColumn<Team, String> teamNameColumn;
    @FXML
    private TableColumn<Team, Player> playerOneName;
    @FXML
    private TableColumn<Team, Player> playerTwoName;
    @FXML
    private TextField teamNameTextField;

    /**
     * This methods gets called when the selected changes to or from the 'Register Team' tab.
     * It gets and displays the players without a team and the teams that are registered in the database.
     */
    @FXML
    private void loadTeamsAndPlayers() {
        playersListView.setItems(FXCollections.observableArrayList(Tournament.getInstance().getPlayersWithoutTeam()));
        teamTableView.setItems(FXCollections.observableArrayList(Tournament.getInstance().getTeamsList()));

        addPropertiesForTeamTable();
    }

    // Helper method to add properties to the team TableView
    private void addPropertiesForTeamTable() {
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        playerOneName.setCellValueFactory(new PropertyValueFactory<>("firstPlayer"));
        playerTwoName.setCellValueFactory(new PropertyValueFactory<>("secondPlayer"));
    }

    private Player player1Selected; // stores the reference to the first player of the team which gets selected
    private Player player2Selected; // stores the reference to the second player of the team which gets selected
    @FXML
    private TextField player1TextField; // this field is non-editable so that user doesn't type in a random player name
    @FXML
    private TextField player2TextField; // this field is non-editable so that user doesn't type in a random player name

    /**
     * This method handles double click on player ListView.
     * When this methods gets called, in case of a double left click on a item,
     * it sets the player1Selected and player2Selected to the proper player reference.
     */
    @FXML
    private void playersListItemClicked(MouseEvent click) {
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


    /**
     * Saves a new team with the entered information by the user.
     * If team name is not provided, a team with default name will be saved.
     */
    @FXML
    private void registerTeam() {
        if (!Tournament.getInstance().checkTournamentStarted()) {
            String teamName = teamNameTextField.getText();
            try {
                Team t;
                if (teamName.isEmpty()) {
                    t = new Team(player1Selected, player2Selected);
                } else {
                    t = new Team(player1Selected, player2Selected, teamName);
                }
                Tournament.getInstance().registerTeam(t);

                loadTeamsAndPlayers();

                SceneManager.getInstance().displayInformation("Team creation", null, "Team created!");
                player1Selected = null;
                player2Selected = null;

                teamNameTextField.clear();
                player1TextField.clear();
                player2TextField.clear();
            } catch (SQLException e) {
                // Code #1062 defines Duplicate entry value for primary key
                // That said, the team name is already used.
                if (e.getErrorCode() == Tournament.PRIMARY_KEY_TAKEN_ERROR) {
                    SceneManager.getInstance().displayError("Error Dialog", null, "Team name is already used. Try a new name!");
                } else {
                    SceneManager.getInstance().displayError("Error Dialog", null, "Ooops, there was an error!\n Try again");
                }
                e.printStackTrace();
            } catch (NullPointerException nullPointer) {
                SceneManager.getInstance().displayError("Error Dialog", null, "Ooops, you need to select the players first!");
                nullPointer.printStackTrace();
            }
        } else {
            SceneManager.getInstance().displayError("Team registration" , null, "Teams can not be registered when tournament is running.");
        }
    }

    /**
     * Clears the first selected player. This method is needed because the TextField is not editable through
     * keyboard inputs.
     */
    @FXML
    private void btnClear() {
        player1TextField.clear();
        player1Selected = null;
    }

    /**
     * Clears the second selected player. This method is needed because the TextField is not editable through
     * keyboard inputs.
     */
    @FXML
    private void btnClear1() {
        player2TextField.clear();
        player2Selected = null;
    }

    /**
     * Method to handle the Start tournament button click.
     */
    @FXML
    private void startTournament() {
        if (Tournament.getInstance().checkTournamentStarted()) {
            SceneManager.getInstance().displayError("Tournament start", null, "Tournament has already started.");
        } else {
            Tournament.getInstance().startTournament();
            SceneManager.getInstance().displayInformation("Tournament start", null, "Tournament successfully started.");
        }
    }

    private Team selectedTeam;

    @FXML
    private void selectTeam() {
        selectedTeam = teamTableView.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            String teamName = selectedTeam.getTeamName();
            Player player1 = selectedTeam.getFirstPlayer();
            Player player2 = selectedTeam.getSecondPlayer();
            teamNameTextField.setText(teamName);
            player1TextField.setText(player1.getPlayerName());
            player2TextField.setText(player2.getPlayerName());
        }
    }

    @FXML
    private void deleteTeam() { // TODO: 19.04.2017 add confirmation message where it states in which team the player is in
        if (!Tournament.getInstance().checkTournamentStarted()) {
            if (selectedTeam != null) {
                if (SceneManager.getInstance().displayConfirmation("Oops!",
                        "Are you sure you want to delete team " + selectedTeam.getTeamName() + "?", null)) {
                    Tournament.getInstance().deleteTeam(selectedTeam);
                    loadTeamsAndPlayers();
                    teamNameTextField.setText("");
                    player1TextField.setText("");
                    player2TextField.setText("");
                    selectedTeam = null;
                }
            } else {
                SceneManager.getInstance().displayError("Oops!", "No Team selected", "Please select team to delete!");
            }
        } else {
            SceneManager.getInstance().displayError("Delete team", null, "Team cannot be deleted when tournament is running.");
        }
    }

    @FXML
    private void editTeam() {
        String teamName = selectedTeam.getTeamName();
        String name = teamNameTextField.getText();
        // when tournament is started,only players can be swapped
        // name can be changed only when tournament is not started
        if (!Tournament.getInstance().checkTournamentStarted() || teamName.equals(name)) {// if tournament is not started or team name remains the same
            Player p1 = selectedTeam.getFirstPlayer();
            Player p2 = selectedTeam.getSecondPlayer();
            if (player1Selected != null && p1.getPlayerID() != player1Selected.getPlayerID()) {
                p1 = player1Selected;
            }
            if (player2Selected != null && p2.getPlayerID() != player2Selected.getPlayerID()) {
                p2 = player2Selected;
            }
            Team team = new Team(p1, p2, name);
            try {
                Tournament.getInstance().editTeamDB(teamName, team);
                SceneManager.getInstance().displayInformation("Edit team", "Team information was successfully changed.", null);
            } catch (SQLException e) {
                // Code #1062 defines Duplicate entry value for primary key
                // That said, the team name is already used.
                if (e.getErrorCode() == Tournament.PRIMARY_KEY_TAKEN_ERROR) {
                    SceneManager.getInstance().displayError("Error Dialog", null, "Team name is already used. Try a new name!");
                } else {
                    SceneManager.getInstance().displayError("Error Dialog", null, "Oops, there was an error!\n Try again");
                }
                e.printStackTrace();
            }
            teamNameTextField.setText("");
            player1TextField.setText("");
            player2TextField.setText("");
            selectedTeam = null;
        } else {
            SceneManager.getInstance().displayError("Edit team", "Tournament is already started.",
                    "Changing team name after tournament was started is not allowed.\nOnly swapping players!");
        }
        loadTeamsAndPlayers();
    }

    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////

    @FXML
    TableView<Match> matchesScheduleTable;
    @FXML
    TableColumn<Match, Team> matchNameColumn;
    @FXML
    TableColumn<Match, Team> matchDateColumn;

    @FXML
    private void loadSchedule() {
        List<Match> matchList = Tournament.getInstance().getDueMatches();
        ObservableList<Match> data = FXCollections.observableArrayList(matchList);
        matchNameColumn.setCellValueFactory(new PropertyValueFactory<>("matchName"));
        matchDateColumn.setCellValueFactory(new PropertyValueFactory<>("matchDate"));
        matchesScheduleTable.setItems(data);
        selectedMatch = null;
        editMatchDatePicker.setValue(null);
    }

    private Match selectedMatch;

    @FXML
    private void btnDeleteMatch() {
        if (selectedMatch != null) {
            if (Tournament.getInstance().deleteMatch(selectedMatch)) {
                SceneManager.getInstance().displayInformation("Delete match", "Match was successfully deleted.", null);
                selectedMatch = null;
            } else {
                SceneManager.getInstance().displayError("Delete match", "An error occurred during match deletion. \n Please try again", null);
            }
        } else {
            SceneManager.getInstance().displayError("Delete match", "Please, first select a match to be deleted.", null);
        }
        loadSchedule();
    }

    /**
     * This method handles the mouse click on the Schedule table for the admin.
     */
    @FXML
    private void getMatchSelected() {
        selectedMatch = matchesScheduleTable.getSelectionModel().getSelectedItem();
        if (selectedMatch != null) {
            editMatchDatePicker.setValue(selectedMatch.getMatchDate());
        }
    }

    @FXML
    private DatePicker editMatchDatePicker;

    @FXML
    private void editMatch() {
        // TODO: 21-Apr-17 Check if date is >= today
        if (selectedMatch != null) {
            selectedMatch.setMatchDate(editMatchDatePicker.getValue());
            if (Tournament.getInstance().editMatchDate(selectedMatch)) {
                SceneManager.getInstance().displayInformation("Edit match date", "Match date was successfully changed", null);
                selectedMatch = null;
                editMatchDatePicker.setValue(null);
            } else {
                SceneManager.getInstance().displayError("Edit match date", "An error occurred during edit. \n Please try again.", null);
            }
        } else {
            SceneManager.getInstance().displayError("Edit match", "Please, first select a match to be edited.", null);
        }
        loadSchedule();
        //matchesScheduleTable.refresh(); // this seems to be needed
    }


    @FXML
    private TextField teamOneScore;
    @FXML
    private TextField teamTwoScore;

    @FXML
    private void addScores() {
        if (selectedMatch != null) {
            try {
                int score1 = Integer.parseInt(teamOneScore.getText());
                int score2 = Integer.parseInt(teamTwoScore.getText());
                if (score1 >= 0 && score2 >= 0) {
                    selectedMatch.setGoalsForTeamOne(score1);
                    selectedMatch.setGoalsForTeamTwo(score2);

                    if (score1 > score2) {
                        Tournament.getInstance().registerMatchPlayed(selectedMatch, selectedMatch.getTeamOne(), selectedMatch.getTeamTwo());
                    } else {
                        Tournament.getInstance().registerMatchPlayed(selectedMatch, selectedMatch.getTeamTwo(), selectedMatch.getTeamOne());
                    }
                    teamOneScore.setText("");
                    teamTwoScore.setText("");
                    selectedMatch = null;
                } else {
                    SceneManager.getInstance().displayWarning("Oops!", "Negative number entered!", "Please try again");
                }
            } catch (NumberFormatException e) {
                SceneManager.getInstance().displayError("Oops!", "The entered input is not a number!", null);
            }
            loadSchedule();
            //matchesResultsTable.refresh();
        } else {
            SceneManager.getInstance().displayError("Oops", "Please select a match first!", null);
        }
    }

    ///////////////////////////////////////////////////RESULTS TAB////////////////////////////////////////////////////////

    @FXML
    TableView<Match> matchesResultsTable;
    @FXML
    private TableColumn<Match, Team> teamOneColumn;
    @FXML
    private TableColumn<Match, Team> teamTwoColumn;
    @FXML
    private TableColumn<Match, Integer> teamOneScoreColumn;
    @FXML
    private TableColumn<Match, Integer> teamTwoScoreColumn;

    @FXML
    private void loadResults() {
        matchesResultsTable.setItems(FXCollections.observableArrayList(Tournament.getInstance().getMatchesResults()));

        teamOneColumn.setCellValueFactory(new PropertyValueFactory<>("teamOne"));
        teamTwoColumn.setCellValueFactory(new PropertyValueFactory<>("teamTwo"));
        teamOneScoreColumn.setCellValueFactory(new PropertyValueFactory<>("teamOneGoals"));
        teamTwoScoreColumn.setCellValueFactory(new PropertyValueFactory<>("teamTwoGoals"));
    }

    ///////////////////////////////////////////////////STANDINGS TAB////////////////////////////////////////////////////////

    @FXML
    private TableView<Team> teamsStandingTable;
    @FXML
    private TableColumn<Team, String> standingTeam;
    @FXML
    private TableColumn<Team, Player> standingPlayer1;
    @FXML
    private TableColumn<Team, Player> standingPlayer2;
    @FXML
    private TableColumn<Team, Integer> standingPoints;
    @FXML
    private TableColumn<Team, Integer> standingGoals;
    @FXML
    private TableColumn<Team, Integer> standingMatchPlayed;
    @FXML
    private TableColumn<Team, Integer> standingWins;
    @FXML
    private TableColumn<Team, Integer> standingLosses;

    @FXML
    private void loadStandings() {

        standingTeam.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        standingPlayer1.setCellValueFactory(new PropertyValueFactory<>("firstPlayer"));
        standingPlayer2.setCellValueFactory(new PropertyValueFactory<>("secondPlayer"));
        standingGoals.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));
        standingMatchPlayed.setCellValueFactory(new PropertyValueFactory<>("matchesPlayed"));
        standingWins.setCellValueFactory(new PropertyValueFactory<>("matchesWon"));
        standingLosses.setCellValueFactory(new PropertyValueFactory<>("matchesLost"));
        standingPoints.setCellValueFactory(new PropertyValueFactory<>("pointsScored")); // FIXME: 23-Apr-17 Change to matches won, they are the same

        teamsStandingTable.setItems(FXCollections.observableArrayList(Tournament.getInstance().getOrderedTeamList()));
    }

    @FXML
    private void finishTournament() {
        if (SceneManager.getInstance().displayConfirmation("End tournament", null, "Are you sure you want to end the tournament?" +
                "\nThis will delete all information about teams and matches")) {
            List<Team> winners = Tournament.getInstance().getOrderedTeamList();
            String teamNames = "";
            for (int i = 0; i < 3 && i < winners.size(); i++) {
                teamNames += (i + 1) + ". " + winners.get(i) + "\n";
            }
            if (Tournament.getInstance().endTournament()) {
                SceneManager.getInstance().displayInformation("End tournament", "Tournament successfully ended.\nTop 3 teams:", teamNames);
            } else {
                SceneManager.getInstance().displayError("End tournament.", null, "An error occurred.\nPlease try again.");
            }
        }
        loadStandings();
    }
}