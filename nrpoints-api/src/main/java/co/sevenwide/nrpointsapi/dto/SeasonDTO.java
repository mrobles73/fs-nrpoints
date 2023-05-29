package co.sevenwide.nrpointsapi.dto;

import co.sevenwide.nrpointsapi.pointslogic.models.RaceResults;
import co.sevenwide.nrpointsapi.pointslogic.models.Season;
import co.sevenwide.nrpointsapi.pointslogic.models.SeasonDriver;
import co.sevenwide.nrpointsapi.pointslogic.models.Series;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class SeasonDTO {

    private String id;
    private String userId;
    private int year;
    private int racesRun;
    private boolean ended;
    private Series series;
    private RaceResults results;
    private List<String> raceTracks;
    private List<SeasonDriver> currentStandings;
    private List<List<SeasonDriver>> allStandings;

    public static SeasonDTO from(Season season) {
        return builder()
                .id(season.getId())
                .userId(season.getUserId())
                .year(season.getYear())
                .racesRun(season.getRacesRun())
                .ended(season.isEnded())
                .series(season.getSeries())
                .results(season.getResults())
                .raceTracks(season.getRaceTracks())
                .currentStandings(season.getSeasonDriverList())
                .allStandings(season.getPrevStandings())
                .build();
    }

}
