package co.sevenwide.nrpointsapi.pointslogic.models;

import lombok.Data;

import java.util.List;

@Data
public class RaceResults {
    private int year;
    private Series series;
    private List<Race> races;
}
