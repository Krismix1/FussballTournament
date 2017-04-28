package technicalservices;

import domain.Player;
import domain.Team;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Chris on 28-Apr-17.
 */
public class TeamCRUD {

    public Map<String, Team> readAllTeams() {
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

    public void deleteTeamDB(Team t) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM teams WHERE team_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, t.getTeamName());
            pstmt.executeUpdate();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
