package domain;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Team {

    private String teamName;
    private Player firstPlayer;
    private Player secondPlayer;
    private int pointsScored;
    private int matchesPlayed;
    private int goalsFor;
    private int goalsAgainst;

    public Team(String teamName, Player firstPlayer, Player secondPlayer) {
        this.teamName = teamName;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public Team(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public void updatePointsScored(int pointsScored) {
        this.pointsScored += pointsScored;
    }


}
