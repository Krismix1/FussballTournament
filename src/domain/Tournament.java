package domain;

import technicalservices.DBConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Tournament {

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
    static {
        Tournament.getInstance().checkTournamentStarted();
    }

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
        Map<String, Team> teamsMap = readAllTeams();
        List<Match> matchList = createMatches(new ArrayList<>(teamsMap.values()));
        saveMatchesToDB(matchList);
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
        Map<String, Team> teamsMap = readAllTeams();
        Map<String, Match> matchList = readAllMatches(teamsMap);
        if (teamsMap.size() >= 2) { // we have at least 2 teams, that means there should be at least 1 match
            if (matchList.size() > 0) { // we have at least 1 match registered in the database
                return true;
            }
        }
        return false;
    }

    /**
     * Adds player to the database.
     *
     * @param p the player to be saved.
     * @return true if player is successfully saved or false otherwise
     */
    public boolean registerPlayer(Player p) {
        try {
            String sql = "INSERT INTO players VALUES(NULL, '" + p.getPlayerName() + "',";
            if (p.getEmail() == null) {
                sql += " NULL, '" + p.getDateOfBirth() + "')";
            } else {
                sql += " '" + p.getEmail() + "', '" + p.getDateOfBirth() + "')";
            }

            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds and returns all the players that are not part of any team.
     *
     * @return the list with players without a team.
     */
    public List<Player> getPlayersWithoutTeam() {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            //String sql = "SELECT player_id,player.name,dob,email FROM `player`,team " +
            //        "WHERE not (`team`.`player_one_id` = player_id or `team`.`player_two_id` = player_id)"
            // Selects all players that don't have a team yet
            String sql = "SELECT player_id,players.name,birthday,email from players WHERE player_id not in" +
                    "( SELECT player_id FROM `players`,teams WHERE `teams`.`player_one_id` = player_id or " +
                    "`teams`.`player_two_id` = player_id)";
            ResultSet rs = stmt.executeQuery(sql);
            List<Player> playerList = new LinkedList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                LocalDate dob = rs.getDate(3).toLocalDate();
                String email = rs.getString(4);
                playerList.add(new Player(name, dob, email, id));
            }
            con.close();
            return playerList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Player> getPlayersFromDB() {
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
                LocalDate pBirthday = rs.getDate("birthday").toLocalDate();
                playerList.add(new Player(name, pBirthday, email, id));
            }
            con.close();
            return playerList;
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
    }

    public void deletePlayerFromDB(int id) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM players WHERE player_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            con.close();

        } catch (SQLException e) {
            //System.out.println("SQL statement is not executed!");
            System.out.println(e);
        }
    }

    public void editPlayerDB(Player p) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE players SET `name` = ?,  email = ?, birthday = ? WHERE player_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, p.getPlayerName());
            pstmt.setString(2, p.getEmail());
            pstmt.setDate(3, Date.valueOf(p.getDateOfBirth()));
            pstmt.setInt(4, p.getPlayerID());
            pstmt.executeUpdate();

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Team> readAllTeams() {
        Map<String, Team> teamsMap = new HashMap<>();
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT team_name,wins,losses,goal_difference,name,birthday,email,player_id " +
                    "FROM teams, players " +
                    "WHERE teams.player_one_id = players.player_id or " +
                    "teams.player_two_id = players.player_id;";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String teamName = rs.getString(1);
                String p1Name = rs.getString(5);
                LocalDate p1Birthday = rs.getDate(6).toLocalDate();
                String p1Email = rs.getString(7);
                int p1ID = rs.getInt(8);
                Player p1 = new Player(p1Name, p1Birthday, p1Email, p1ID);

                rs.next();
                String p2Name = rs.getString(5);
                LocalDate p2Birthday = rs.getDate(6).toLocalDate();
                String p2Email = rs.getString(7);
                int p2ID = rs.getInt(8);
                Player p2 = new Player(p2Name, p2Birthday, p2Email, p2ID);

                Team t = new Team(p1, p2, teamName);
                t.setMatchesWon(rs.getInt(2));
                t.setMatchesLost(rs.getInt(3));
                t.setGoalDifference(rs.getInt(4));

                teamsMap.put(teamName, t);
            }
            con.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return teamsMap;
    }

    /**
     * Saves team to the database.
     *
     * @param t the team to be added.
     */
    public void registerTeam(Team t) throws NullPointerException, SQLException {
        String sql = "INSERT INTO `teams` (`team_name`, `player_one_id`, `player_two_id`) " +
                "VALUES ('" + t.getTeamName() + "', '" + t.getFirstPlayer().getPlayerID() + "', '" + t.getSecondPlayer().getPlayerID() + "')";
        Connection con = DBConnection.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    /**
     * Reads and returns the list of teams from the database.
     *
     * @return the list with teams
     */
    public Collection<Team> getTeamsList() {
        return readAllTeams().values();
    }

    /**
     * Reads all the teams from the database and returns a list with teams ordered descending by wins, then goal difference
     * and afterwards, if any matches, sorted ascending by losses.
     * @return sorted list of teams
     */
    public List<Team> getOrderedTeamList() {
        Map<String, Team> teamsMap = readAllTeams();
        List<Team> teams = new ArrayList<>(teamsMap.size());
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT team_name " +
                    "FROM teams,players " +
                    "WHERE teams.player_one_id = players.player_id or " +
                    "teams.player_two_id = players.player_id " +
                    "ORDER BY wins DESC, goal_difference DESC, losses ASC";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String teamName = rs.getString(1);
                teams.add(teamsMap.get(teamName));
                rs.next();
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }

    public void editTeamDB(String teamName, Team team) throws SQLException {
        Connection con = DBConnection.getConnection();
        String sql = "UPDATE teams SET `team_name` = ?, player_one_id = ?, player_two_id = ? WHERE team_name = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, team.getTeamName());
        pstmt.setInt(2, team.getFirstPlayer().getPlayerID());
        pstmt.setInt(3, team.getSecondPlayer().getPlayerID());
        pstmt.setString(4, teamName);
        pstmt.executeUpdate();

        con.close();
    }

    public void deleteTeamD(String id) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM teams WHERE team_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            con.close();

        } catch (SQLException e) {
            //System.out.println("SQL statement is not executed!");
            e.printStackTrace();
        }
    }

    private Map<String, Match> readAllMatches(Map<String, Team> teamsMap) {
        Map<String, Match> matchesMap = new HashMap<>();
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM matches";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String matchName = rs.getString(1);
                String teamOneName = rs.getString(2);
                String teamTwoName = rs.getString(3);
                LocalDate matchDate = rs.getDate(4).toLocalDate();
                int teamOneGoals = rs.getInt(5);
                int teamTwoGoals = rs.getInt(6);


//                if (matchesMap.containsKey(matchName)) { // If the map on the local machine contains the match, just update
//                    // the info, otherwise add it to the map
//                    Match oldMatchInfo = matchesMap.get(matchName);
//                    oldMatchInfo.setMatchDate(matchDate);
//                    oldMatchInfo.setGoalsForTeamOne(teamOneGoals);
//                    oldMatchInfo.setGoalsForTeamTwo(teamTwoGoals);
//                    Team teamOne = teamsMap.get(teamOneName);
//                    Team teamTwo = teamsMap.get(teamTwoName);
//                    oldMatchInfo.setTeamOne(teamOne);
//                    oldMatchInfo.setTeamTwo(teamTwo);
//                } else {

                Team teamOne = teamsMap.get(teamOneName);
                Team teamTwo = teamsMap.get(teamTwoName);

                Match match = new Match(matchName, teamOne, teamTwo, teamOneGoals, teamTwoGoals);
                match.setMatchDate(matchDate);

                matchesMap.put(matchName, match);
                //}
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchesMap;
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

    /**
     * Shuffles the matches created and insert each match in the database.
     * For each match, a new connection is created and then destroyed. Think of a better way to store them,
     * because this takes a lot of time. Also the shuffle thing is pointless because the DBMS reorders the data.
     */
    private void saveMatchesToDB(List<Match> matchList) {
        Collections.shuffle(matchList);
        for (Match match : matchList) {
            Team team1 = match.getTeamOne();
            Team team2 = match.getTeamTwo();
            try {
                Connection con = DBConnection.getConnection();
                Statement stmt = con.createStatement();
                String sql = "INSERT INTO matches VALUES('" + match.getMatchName() + "', '" + team1.getTeamName() +
                        "', '" + team2.getTeamName() + "', '" + match.getMatchDate() + "'," + match.getTeamOneGoals() +
                        ", " + match.getTeamTwoGoals() + ",FALSE );";
                stmt.execute(sql);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the list of the matches for the tournament
     *
     * @return the list with the matches
     */
    public List<Match> getDueMatches() {
        Map<String, Match> matchesMap = readAllMatches(readAllTeams());
        List<Match> matches = new ArrayList<>(); // TODO: 19-Apr-17 ArrayList?
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT match_name FROM matches WHERE match_played = 0"; // Finds all matches that need to be played
            // 0 means it wasn't played it, 1 means it was played
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String matchName = resultSet.getString(1);

                Match match = matchesMap.get(matchName);

                matches.add(match);
            }
            con.close();
            return matches;
        } catch (SQLException e) {
            e.printStackTrace();
            return matches;
        }
    }

    public List<Match> getMatchesResults() {
        Map<String, Match> matchesMap = readAllMatches(readAllTeams());
        List<Match> matches = new ArrayList<>(); // TODO: 19-Apr-17 ArrayList?
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT match_name FROM matches WHERE match_played = 1"; // Finds all matches that were played
            // 0 means it wasn't played it, 1 means it was played
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String matchName = rs.getString(1);

                Match match = matchesMap.get(matchName);

                matches.add(match);
            }
            con.close();
            return matches;
        } catch (SQLException e) {
            e.printStackTrace();
            return matches;
        }
    }

    public boolean deleteMatch(Match match) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM matches WHERE match_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, match.getMatchName());
            pstmt.executeUpdate();
            con.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editMatchDate(Match match, LocalDate newDate) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE matches SET match_date = ? WHERE match_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(newDate));
            pstmt.setString(2, match.getMatchName());
            pstmt.executeUpdate();
            con.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerMatchPlayed(Match match, Team winner, Team loser) {
        int teamOneGoals = match.getTeamOneGoals();
        int teamTwoGoals = match.getTeamTwoGoals();
        String winnerName = winner.getTeamName();
        String loserName = loser.getTeamName();

        winner.updateGoalDifference(Math.abs(teamOneGoals - teamTwoGoals));
        winner.incrementMatchesWon();
        loser.updateGoalDifference(Math.abs(teamTwoGoals - teamOneGoals) * (-1));
        loser.incrementMatchesLost();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE matches SET `team_one_goals` = ?, `team_two_goals` = ?, `match_played` = ? WHERE match_name = ?;";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, teamOneGoals);
            pstmt.setInt(2, teamTwoGoals);
            pstmt.setInt(3, 1);
            pstmt.setString(4, match.getMatchName());
            pstmt.executeUpdate();

            sql = "UPDATE teams SET `wins` = ?, `losses` = ?, `goal_difference` = ? WHERE team_name = ?;";
            PreparedStatement pstmt1 = con.prepareStatement(sql);
            pstmt1.setInt(1, winner.getMatchesWon());
            pstmt1.setInt(2, winner.getMatchesLost());
            pstmt1.setInt(3, winner.getGoalDifference());
            pstmt1.setString(4, winnerName);
            pstmt1.executeUpdate();

            sql = "UPDATE teams SET `wins` = ?, `losses` = ?, `goal_difference` = ? WHERE team_name = ?";
            PreparedStatement pstmt2 = con.prepareStatement(sql);
            pstmt2.setInt(1, loser.getMatchesWon());
            pstmt2.setInt(2, loser.getMatchesLost());
            pstmt2.setInt(3, loser.getGoalDifference());
            pstmt2.setString(4, loserName);
            pstmt2.executeUpdate();


            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

