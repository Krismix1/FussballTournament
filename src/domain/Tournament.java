package domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Tournament {

    List<Match> matchList = new LinkedList<>();
    List<Team> teamList = new LinkedList<>();
    List<Player> playerList = new LinkedList<>();

    public void registerPlayer (Player p){

    playerList.add(p);
    }

    public void registerTeam (Team t){

        teamList.add(t);
    }

    public void createMatches(){
        for(int i = 0; i< teamList.size()-1; i++){
            Team team1 = teamList.get(i);
            for(int j = i+1; j <teamList.size(); j++){
                Team team2 = teamList.get(j);
                String matchName = team1.getTeamName() + " vs " + team2.getTeamName();
                matchList.add(new Match(matchName, team1, team2));
            }

        }



    }

}

