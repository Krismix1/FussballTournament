package technicalservices;

import domain.Player;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 28-Apr-17.
 */
public class PlayerCRUD {
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

    public void deletePlayerFromDB(Player p) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM players WHERE player_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, p.getPlayerID());
            pstmt.executeUpdate();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
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
}
