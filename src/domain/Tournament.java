package domain;

import sun.plugin.dom.exception.InvalidStateException;
import technicalservices.DBConnection;
import technicalservices.MatchCRUD;
import technicalservices.PlayerCRUD;
import technicalservices.TeamCRUD;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Tournament {

    private final PlayerCRUD playerTable = new PlayerCRUD();
    private final TeamCRUD teamTable = new TeamCRUD();
    private final MatchCRUD matchTable = new MatchCRUD();

    //private Map<String, Match> matchesMap = new HashMap<>();
    /**
     * Error code when trying to create a database with an already existing name
     */
    public static final int DATABASE_EXIST_ERROR_CODE = 1007;
    /**
     * Code #1062 defines Duplicate entry value for primary key.
     */
    public static final int PRIMARY_KEY_TAKEN_ERROR = 1062;

    // This block of code gets executed when this class gets loaded.
    //static {
    //    Tournament.getInstance().checkTournamentStarted();
    //}

    // Assuring that the class is a singleton
    private Tournament() {
    }

    private static Tournament instance;

    public static Tournament getInstance() {
        if (instance == null) {
            instance = new Tournament();
        }
        return instance;
    }

    /**
     * Starts the tournament, does all required actions like creating and registering matches
     */
    public void startTournament() {
        Map<String, Team> teamsMap = teamTable.readAllTeams();
        if (teamsMap.size() >= 2) {
            List<Match> matchList = createMatches(new ArrayList<>(teamsMap.values()));
            matchTable.saveMatchesToDB(matchList);
        }else{
            throw new InvalidStateException("You need at least 2 teams to start a tournament");
        }
    }

    public boolean endTournament() {
        //if (checkTournamentStarted()) {
        try {
            String sql = "TRUNCATE TABLE matches;";

            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);

            sql = "DELETE from teams;";
            stmt.executeUpdate(sql);
            con.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //}
        return false;
    }

    /**
     * Returns if the tournament has been started or not.
     *
     * @return true if tournament was started, false otherwise
     */
    public boolean checkTournamentStarted() {
        Map<String, Team> teamsMap = teamTable.readAllTeams();
        Map<String, Match> matchList = matchTable.readAllMatches(teamsMap);
        if (teamsMap.size() >= 2) { // we have at least 2 teams, that means there should be at least 1 match
            if (matchList.size() > 0) { // we have at least 1 match registered in the database
                return true;
            }
        }
        return false;
    }


    /**
     * Creates the matches for the tournament.
     * Each team is assigned to play a match against all other remaining teams.
     */
    private List<Match> createMatches(List<Team> teamList) {
        List<Match> matchList = new ArrayList<>();
        for (int i = 0; i < teamList.size() - 1; i++) {
            Team team1 = teamList.get(i);
            for (int j = i + 1; j < teamList.size(); j++) {
                Team team2 = teamList.get(j);
                String matchName = team1.getTeamName() + " vs " + team2.getTeamName();
                Match match = new Match(matchName, team1, team2);
                LocalDate matchDate = LocalDate.now();
                match.setMatchDate(matchDate);
                matchList.add(match);
            }
        }
        return matchList;
    }

    public List<Player> getPlayersFromDB() {
        return playerTable.getPlayersFromDB();
    }

    public boolean registerPlayer(Player player) {
        return playerTable.registerPlayer(player);
    }

    public void deletePlayerFromDB(Player player) {
        playerTable.deletePlayerFromDB(player);
    }

    public void editPlayerDB(Player player) {
        playerTable.editPlayerDB(player);
    }

    public List<Player> getPlayersWithoutTeam() {
        return playerTable.getPlayersWithoutTeam();
    }

    public Collection<Team> getTeamsList() {
        return teamTable.readAllTeams().values();
    }

    public List<Team> getOrderedTeamList() {
        return teamTable.getOrderedTeamList();
    }

    public void registerTeam(Team team) throws SQLException {
        teamTable.registerTeam(team);
    }

    public void deleteTeam(Team team) {
        teamTable.deleteTeamDB(team);
    }

    public void editTeamDB(String teamName, Team team) throws SQLException {
        teamTable.editTeamDB(teamName, team);
    }

    public List<Match> getDueMatches() {
        Map<String, Team> teamMap = teamTable.readAllTeams();
        return matchTable.getDueMatches(teamMap);
    }

    public boolean deleteMatch(Match match) {
        return matchTable.deleteMatch(match);
    }

    public boolean editMatchDate(Match match) {
        return matchTable.editMatchDate(match);
    }

    public void registerMatchPlayed(Match match, Team winner, Team loser) {
        int teamOneGoals = match.getTeamOneGoals();
        int teamTwoGoals = match.getTeamTwoGoals();
        winner.updateGoalDifference(Math.abs(teamOneGoals - teamTwoGoals));
        winner.incrementMatchesWon();
        loser.updateGoalDifference(Math.abs(teamTwoGoals - teamOneGoals) * (-1));
        loser.incrementMatchesLost();
        matchTable.registerMatchPlayed(match, winner, loser);
    }

    public List<Match> getMatchesResults() {
        Map<String, Team> teamMap = teamTable.readAllTeams();
        return matchTable.getMatchesResults(teamMap);
    }
}

