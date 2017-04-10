package domain;

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
}

