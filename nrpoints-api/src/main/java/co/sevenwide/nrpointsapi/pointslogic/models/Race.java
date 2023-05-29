package co.sevenwide.nrpointsapi.pointslogic.models;

import lombok.Data;

import java.util.List;

@Data
public class Race {
    String track;
    String date;
    List<SingleRaceDriver> results;
}
