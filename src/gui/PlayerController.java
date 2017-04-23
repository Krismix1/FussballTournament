package gui;

import domain.Match;
import domain.Player;
import domain.Team;
import domain.Tournament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chris on 23-Apr-17.
 */
public class PlayerController {

    ///////////////////////////////////////////////////SCHEDULE TAB////////////////////////////////////////////////////////

    @FXML
    private TableView<Match> matchTable;
    @FXML
    private TableColumn<Match, String> matchColumn;
    @FXML
    private TableColumn<Match, String> dateColumn;

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

    @FXML
    private void btnBackAction() {
        try {
            SceneManager.getInstance().loadLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        Collection<Team> statisticsList = Tournament.getInstance().getTeamsList();
        ObservableList<Team> stats = FXCollections.observableArrayList(statisticsList);

        standingTeam.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        standingPlayer1.setCellValueFactory(new PropertyValueFactory<>("firstPlayer"));
        standingPlayer2.setCellValueFactory(new PropertyValueFactory<>("secondPlayer"));
        standingPoints.setCellValueFactory(new PropertyValueFactory<>("pointsScored")); // FIXME: 23-Apr-17 Change to matches won, they are the same
        standingGoals.setCellValueFactory(new PropertyValueFactory<>("goalsFor"));
        standingMatchPlayed.setCellValueFactory(new PropertyValueFactory<>("matchesPlayed"));
        standingWins.setCellValueFactory(new PropertyValueFactory<>("matchesWon"));
        standingLosses.setCellValueFactory(new PropertyValueFactory<>("matchesLost"));

        teamsStandingTable.setItems(stats);
    }

}
