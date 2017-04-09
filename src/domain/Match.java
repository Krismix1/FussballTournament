package domain;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Match {
    private String matchName;
    private Team team1;
    private Team team2;
    private int goals1, goals2;

    public Match(String matchName, Team team1, Team team2) {
        this.matchName = matchName;
        this.team1 = team1;
        this.team2 = team2;
    }

    public Match(String matchName, Team team1, Team team2, int goals1, int goals2) {
        this.matchName = matchName;
        this.team1 = team1;
        this.team2 = team2;
        this.goals1 = goals1;
        this.goals2 = goals2;
    }
}
