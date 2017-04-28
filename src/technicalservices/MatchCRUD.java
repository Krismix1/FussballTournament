package technicalservices;

import domain.Match;
import domain.Team;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Chris on 28-Apr-17.
 */
public class MatchCRUD {

    public Map<String, Match> readAllMatches(Map<String, Team> teamsMap) {
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
     * For each match, a new connection is created and then destroyed. Think of a better way to store them,
     * because this takes a lot of time. Also the shuffle thing is pointless because the DBMS reorders the data.
     */
    public void saveMatchesToDB(List<Match> matchList) {
        //Collections.shuffle(matchList);
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
    public List<Match> getDueMatches(Map<String, Team> teamMap) {
        Map<String, Match> matchesMap = readAllMatches(teamMap);
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

    public List<Match> getMatchesResults(Map<String, Team> teamMap) {
        Map<String, Match> matchesMap = readAllMatches(teamMap);
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

    public boolean editMatchDate(Match match) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE matches SET match_date = ? WHERE match_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(match.getMatchDate()));
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
