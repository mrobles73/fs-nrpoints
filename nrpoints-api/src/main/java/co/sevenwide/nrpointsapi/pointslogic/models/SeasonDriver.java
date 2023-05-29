package co.sevenwide.nrpointsapi.pointslogic.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeasonDriver {
    private String name;
    private int number;
    private int points;
    private int playoffPoints;
    private int seasonPlayoffPoints; //was seasonPlayoffPoints
    private int pointsPosition;
    private int racesRun;
    private int lapsRun;
    private int lapsLed;
    private double avgStart;
    private double avgFinish;
    private boolean inPostSeason;
    private int pointsToLeader;
    private int pointsToNext;
    private int poles;
    private int wins;
    private int playoffWins;
    private int t5;
    private int t10;
    private int dnfs;

    public SeasonDriver incrementRacesRun() {
        this.racesRun++;
        return this;
    }

    public void incrementSeasonPlayoffPoints(int points) {
        this.seasonPlayoffPoints += points;
    }

    public SeasonDriver incrementPoints(int points) {
        this.points += points;
        return this;
    }

    public SeasonDriver incrementLapsRun(int lapsRun) {
        this.lapsRun += lapsRun;
        return this;
    }

    public SeasonDriver incrementLapsLed(int lapsLed) {
        this.lapsLed += lapsLed;
        return this;
    }

    public SeasonDriver calcAvgFinish(int finish) {
        this.avgFinish = ((this.avgFinish * (this.racesRun-1)) + finish) / this.racesRun;
        return this;
    }

    public SeasonDriver calcAvgStart(int start) {
        this.avgStart = ((this.avgStart * (this.racesRun-1)) + start) / this.racesRun;
        return this;
    }

    public void addPlayoffPoints(int playoffPoints) {
        this.playoffPoints += playoffPoints;
    }

    public void incrementPoles() {
        this.poles++;
    }

    public void incrementT5() {
        this.t5++;
    }

    public void incrementT10() {
        this.t10++;
    }

    public void incrementDNFs() {
        this.dnfs++;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementPlayoffWins() {
        this.playoffWins++;
    }
}

