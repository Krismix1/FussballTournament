package domain;

import technicalservices.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Tournament {
    private static Tournament instance;


    List<Match> matchList = new LinkedList<>();
    List<Team> teamList = new ArrayList<>();
    List<Player> playerList = new LinkedList<>();

    private Tournament() {}

    public static Tournament getInstance() {
        if (instance == null) {
            instance = new Tournament();
        }
        return instance;
    }

    /**
     * Adds player to the tournament.
     * @param p the player to be added.
     */
    public void registerPlayer(Player p) {
        playerList.add(p);
    }

    /**
     * Adds team to the tournament.
     * @param t the team to be added.
     */
    public void registerTeam(Team t) {
        teamList.add(t);
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
                matchList.add(match);
            }
        }
    }

    private void saveMatchesToDB() {
        Collections.shuffle(matchList);
        for (Match match : matchList) {
            Team team1 = match.getTeamOne();
            Team team2 = match.getTeamTwo();
            try {
                Connection con = DBConnection.getConnection();
                Statement stmt = con.createStatement();
                String sql = "INSERT INTO matches VALUES('" +match.getMatchName() + "', '" + team1.getTeamName() + "', '" + team2.getTeamName() + "', NULL);";
                stmt.execute(sql);
                con.close();
            }catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<Match> getMatchList() {
        return this.matchList;
    }

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
                String dob = rs.getString(3);
                String email = rs.getString(4);
                playerList.add(new Player(name, dob, email, id));
            }
            con.close();
            return playerList;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

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
                String p1Birthday = rs.getString(3);
                String p1Email = rs.getString(4);
                Player p1 = new Player(p1Name, p1Birthday, p1Email);

                rs.next();

                String p2Name = rs.getString(2);
                String p2Birthday = rs.getString(3);
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

    public void createAndSaveTeam(Team t) throws NullPointerException, SQLException {
        // TODO: 16-Apr-17 Check if team name already exists in the teams table 
        String sql = "INSERT INTO `teams` (`team_name`, `player_one_id`, `player_two_id`) " +
                "VALUES ('" + t.getTeamName() + "', '" + t.getFirstPlayer().getPlayerID() + "', '" + t.getSecondPlayer().getPlayerID() + "')";
        Connection con = DBConnection.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    private boolean isStarted = false;

    public boolean isStarted() {
        return isStarted;
    }

    public void startTournament() {
        isStarted = true;
        createMatches();
        saveMatchesToDB();
    }
}

