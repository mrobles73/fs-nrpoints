package co.sevenwide.nrpointsapi.pointslogic.models;

import co.sevenwide.nrpointsapi.dto.SeasonDTO;
import co.sevenwide.nrpointsapi.pointslogic.calc.NRUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Season {

    @Id
    private String id;
    private String userId;
    private int year;
    private int racesRun = 0;
    private boolean ended = false;
    private Series series;
    private RaceResults results;
    private List<String> raceTracks = new ArrayList<>();
    protected List<SeasonDriver> seasonDriverList = new ArrayList<>(); //these are the current standings
    protected List<List<SeasonDriver>> prevStandings = new ArrayList<List<SeasonDriver>>();

    public Season(int year, Series series) {
        this.year = year;
        this.series = series;
    }

    public static Season from(SeasonDTO season) {
        Season ret = new Season(season.getYear(), season.getSeries());
        ret.setId(season.getId());
        ret.setUserId(season.getUserId());
        ret.setRacesRun(season.getRacesRun());
        ret.setEnded(season.isEnded());
        ret.setResults(season.getResults());
        ret.setRaceTracks(season.getRaceTracks());
        ret.setSeasonDriverList(season.getCurrentStandings());
        ret.setPrevStandings(season.getAllStandings());
        return ret;
    }

    public void addRaces(RaceResults results) {
        this.results = results;
        final int raceCount = ((this.series.equals(Series.CUP) && this.year > 2003) || year > 2015) ?  NRUtils.getSeriesRaceCount(series, year) : 0;
        final int postSeasonRaceCutoff = raceCount == 0 ? 0 : series.equals(Series.CUP) ? raceCount-10 : raceCount-7;
        final boolean hasPostSeason = (series.equals(Series.CUP) && year > 2003) || year > 2015;

        results.getRaces().forEach(race -> {
            if(ended) return;

            raceTracks.add(race.getTrack());
            racesRun++;
            if(racesRun == 1) {
                race.getResults().forEach(driver -> seasonDriverList.add(createSeasonDriver(driver)));
            } else {
                ArrayList<SeasonDriver> wildCardPlayoffsDrivers = new ArrayList<SeasonDriver>(); //find a better name for this!!!!!
                race.getResults().forEach(srDriver -> {
                    SeasonDriver fsDriver = seasonDriverList.stream()
                            .filter(seasonDriver -> NRUtils.compareSeasonAndSingleRaceDriver(seasonDriver, srDriver))
                            .findAny()
                            .orElse(null);
                    if(fsDriver == null) { //if driver hasn't run race, add him to the driver list
                        seasonDriverList.add(createSeasonDriver(srDriver));
                    } else {
                        if(hasPostSeason && year > 2013 && racesRun == raceCount && fsDriver.isInPostSeason()) { //!!!
                            NRUtils.handlePlayoffsFinalFour(srDriver, year, series);
                        }

                        fsDriver.setNumber(srDriver.getNumber());
                        fsDriver.incrementRacesRun()
                                .incrementPoints(srDriver.getPoints())
                                .incrementLapsRun(srDriver.getLapsRun())
                                .incrementLapsLed(srDriver.getLapsLed())
                                .calcAvgFinish(srDriver.getFinish())
                                .calcAvgStart(srDriver.getStart())
                                .addPlayoffPoints(srDriver.getPlayoffPoints());

                        if(fsDriver.isInPostSeason())
                            fsDriver.incrementSeasonPlayoffPoints(srDriver.getPoints());
                        if(srDriver.getStart() == 1)
                            fsDriver.incrementPoles();
                        if(srDriver.getFinish() < 6)
                            fsDriver.incrementT5();
                        if(srDriver.getFinish() < 11)
                            fsDriver.incrementT10();
                        if(!srDriver.getStatus().equals("Running"))
                            fsDriver.incrementDNFs();
                        if(srDriver.getFinish() == 1){
                            fsDriver.incrementWins();
                            if(hasPostSeason && fsDriver.isInPostSeason() && racesRun > postSeasonRaceCutoff) {
                                fsDriver.incrementPlayoffWins();
                            }
                        }

                        if(hasPostSeason && racesRun >= postSeasonRaceCutoff) {
                            //calculate playoffs
                            if(series.equals(Series.CUP)) {
                                NRUtils.cupPostSeason(fsDriver, wildCardPlayoffsDrivers, this.year, this.racesRun);
                            } else {
                                NRUtils.gnsTrucksPlayoffs(fsDriver, wildCardPlayoffsDrivers, this.series, this.year, this.racesRun, postSeasonRaceCutoff);
                            }
                        }
                    }
                });
                if(series.equals(Series.CUP) && year > 2016 && racesRun == postSeasonRaceCutoff) { //??
                    seasonDriverList.sort(new NRUtils.SortByPoints());
                    for(int i=0; i<10; i++)
                        seasonDriverList.get(i).setPointsPosition(i+1);
                }
                if (wildCardPlayoffsDrivers.size() > 0) {
                    if(series.equals(Series.CUP)) {
                        NRUtils.setCupWildCardAndPlayoffs(wildCardPlayoffsDrivers, this.racesRun, this.year); //make gns and trucks version
                    } else {
                        NRUtils.setGnsTrucksPlayoffs(wildCardPlayoffsDrivers, this.series, this.year, this.racesRun, postSeasonRaceCutoff);
                    }
                }
            }

            seasonDriverList.sort(new NRUtils.SortByPoints());
            for(int i = 0; i<seasonDriverList.size(); i++) {
                seasonDriverList.get(i).setPointsPosition(i+1);
                seasonDriverList.get(i).setPointsToLeader(seasonDriverList.get(0).getPoints() - seasonDriverList.get(i).getPoints());
                if (i == 0)
                    seasonDriverList.get(i).setPointsToNext(0);
                else
                    seasonDriverList.get(i).setPointsToNext(seasonDriverList.get(i - 1).getPoints() - seasonDriverList.get(i).getPoints());
            }
            prevStandings.add(makeDeepCopyOfStandings(seasonDriverList));

            if(racesRun == raceCount)
                ended = true;
        });
    }

    private SeasonDriver createSeasonDriver(SingleRaceDriver singleRaceDriver) {
        int poles = (singleRaceDriver.getStart() == 1) ? 1:0;
        int wins = (singleRaceDriver.getFinish() == 1) ? 1:0;
        int t5 = (singleRaceDriver.getFinish() < 6) ? 1:0;
        int t10 = (singleRaceDriver.getFinish() < 11) ? 1:0;
        int dnfs = (singleRaceDriver.getStatus().equals("Running")) ? 0:1;
        return new SeasonDriver(
                singleRaceDriver.getName(),
                singleRaceDriver.getNumber(),
                singleRaceDriver.getPoints(),
                singleRaceDriver.getPlayoffPoints(),
                0,
                0,
                1,
                singleRaceDriver.getLapsRun(),
                singleRaceDriver.getLapsLed(),
                singleRaceDriver.getStart(),
                singleRaceDriver.getFinish(),
                false,
                0,
                0,
                poles,
                wins,
                0,
                t5,
                t10,
                dnfs
        );
    }

    private List<SeasonDriver> makeDeepCopyOfStandings(List<SeasonDriver> list) {
        List<SeasonDriver> newList = new ArrayList<>();
        list.forEach((driver) -> newList.add(new SeasonDriver(
                driver.getName(),
                driver.getNumber(),
                driver.getPoints(),
                driver.getPlayoffPoints(),
                driver.getSeasonPlayoffPoints(),
                driver.getPointsPosition(),
                driver.getRacesRun(),
                driver.getLapsRun(),
                driver.getLapsLed(),
                driver.getAvgStart(),
                driver.getAvgFinish(),
                driver.isInPostSeason(),
                driver.getPointsToLeader(),
                driver.getPointsToNext(),
                driver.getPoles(),
                driver.getWins(),
                driver.getPlayoffWins(),
                driver.getT5(),
                driver.getT10(),
                driver.getDnfs()
        )));
        return newList;
    }
}
