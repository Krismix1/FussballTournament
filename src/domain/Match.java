package domain;

/**
 * Created by ArcticMonkey on 4/9/2017.
 */

public class Match {
    private String matchName;
    private String matchDate;
    private Team team1;
    private Team team2;
    private int goals1, goals2;

    public Match(){}

    /**
     * Creates a new match with given matchName and two teams.
     * @param matchName name of match
     * @param team1 team #1
     * @param team2 team #2
     */
    public Match(String matchName, Team team1, Team team2) {
        this.matchName = matchName;
        this.team1 = team1;
        this.team2 = team2;
    }

    /**
     * Creates a new match given matchName, two teams and also goals scored for each team
     * @param matchName name of match
     * @param team1 team #1
     * @param team2 team #2
     * @param goals1 goals scored by team #1
     * @param goals2 goals scored by team #2
     */
    public Match(String matchName, Team team1, Team team2, int goals1, int goals2) {
        this.matchName = matchName;
        this.team1 = team1;
        this.team2 = team2;
        this.goals1 = goals1;
        this.goals2 = goals2;
    }

    /**
     * Gets match name
     * @return match name
     */
    public String getMatchName() {
        return matchName;
    }

    /**
     * Returns the first team that is participating in the match.
     * @return the first team of the match
     */
    public Team getTeamOne() {
        return team1;
    }

    /**
     * Returns the second team that is participating in the match.
     * @return the second team of the match
     */
    public Team getTeamTwo() {
        return team2;
    }

    /**
     * Returns the date when the match is registered to be played.
     * @return the date when the match should be played.
     */
    public String getMatchDate() {
        return matchDate;
    }

    public int getTeamOneGoals() {
        return goals1;
    }

    public int getTeamTwoGoals() {
        return goals2;
    }

    /**
     * Sets the name of the match.
     * @param matchName the name of the match.
     */
    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    /**
     * Sets the date of the match.
     * @param matchDate the date when the match is played.
     */
    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public void setTeamOne(Team team1) {
        this.team1 = team1;
    }

    public void setTeamTwo(Team team2) {
        this.team2 = team2;
    }

    public void setGoalsForTeamOne(int goals) {
        this.goals1 = goals1;
    }

    public void setGoalsForTeamTwo(int goals) {
        this.goals2 = goals2;
    }
}
