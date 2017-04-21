package gui;


import domain.Match;
import domain.Player;
import domain.Team;
import domain.Tournament;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import technicalservices.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Controller {

    ///////////////////////////////////////////////////REGISTER PLAYER TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////REGISTER PLAYER TAB////////////////////////////////////////////////////////
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
     * The save player button action. Will check the entered information for all required fields, valid email if provided.
     * If succeeds, sends a request to the Tournament class to save the player to the database.
     */
    @FXML
    private void savePlayer(ActionEvent actionEvent) {
        String name = nameInput.getText();
        String email = emailInput.getText();
        if (email.length() <= 0) {
            email = null;
        }
        savePlayerBtn.defaultButtonProperty().bind(savePlayerBtn.focusedProperty());

        if (name.isEmpty() || dateBirthInput.getValue() == null) {
            displayError("Error Dialog", null, "Please enter player details!");
            return;
        }
        if (!validEmail(email)) {
            displayError("Invalid email", null, "Invalid email format.");
            return;
        }
        if (dateBirthInput.getValue().compareTo(LocalDate.now()) >= 0) {
            displayError("Wrong birthday date", null, "Enter a valid date.");
            return;
        }
        LocalDate birthday = dateBirthInput.getValue();
        if (Tournament.getInstance().registerPlayer(new Player(name, birthday, email))) {
            nameInput.clear();
            emailInput.clear();
            dateBirthInput.setValue(null);
            displayInformation("Player saved!", null, "Player was saved!");
            showData();
        } else {
            displayError("Player registration", null, "An error occurred. Please try again.");
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
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
            Scene scene = new Scene(root, 900, 575);
            Main.mainStage.setScene(scene);
            Main.mainStage.show();
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
        if (selectedPlayer != null) {
            if (displayConfirmation("Oops!",
                    "Are you sure you want to delete player " + selectedPlayer.getPlayerName() + "?", null)) {
                int id = selectedPlayer.getPlayerID();
                Tournament.getInstance().deletePlayerFromDB(id);
                showData();
                nameInput.setText("");
                emailInput.setText("");
                dateBirthInput.setValue(null);
                selectedPlayer = null;
            }
        } else {
            displayError("Oops!", "No Player selected", "Please select player to delete!");
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
            displayError("Oops", "No player selected", "You need to select a player first!");
        }
        showData();
    }


    ///////////////////////////////////////////////////REGISTER TEAM TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////REGISTER TEAM TAB////////////////////////////////////////////////////////
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
     * Code #1062 defines Duplicate entry value for primary key.
     */
    private static final int PRIMARY_KEY_TAKEN_ERROR = 1062;

    /**
     * Saves a new team with the entered information by the user.
     * If team name is not provided, a team with default name will be saved.
     */
    @FXML
    private void registerTeam() {
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

            displayInformation("Team creation", null, "Team created!");
            player1Selected = null;
            player2Selected = null;

            teamNameTextField.clear();
            player1TextField.clear();
            player2TextField.clear();
        } catch (SQLException e) {
            // Code #1062 defines Duplicate entry value for primary key
            // That said, the team name is already used.
            if (e.getErrorCode() == PRIMARY_KEY_TAKEN_ERROR) {
                displayError("Error Dialog", null, "Team name is already used. Try a new name!");
            } else {
                displayError("Error Dialog", null, "Ooops, there was an error!\n Try again");
            }
            e.printStackTrace();
        } catch (NullPointerException nullPointer) {
            displayError("Error Dialog", null, "Ooops, you need to select the players first!");
            nullPointer.printStackTrace();
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
        if (Tournament.getInstance().isStarted()) {
            displayError("Tournament start", null, "Tournament has already started.");
        } else {
            Tournament.getInstance().startTournament();
            displayInformation("Tournament start", null, "Tournament successfully started.");
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
        if (selectedTeam != null) {
            if (displayConfirmation("Oops!",
                    "Are you sure you want to delete team " + selectedTeam.getTeamName() + "?", null)) {
                String id = selectedTeam.getTeamName();
                Tournament.getInstance().deleteTeamD(id);
                loadTeamsAndPlayers();
                teamNameTextField.setText("");
                player1TextField.setText("");
                player2TextField.setText("");
                selectedTeam = null;
            }
        } else {
            displayError("Oops!", "No Team selected", "Please select team to delete!");
        }
    }

    @FXML
    private void editTeam() {
        String teamName = selectedTeam.getTeamName();
        String name = teamNameTextField.getText();
        if (!name.equals(teamName)) {
            try {
                Tournament.getInstance().editTeamDB(name, teamName);
                displayInformation("Edit team", "Team information was successfully changed.", null);
            } catch (SQLException e) {
                // Code #1062 defines Duplicate entry value for primary key
                // That said, the team name is already used.
                if (e.getErrorCode() == PRIMARY_KEY_TAKEN_ERROR) {
                    displayError("Error Dialog", null, "Team name is already used. Try a new name!");
                } else {
                    displayError("Error Dialog", null, "Oops, there was an error!\n Try again");
                }
                e.printStackTrace();
            }

        } else {
            displayWarning("Oops", "Ops", "Team name should be unique");
        }
        teamNameTextField.setText("");
        player1TextField.setText("");
        player2TextField.setText("");
        selectedTeam = null;
        loadTeamsAndPlayers();

    }

    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////

    @FXML
    TableView<Match> matchesScheduleTable;
    @FXML
    TableColumn<Match, Team> adminMatchNameColumn;
    @FXML
    TableColumn<Match, Team> adminMatchDateColumn;


    @FXML
    private void adminLoadSchedule() {
        List<Match> matchList = Tournament.getInstance().getDueMatches();
        ObservableList<Match> data = FXCollections.observableArrayList(matchList);
        adminMatchNameColumn.setCellValueFactory(new PropertyValueFactory<>("matchName"));
        adminMatchDateColumn.setCellValueFactory(new PropertyValueFactory<>("matchDate"));
        matchesScheduleTable.setItems(data);
    }

    private Match selectedMatch;
    @FXML
    private void btnDeleteMatch() {
        if (selectedMatch != null) {
            if (Tournament.getInstance().deleteMatch(selectedMatch)) {
                displayInformation("Delete match", "Match was successfully deleted.", null);
                selectedMatch = null;
            } else {
                displayError("Delete match", "An error occurred during match deletion. \n Please try again", null);
            }
        } else {
            displayError("Delete match", "Please, first select a match to be deleted.", null);
        }
        adminLoadSchedule();
    }

    /**
     * This method handles the mouse click on the Schedule table for the admin.
     */
    @FXML
    private void adminGetMatchSelected() {
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
            if (Tournament.getInstance().editMatch(selectedMatch, editMatchDatePicker.getValue())) {
                displayInformation("Edit match date", "Match date was successfully changed", null);
                selectedMatch = null;
                editMatchDatePicker.setValue(null);
            } else {
                displayError("Edit match date", "An error occurred during edit. \n Please try again.", null);
            }
        } else {
            displayError("Edit match", "Please, first select a match to be edited.", null);
        }
        adminLoadSchedule();
    }


    ///////////////////////////////////////////////////ADMIN RESULT TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////RESULT TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////RESULT TAB////////////////////////////////////////////////////////
    @FXML
    TableView<Match> matchesAdminResultsTable;
    @FXML
    private TableColumn<Match, Team> teamOneColumn1;
    @FXML
    private TableColumn<Match, Team> teamTwoColumn2;
    @FXML
    private TableColumn<Match, Integer> teamOneScoreColumn1;
    @FXML
    private TableColumn<Match, Integer> teamTwoScoreColumn2;

    @FXML
    private void btnCreateResults() {
        matchesAdminResultsTable.setItems(FXCollections.observableArrayList(Tournament.getInstance().getMatchesResults()));

        teamOneColumn1.setCellValueFactory(new PropertyValueFactory<>("teamOne"));
        teamTwoColumn2.setCellValueFactory(new PropertyValueFactory<>("teamTwo"));
        teamOneScoreColumn1.setCellValueFactory(new PropertyValueFactory<>("teamOneGoals"));
        teamTwoScoreColumn2.setCellValueFactory(new PropertyValueFactory<>("teamTwoGoals"));

    }

    ///////////////////////////////////////////////////STANDINGS TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////STANDINGS TAB////////////////////////////////////////////////////////
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

        List<Team> statisticsList = Tournament.getInstance().getTeamsList();
        ObservableList<Team> stats = FXCollections.observableArrayList(statisticsList);

        standingTeam.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        standingPlayer1.setCellValueFactory(new PropertyValueFactory<>("firstPlayer"));
        standingPlayer2.setCellValueFactory(new PropertyValueFactory<>("secondPlayer"));
        standingPoints.setCellValueFactory(new PropertyValueFactory<>("pointsScored"));
        standingGoals.setCellValueFactory(new PropertyValueFactory<>("goalsFor"));
        standingMatchPlayed.setCellValueFactory(new PropertyValueFactory<>("matchesPlayed"));
        standingWins.setCellValueFactory(new PropertyValueFactory<>("matchesWon"));
        standingLosses.setCellValueFactory(new PropertyValueFactory<>("matchesLost"));

        teamsStandingTable.setItems(stats);
    }


    ///////////////////////////////////////////////////PLAYER VIEW////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////PLAYER VIEW////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////PLAYER VIEW////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////


    @FXML
    private TableView<Match> matchTable;
    @FXML
    TableColumn<Match, String> matchColumn;
    @FXML
    TableColumn<Match, String> dateColumn;

    /**
     * Gets and displays the tournament schedule of the matches that are left to be played.
     * This method gets called when the selected tab changes to or from the 'Schedule' tab
     */
    @FXML
    private void displayMatchSchedule() {
        Tournament tournament = Tournament.getInstance();

        List<Match> matchList = tournament.getDueMatches();
        ObservableList<Match> data = FXCollections.observableArrayList(matchList);
        matchColumn.setCellValueFactory(new PropertyValueFactory<>("matchName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("matchDate"));
        matchTable.setItems(data);
    }

    ///////////////////////////////////////////////////RESULT TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////RESULT TAB////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////RESULT TAB////////////////////////////////////////////////////////


    @FXML
    private TableView<Match> matchesResultsTable;
    @FXML
    private TableColumn<Match, Team> teamOneColumn;
    @FXML
    private TableColumn<Match, Team> teamTwoColumn;
    @FXML
    private TableColumn<Match, Integer> teamOneScoreColumn;
    @FXML
    private TableColumn<Match, Integer> teamTwoScoreColumn;

    @FXML
    private void showMatchesResult() {
        List<Match> matchList = Tournament.getInstance().getMatchesResults();
        ObservableList<Match> data = FXCollections.observableArrayList(matchList);
        matchesResultsTable.setItems(data);

        teamOneColumn.setCellValueFactory(new PropertyValueFactory<>("teamOne"));
        teamTwoColumn.setCellValueFactory(new PropertyValueFactory<>("teamTwo"));
        teamOneScoreColumn.setCellValueFactory(new PropertyValueFactory<>("teamOneGoals"));
        teamTwoScoreColumn.setCellValueFactory(new PropertyValueFactory<>("teamTwoGoals"));
    }


    ///////////////////////////////////////////////////HELPER METHODS////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////HELPER METHODS////////////////////////////////////////////////////////

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
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display an information alert with the given information.
     *
     * @param title   the title of the information window.
     * @param header  the message which will be showed in the header. If headers is not wanted,
     *                a null value can be sent as parameter.
     * @param content the message of the information window.
     */
    private void displayInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display a warning alert with the given information.
     *
     * @param title   the title of the warning window.
     * @param header  the message which will be showed in the header. If headers is not wanted,
     *                a null value can be sent as parameter.
     * @param content the message of the warning window.
     */
    private void displayWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean displayConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}