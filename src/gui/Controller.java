package gui;


import com.sun.prism.impl.Disposer;
import domain.Match;
import domain.Player;
import domain.Team;
import domain.Tournament;

import javafx.stage.Modality;
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
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
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
        String birthday = dateBirthInput.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
    private void showData(){
        List<Player> playerList = new LinkedList<>();
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM players ";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("player_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String pBirthday = rs.getString("birthday");
                playerList.add(new Player(name, pBirthday, email, id));
            }

            con.close();
        } catch(SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        ObservableList<Player> players = FXCollections.observableArrayList(playerList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        playersTable.setItems(players);
    }

    private Player selectedPlayer;

    @FXML
    private void selectPlayer() {
        selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if(selectedPlayer != null) {
            String nameTbl = selectedPlayer.getPlayerName();
            String emailTbl = selectedPlayer.getEmail();
            String dob = selectedPlayer.getDateOfBirth();
            nameInput.setText(nameTbl);
            emailInput.setText(emailTbl);
            dateBirthInput.setValue(LocalDate.parse(String.valueOf(dob)));
        }
    }

    @FXML
    private void deletePlayer(){ // TODO: 19.04.2017 add confirmation message where it states in which team the player is in
        if(selectedPlayer != null) {
            if(displayConfirmation("Oops!",
                    "Are you sure you want to delete " + selectedPlayer.getPlayerName() + "?", "Player will be deleted")) {

                int id = selectedPlayer.getPlayerID();
                try {
                    Connection con = DBConnection.getConnection();
                    String sql = "DELETE FROM players WHERE player_id = ?";
                    PreparedStatement pstmt = con.prepareStatement(sql);
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                    con.close();
                    showData();

                } catch (SQLException e) {
                    //System.out.println("SQL statement is not executed!");
                    System.out.println(e);
                }
                nameInput.setText("");
                emailInput.setText("");
                dateBirthInput.setValue(null);
                selectedPlayer = null;
            }
        }else{
            displayInformation("Oops!", "No Player selected", "Please select player to delete!");
        }
    }

    @FXML
    private void editPlayer(){

        String name = nameInput.getText();
        String email = emailInput.getText();
        LocalDate dob = dateBirthInput.getValue();

        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE players SET `name` = ?,  email = ?, birthday = ? WHERE player_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setDate(3, Date.valueOf(dob));
            pstmt.setInt(4, selectedPlayer.getPlayerID());
            pstmt.executeUpdate();

            con.close();
        } catch (SQLException e) {
            System.out.println("SQL statement is not executed!");
            System.out.println(e);
        }
        nameInput.setText("");
        emailInput.setText("");
        dateBirthInput.setValue(null);
        selectedPlayer = null;
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
    private TableColumn<Team, String> playerOneName;
    @FXML
    private TableColumn<Team, String> playerTwoName;
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
        playerOneName.setCellValueFactory(new PropertyValueFactory<>("firstPlayerName"));
        playerTwoName.setCellValueFactory(new PropertyValueFactory<>("secondPlayerName"));
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
        if(selectedTeam != null) {
            String teamName = selectedTeam.getTeamName();
            String player1 = selectedTeam.getFirstPlayerName();
            String player2 = selectedTeam.getSecondPlayerName();
            teamNameTextField.setText(teamName);
            player1TextField.setText(player1);
            player2TextField.setText(player2);
        }
    }

    @FXML
    private void deleteTeam(){ // TODO: 19.04.2017 add confirmation message where it states in which team the player is in
        if(selectedTeam != null) {
            if(displayConfirmation("Oops!",
                    "Are you sure you want to delete " + selectedTeam.getTeamName() + "?", "Team will be deleted")) {

                String id = selectedTeam.getTeamName();
                try {
                    Connection con = DBConnection.getConnection();
                    String sql = "DELETE FROM teams WHERE team_name = ?";
                    PreparedStatement pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, id);
                    pstmt.executeUpdate();
                    con.close();
                    loadTeamsAndPlayers();

                } catch (SQLException e) {
                    //System.out.println("SQL statement is not executed!");
                    System.out.println(e);
                }
                teamNameTextField.setText("");
                player1TextField.setText("");
                player2TextField.setText("");
                selectedTeam = null;
            }
        }else{
            displayInformation("Oops!", "No Team selected", "Please select team to delete!");
        }
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
    TableColumn<Match, String> dateColumn; // FIXME: 14.04.2017 how to store date?

    /**
     * Gets and displays the tournament schedule of the matches that are left to be played.
     * This method gets called when the selected tab changes to or from the 'Schedule' tab
     */
    @FXML
    private void displayMatchSchedule() {
        Tournament tournament = Tournament.getInstance();

        List<Match> matchList = tournament.getMatchList();
        ObservableList<Match> data = FXCollections.observableArrayList(matchList);
        matchColumn.setCellValueFactory(new PropertyValueFactory<>("matchName"));
        matchTable.setItems(data);
    }


    ///////////////////////////////////////////////////HELPER METHODS////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////HELPER METHODS////////////////////////////////////////////////////////

    /**
     * Display an error alert with the given information.
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
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        }else{
            return false;
        }
    }
}