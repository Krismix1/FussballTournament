package domain;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Player {

    private String playerName;
    private int matchesPlayed;
    private String dob; // DD.MM.YYYY, change to LocalDate


    public Player(String playerName, String dob) {
        this.playerName = playerName;
        this.dob = dob;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        if(matchesPlayed >= 0) {
            this.matchesPlayed = matchesPlayed;
        }else
        {
            throw new IllegalArgumentException("Matches played can't be negative!");
            //this.matchesPlayed = 0;
        }
    }
    public void updateMatchesPlayed(int matchesPlayed) {
        if(matchesPlayed >= 0) {// FIXME: 4/9/2017 consider checking sum greater than 0
            this.matchesPlayed += matchesPlayed;
        }else
        {
            throw new IllegalArgumentException("Matches played can't be negative!");
            //this.matchesPlayed = 0;
        }
    }

    public void setPlayerName(String playerName) {// FIXME: 4/9/2017 name should not be empty yea
        this.playerName = playerName;
    }

    public void setDateOfBirth(String dob) {
        this.dob = dob;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public String getDob() {
        return dob;
    }
}
