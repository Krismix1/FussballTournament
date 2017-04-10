package domain;

/**
 * Created by Cristian Betivu on 4/9/2017.
 */
public class Team {
    /**
     * The default template for naming a team when a desired name is not entered.
     * The names is composed from the DEFAULT_NAME and a number which is auto-incremented
     * every time a team with no name is created.
     */
    public static final String DEFAULT_NAME = "Team #"; // FIXME: 10-Apr-17 Fucks up when project restarts

    private static int teamDefaultNameIndex = 1;

    private String teamName;
    private Player firstPlayer;
    private Player secondPlayer;
    private int pointsScored;
    private int matchesPlayed;
    private int goalsFor;
    private int goalsAgainst;

    /**
     * Creates a new team with given players and given name.
     * @param firstPlayer first player of the team
     * @param secondPlayer second player of the team
     * @param teamName the name of the team
     */
    public Team(Player firstPlayer, Player secondPlayer, String teamName) {
        this.teamName = teamName;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    /**
     * Creates a new team with given players and default name.
     * The name is "{@value domain.Team#DEFAULT_NAME}" followed by a number.
     * @see Team#DEFAULT_NAME
     * @param firstPlayer first player of the team
     * @param secondPlayer second player of the team
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
     * @return the name generated
     */
    private String createDefaultName() {
        return DEFAULT_NAME + teamDefaultNameIndex++;
    }

    /**
     * Adds points to the score of the team.
     * @param pointsScored how many points the team earned at the end of the match
     */
    public void addPointsScored(int pointsScored) {
        this.pointsScored += pointsScored; // FIXME: 09-Apr-17 Check for value >= 0
    }

    /**
     * Sets team's points to the given value.
     * @param points the number of points
     */
    public void setPointsScored(int points) {
        this.pointsScored = points; // FIXME: 09-Apr-17 Check for value >= 0
    }

    /**
     * Returns the number of points the team has.
     * @return the number of points
     */
    public int getPointsScored() {
        return pointsScored;
    }

    /**
     * Returns the name of the team.
     * @return the name of the team.
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Assign a value to the name of the team.
     * @param teamName the name of the team.
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * Registers a new player to the team.
     * @throws IllegalStateException if more than 2 members are tried to be registered.
     * @param player the player to register
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
     * @param oldPlayer the player to substitute
     * @param newPlayer the new player to add. This value can't be null.
     * @throws Exception if the player to substitute is not found or if the new player is null.
     */
    public void substitutePlayer(Player oldPlayer, Player newPlayer) throws Exception{
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
     * @return the number of played matches
     */
    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    /**
     * Sets the number of played matches for the team.
     * @throws IllegalArgumentException if the number entered is negative.
     * @param matchesPlayed the number of matches played.
     */
    public void setMatchesPlayed(int matchesPlayed) {
        if (matchesPlayed >= 0) {
            this.matchesPlayed = matchesPlayed;
        } else {
            throw new IllegalArgumentException("Matches played can't be negative");
        }
    }

    /**
     * Adds the given value to the number of played matches.
     * @throws IllegalArgumentException if the matches played value is negative.
     * @param matchesPlayed the number of matches to add.
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
     * Returns how many goals the team scored.
     * @return the number of how many goals the team scored.
     */
    public int getGoalsFor() {
        return goalsFor;
    }

    /**
     * Sets the value of how many goals the team scored.
     * @param goals the number of goals scored by the team.
     */
    public void setGoalsFor(int goals) {
        this.goalsFor = goals;
    }

    /**
     * Adds goals to the number of goals scored by the team.
     * @throws IllegalArgumentException if the number of goals to add is negative.
     * @param goals the number of goals to add.
     */
    public void addGoalsFor(int goals) {
        if (goals >= 0) {// FIXME: 4/9/2017 consider checking sum greater than 0
            this.goalsFor += goals;
        } else {
            throw new IllegalArgumentException("Goals scored for team can't be negative!");
            //this.goalsFor = 0;
        }
    }

    /**
     * Returns the number of goals scored by the team.
     * @return the number of goals scored.
     */
    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    /**
     * Sets the number of goals scored against current team.
     * @param goals the number of goals scored against current team.
     */
    public void setGoalsAgainst(int goals) {
        this.goalsAgainst = goals;
    }

    /**
     * Adds goals to the number of goals scored against the team.
     * @throws IllegalArgumentException if the number of goals to add is negative.
     * @param goals the number of goals to add.
     */
    public void addGoalsAgainst(int goals) {
        if (goals >= 0) {// FIXME: 4/9/2017 consider checking sum greater than 0
            this.goalsAgainst += goals;
        } else {
            throw new IllegalArgumentException("Goals scored against team can't be negative!");
            //this.goalsFor = 0;
        }
    }
}
