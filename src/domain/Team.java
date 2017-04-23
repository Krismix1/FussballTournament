package domain;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Created by Cristian Betivu on 4/9/2017.
 */
public class Team {
    /**
     * The default template for naming a team when a desired name is not entered.
     * The names is composed from the DEFAULT_NAME and a number which is auto-incremented
     * every time a team with no name is created.
     */
    public static final String DEFAULT_NAME = "Team #";
    private static final String ERROR_TEAM_NAME = "Error"; // The name to be assigned if the createDefaultName() failed

    private static int teamDefaultNameIndex = 1;
    private static final File NAME_INDEX_FILE = new File("teamNameIndex.txt");

    private String teamName;
    private Player firstPlayer;
    private Player secondPlayer;
    private int matchesPlayed;
    private int matchesLost;
    private int matchesWon;   // Point amount is the same as matchesWon, that's why we refer to this field when need to get points.
    private int goalDifference;


    public Team(String teamName) {
        this.teamName = teamName;
    }

    /**
     * Creates a new team with given players and given name.
     *
     * @param firstPlayer  first player of the team
     * @param secondPlayer second player of the team
     * @param teamName     the name of the team
     */
    public Team(Player firstPlayer, Player secondPlayer, String teamName) {
        this.teamName = teamName;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    /**
     * Creates a new team with given players and default name.
     * The name is "{@value domain.Team#DEFAULT_NAME}" followed by a number.
     *
     * @param firstPlayer  first player of the team
     * @param secondPlayer second player of the team
     * @see Team#DEFAULT_NAME
     */
    public Team(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.teamName = createDefaultName();
    }

    /**
     * Creates a name for teams which don't specify a name.
     * It uses the DEFAULT_NAME followed by a number. After generation,
     * the number is incremented, so that the names are unique for each team.
     *
     * @return the name generated
     */
    private String createDefaultName() {
        try {
            if (!NAME_INDEX_FILE.exists()) {
                NAME_INDEX_FILE.createNewFile();
            }
            Scanner input = new Scanner(NAME_INDEX_FILE);
            if (input.hasNextInt()) {
                teamDefaultNameIndex = input.nextInt();
            }
            input.close();
            PrintStream output = new PrintStream(NAME_INDEX_FILE);
            output.print(teamDefaultNameIndex + 1);// writes the next available number
            return DEFAULT_NAME + teamDefaultNameIndex;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_TEAM_NAME;
        }
    }

    /**
     * Returns the name of the team.
     *
     * @return the name of the team.
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Assign a value to the name of the team.
     *
     * @param teamName the name of the team.
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * Registers a new player to the team.
     *
     * @param player the player to register
     * @throws IllegalStateException if more than 2 members are tried to be registered.
     */
    public void registerPlayer(Player player) {
        if (firstPlayer == null) {
            firstPlayer = player;
            return;
        }
        if (secondPlayer == null) {
            secondPlayer = player;
        } else {
            throw new IllegalStateException("Full team");
        }
    }

    /**
     * Substitutes players in the team with a new one.
     *
     * @param oldPlayer the player to substitute
     * @param newPlayer the new player to add. This value can't be null.
     * @throws Exception if the player to substitute is not found or if the new player is null.
     */
    public void substitutePlayer(Player oldPlayer, Player newPlayer) throws Exception {
        if (newPlayer != null) {
            if (firstPlayer.equals(oldPlayer)) {
                firstPlayer = newPlayer;
            } else if (secondPlayer.equals(oldPlayer)) {
                secondPlayer = newPlayer;
            } else {
                throw new Exception("Player not found.");
            }
        } else {
            throw new Exception("Player can't be null");
        }
    }

    /**
     * Returns how many matches the team played.
     *
     * @return the number of played matches
     */
    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    /**
     * Returns the goal difference.
     * @return
     */
    public int getGoalDifference() {
        return goalDifference;
    }

    /**
     * Updates the goal difference for the team.
     * @param diff
     */
    public void updateGoalDifference(int diff){
        this.goalDifference +=diff;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    /**
     * Increments matches lost and matches played for the team.
     */
    public void incrementMatchesLost() {
        this.matchesLost++;
        this.matchesPlayed++;
    }

    /**
     * Increments matches won and matches played for the team.
     */
    public void incrementMatchesWon() {
        this.matchesWon++;
        this.matchesPlayed++;
    }

    /**
     * Returns the first player of the team.
     *
     * @return the first player of the team
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Returns the second player of the team.
     *
     * @return the second player of the team
     */
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public String toString() {
        return teamName;
    }
}
