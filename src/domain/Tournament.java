package domain;

import technicalservices.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Tournament {

    private List<Match> matchList = new LinkedList<>();
    private List<Team> teamList = new ArrayList<>();
    private List<Player> playerList = new LinkedList<>();

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
     * Creates the matches for the tournament.
     * Each team is assigned to play a match against all other remaining teams.
     */
    private void createMatches() {
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
    }

    /**
     * Shuffles the matches created and insert each match in the database.
     * For each match, a new connection is created and then destroyed. Think of a better way to store them,
     * because this takes a lot of time. Also the shuffle thing is pointless because the DBMS reorders the data.
     */
    private void saveMatchesToDB() {
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
        try {
            List<Match> matches = new LinkedList<>(); // TODO: 19-Apr-17 LinkedList?
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM matches WHERE match_played = 0"; // Finds all matches that need to be played
            // 0 means it wasn't played it, 1 means it was played
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String matchName = resultSet.getString(1);
                LocalDate matchDate = resultSet.getDate(4).toLocalDate();
                Match match = new Match();
                match.setMatchName(matchName);
                match.setMatchDate(matchDate);
                matches.add(match);
            }
            con.close();
            return matches;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Match> getMatchesResults() {
        try {
            List<Match> matches = new LinkedList<>(); // TODO: 19-Apr-17 LinkedList?
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM matches WHERE match_played = 1"; // Finds all matches that were played
            // 0 means it wasn't played it, 1 means it was played
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String teamOneName = rs.getString(2);
                String teamTwoName = rs.getString(3);
                int teamOneGoals = rs.getInt(5);
                int teamTwoGoals = rs.getInt(6);

                Match match = new Match();
                match.setGoalsForTeamOne(teamOneGoals);
                match.setGoalsForTeamTwo(teamTwoGoals);
                match.setTeamOne(new Team(teamOneName));
                match.setTeamTwo(new Team(teamTwoName));

                matches.add(match);
            }
            con.close();
            return matches;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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

    /**
     * Reads and returns the list of teams from the database.
     *
     * @return the list with teams
     */
    public List<Team> getTeamsList() {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT team_name,name,birthday,email " +
                    "FROM teams, players " +
                    "where teams.player_one_id = players.player_id or " +
                    "teams.player_two_id = players.player_id;";
            ResultSet rs = stmt.executeQuery(sql);
            List<Team> teamList = new LinkedList<>();
            while (rs.next()) {
                String teamName = rs.getString(1);
                String p1Name = rs.getString(2);
                LocalDate p1Birthday = rs.getDate(3).toLocalDate();
                String p1Email = rs.getString(4);
                Player p1 = new Player(p1Name, p1Birthday, p1Email);

                rs.next();
                String p2Name = rs.getString(2);
                LocalDate p2Birthday = rs.getDate(3).toLocalDate();
                String p2Email = rs.getString(4);
                Player p2 = new Player(p2Name, p2Birthday, p2Email);
                //rs.getObject(1, Player.class);
                teamList.add(new Team(p1, p2, teamName));
            }
            con.close();
            this.teamList = teamList;
            return teamList;
        } catch (SQLException exception) {
            exception.printStackTrace();
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
            System.out.println("SQL statement is not executed!");
            System.out.println(e);
        }
    }

    public void editTeamDB(String s1, String s2) throws SQLException {

        Connection con = DBConnection.getConnection();
        String sql = "UPDATE teams SET `team_name` = ? WHERE team_name = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, s1);
        pstmt.setString(2, s2);
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
            //System.out.println("SQL statement is not executed!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean editMatch(Match oldMatch, LocalDate newDate) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE matches SET match_date = ? WHERE match_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(newDate));
            pstmt.setString(2, oldMatch.getMatchName());
            pstmt.executeUpdate();
            con.close();
            return true;
        } catch (SQLException e) {
            //System.out.println("SQL statement is not executed!");
            e.printStackTrace();
            return false;
        }
    }

    // Will store state of the tournament: false - not started, true - started.
    private boolean isStarted = false;

    /**
     * Returns if the tournament has been started or not.
     *
     * @return true if tournament was started, false otherwise
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Starts the tournament, does all required actions like creating and registering matches
     */
    public void startTournament() {
        isStarted = true;
        createMatches();
        saveMatchesToDB();
    }
}

