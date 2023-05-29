package co.sevenwide.nrpointsapi.pointslogic.models;

import lombok.Data;

@Data
public class SingleRaceDriver {
    private String name;
    private int finish;
    private int start;
    private int number;
    private String interval;
    private int lapsRun;
    private int lapsLed;
    private int points;
    private int playoffPoints;
    private String status;
    private boolean lapsLedLeader;
}
