package domain;

import java.time.LocalDate;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */
public class Player {

    private int player_id;
    private String playerName;
    private int matchesPlayed;
    private LocalDate dob; // MM.DD.YYYY, change to LocalDate
    private String email;


    /**
     * Creates a new player with given name, date of birth and email.
     *
     * @param playerName player name
     * @param dob        date of birth of the player
     * @param email      email of the player
     */
    public Player(String playerName, LocalDate dob, String email) {
        this.playerName = playerName;
        this.dob = dob;
        this.email = email;
    }

    /**
     * Creates a new player with given name, date of birth, email and ID.
     *
     * @param playerName player name
     * @param dob        date of birth of the player
     * @param email      email of the player
     * @param id         the ID of the player, which represents the primary key for this player in the database
     */

    public Player(String playerName, LocalDate dob, String email, int id) {
        this.playerName = playerName;
        this.dob = dob;
        this.email = email;
        this.player_id = id;
    }

    /**
     * Sets amount of matches played for the player.
     *
     * @param matchesPlayed the number of matches played by the player
     * @throws IllegalArgumentException if the number of matches played is negative
     */
    public void setMatchesPlayed(int matchesPlayed) {
        if (matchesPlayed >= 0) {
            this.matchesPlayed = matchesPlayed;
        } else {
            throw new IllegalArgumentException("Matches played can't be negative!");
            //this.matchesPlayed = 0;
        }
    }

    /**
     * Adds to amount of matches played for the player.
     *
     * @param matchesPlayed the number of how many matches to add
     * @throws IllegalArgumentException if the value of the parameters is negative
     */
    public void addMatchesPlayed(int matchesPlayed) {
        if (matchesPlayed >= 0) {// FIXME: 4/9/2017 consider checking sum greater than 0
            this.matchesPlayed += matchesPlayed;
        } else {
            throw new IllegalArgumentException("Matches played can't be negative!");
            //this.matchesPlayed = 0;
        }
    }

    /**
     * Sets player name.
     *
     * @param playerName the name of the player.
     */
    public void setPlayerName(String playerName) {// FIXME: 4/9/2017 name should not be empty yea
        this.playerName = playerName;
    }

    /**
     * Sets player's date of birth.
     *
     * @param dob the date of birth of the player.
     */
    public void setDateOfBirth(LocalDate dob) {
        this.dob = dob;
    }

    /**
     * Gets player's name.
     *
     * @return the name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the number of matches played by the player.
     *
     * @return the number of matches played.
     */
    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    /**
     * Gets the date of birth of the player.
     *
     * @return the date of birth of the player.
     */
    public LocalDate getDateOfBirth() {
        return dob;
    }

    /**
     * Returns the email of the player.
     *
     * @return the email of the player.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email for the player.
     *
     * @param email player's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns player's ID.
     *
     * @return player's ID.
     */
    public int getPlayerID() {
        return player_id;
    }

    @Override
    public String toString() {
        //return String.format("%s %s %s",playerName, dob, email);
        return this.playerName;
    }
}
