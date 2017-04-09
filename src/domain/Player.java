package domain;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Player {

    private String playerName;
    private int matchesPlayed;
    private String dob;
    private int goalsScored;

    public Player(String playerName) {
        this.playerName = playerName;
        this.matchesPlayed = 0;
        this.goalsScored = 0;
    }


}
