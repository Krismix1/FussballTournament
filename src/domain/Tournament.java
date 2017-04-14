package domain;

import technicalservices.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Tournament {

    List<Match> matchList = new LinkedList<>();
    List<Team> teamList = new ArrayList<>();
    List<Player> playerList = new LinkedList<>();

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
    public void createMatches() {
        for (int i = 0; i < teamList.size() - 1; i++) {
            Team team1 = teamList.get(i);
            for (int j = i + 1; j < teamList.size(); j++) {
                Team team2 = teamList.get(j);
                String matchName = team1.getTeamName() + " vs " + team2.getTeamName();
                matchList.add(new Match(matchName, team1, team2));
            }
        }
    }

    public static List<Player> getPlayersWithoutTeam() {
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

    public static List<Team> getTeamsList() {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            String sql = "SELECT team_name, player_one_id, player_two_id";
            ResultSet rs = stmt.executeQuery(sql);
            List<Team> teamList = new LinkedList<>();
            while (rs.next()) {


               // teamList.add(new Team());
            }
            con.close();
            return teamList;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void createAndSaveTeam(String teamName, Player p1, Player p2) throws NullPointerException, SQLException{
        String sql = "INSERT INTO `teams` (`team_name`, `player_one_id`, `player_two_id`) " +
                "VALUES ('" + teamName + "', '" + p1.getPlayerID() + "', '" + p2.getPlayerID() + "')";
        Connection con = DBConnection.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }
}

