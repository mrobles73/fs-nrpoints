package co.sevenwide.nrpointsapi.pointslogic.calc;

import co.sevenwide.nrpointsapi.pointslogic.models.RaceResults;
import co.sevenwide.nrpointsapi.pointslogic.models.SeasonDriver;
import co.sevenwide.nrpointsapi.pointslogic.models.Series;
import co.sevenwide.nrpointsapi.pointslogic.models.SingleRaceDriver;

import java.text.DecimalFormat;
import java.util.*;

public class NRUtils {

    private final static int WINNER_0406 = 180;
    private final static int WINNER_0710 = 185;
    private final static int[] POINTS_75_10 = new int[]{0, 175, 170, 165, 160, 155, 150, 146, 142, 138, 134, 130, 127, 124, 121, 118, 115, 112, 109, 106, 103, 100, 97, 94, 91, 88, 85, 82, 79, 76, 73, 70, 67, 64, 61, 58, 55, 52, 49, 46, 43, 40, 37, 34};
    public final static int[] POINTS_11_15 = new int[]{0, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    public final static int[] POINTS_16 = new int[]{0, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0}; //maybe change extended to 1 instead of 0
    public final static int[] POINTS_16_TRUCKS = new int[]{0, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final static int[] POINTS_17 = new int[]{0, 40, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 1, 1, 1, 0, 0, 0};
    public final static int[] POINTS_17_TRUCKS = new int[]{0, 40, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0};
    private final static int[] TOP_10_RESET_0406 = new int[]{5050, 5045, 5040, 5035, 5030, 5025, 5020, 5015, 5010, 5005};
    private final static int[] TOP_10_POINTS_17 = new int[]{0, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    private final static HashMap<Series, Integer> BASE_RACE_COUNT = new HashMap<>();
    private final static HashMap<Series, Integer> PLAYOFF_CUTOFF = new HashMap<>();
    private final static HashMap<Series, int[][]> RACE_COUNTS = new HashMap<>();
    private static final DecimalFormat decFormat = new DecimalFormat("0.00");

    static {
        BASE_RACE_COUNT.put(Series.CUP, 36);
        BASE_RACE_COUNT.put(Series.GNS, 33);
        BASE_RACE_COUNT.put(Series.TRUCKS, 23);
        PLAYOFF_CUTOFF.put(Series.CUP, 17);
        PLAYOFF_CUTOFF.put(Series.GNS, 13);
        PLAYOFF_CUTOFF.put(Series.TRUCKS, 11);
        RACE_COUNTS.put(Series.CUP, new int[][]{{28, 1985}, {29, 1986, 1987, 1988, 1989, 1990, 1991, 1992}, {30, 1975, 1976, 1977, 1978, 1982, 1983, 1984, 1993}, {31, 1979, 1980, 1981, 1994, 1995, 1996}, {32, 1997}, {33, 1998}, {34, 1999, 2000}, {36, 2001}});
        RACE_COUNTS.put(Series.GNS, new int[][]{{26, 1995, 1996}, {27, 1985, 1987}, {28, 1993, 1994}, {29, 1982, 1984, 1989}, {30, 1988, 1997}, {31, 1986, 1990, 1991, 1992, 1998}, {32, 1999, 2000}, {33, 2001, 2012}, {34, 2002, 2003, 2004, 2011}, {35, 1983, 2005, 2006, 2007, 2008, 2009, 2010}});
        RACE_COUNTS.put(Series.TRUCKS, new int[][]{{20, 1995}, {22, 2002, 2012, 2013, 2014, 2021}, {23, 2015, 2016, 2017, 2018, 2019, 2020, 2022, 2023}, {24, 1996, 2000, 2001}, {25, 1999, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011}, {26, 1997}, {27, 1998}});
    }

    public static void setResultsPoints(RaceResults results) {
        int year = results.getYear();
        results.getRaces().forEach(race -> {
            race.getResults().forEach(srDriver -> {
                srDriver.setPoints(calculateDriverPoints(year, srDriver.getFinish(), srDriver.getLapsLed(), srDriver.isLapsLedLeader(), results.getSeries()));
            });
        });
    }

    private static int calculate0410FirstPlacePoints(int year, int led, boolean lapsLedLeader) {
        int points;
        int toadd = 0;
        if(led > 0) toadd = 5;
        if(lapsLedLeader) toadd += 5;

        if(year < 2007) {
            points = 180;
        } else {
            points = 185;
        }
        points += toadd;
        return points;
    }

    private static int calculateDriverPoints(int year, int finish, int led, boolean lapsLedLeader, Series series) {
        int points = 0;
        if(year > 1974 && year < 2011) {
            int lapsLed = 5;
            int mLapsLed = 5;
            points = POINTS_75_10[finish];
            if(finish == 1) {
                if((series.equals(Series.GNS) && year < 1998) || (series.equals(Series.TRUCKS) && year < 1999)) {
                    return 180;
                }
                if(year > 2003 && year < 2007) {
                    points = WINNER_0406;
                } else if(year > 2006) {
                    points = WINNER_0710;
                }
            }
            if(led > 0) points+=lapsLed;
            if(lapsLedLeader) points+=mLapsLed;
        } else if(year > 2010 && year < 2017) {
            int win = 3;
            int lapLed = 1;
            int mLapsLed = 1;
            if(year < 2016) {
                points = POINTS_11_15[finish];
            } else if(series.equals(Series.TRUCKS)){
                points = POINTS_16_TRUCKS[finish];
            } else {
                points = POINTS_16[finish];
            }
            if(finish == 1) points += win;
            if(led > 0) points += lapLed;
            if(lapsLedLeader) points += mLapsLed;
        } else {
            if(series.equals(Series.TRUCKS)) {
                points = POINTS_17_TRUCKS[finish];
            } else {
                points = POINTS_17[finish];
            }
            if(finish == 1) {
                points += 5;
            }
        }
        return points;
    }

    public static int getSeriesRaceCount(Series series, int year) {
//        for(int[] arr : RACE_COUNTS.get(series) ) {
//            if(Arrays.binarySearch(arr, year) > 0) {
//                return arr[0];
//            }
//        }
        if(series.equals(Series.TRUCKS) && year == 2021)
            return 22;
        return BASE_RACE_COUNT.get(series);
    }

    public static boolean compareSeasonAndSingleRaceDriver(SeasonDriver seasonDriver, SingleRaceDriver singleRaceDriver) {
        return seasonDriver.getName().equals(singleRaceDriver.getName());
    }

    public static void handlePlayoffsFinalFour(SingleRaceDriver srDriver, int year, Series series) {
        if(year < 2016) {
            srDriver.setPoints(POINTS_11_15[srDriver.getFinish()]);
        } else if(year == 2016) {
            if(series.equals(Series.TRUCKS))
                srDriver.setPoints(POINTS_16_TRUCKS[srDriver.getFinish()]);
            else
                srDriver.setPoints(POINTS_16[srDriver.getFinish()]);
        } else {
            if(series.equals(Series.TRUCKS))
                srDriver.setPoints(POINTS_17_TRUCKS[srDriver.getFinish()]);
            else
                srDriver.setPoints(POINTS_17[srDriver.getFinish()]);
        }
    }

    public static void cupPostSeason(SeasonDriver fsDriver, ArrayList<SeasonDriver> wildCardPlayoffsDrivers, int year, int racesRun) {
        if(year > 2003 && racesRun == 26) {
            if(year < 2007 && fsDriver.getPointsPosition() < 11) {
                fsDriver.setInPostSeason(true);
                fsDriver.setPoints(TOP_10_RESET_0406[fsDriver.getPointsPosition()-1]);
            } else if(year > 2006 && year < 2011 && fsDriver.getPointsPosition() < 13) {
                fsDriver.setInPostSeason(true);
                fsDriver.setPoints((fsDriver.getWins()*5) + 5000);
            } else if(year > 2010 && year < 2014) {
                if(fsDriver.getPointsPosition() < 11) {
                    fsDriver.setInPostSeason(true);
                    fsDriver.setPoints((fsDriver.getWins()*3) + 2000);
                } else if(fsDriver.getPointsPosition() > 10 && fsDriver.getPointsPosition() < 21) { //???
                    wildCardPlayoffsDrivers.add(fsDriver);
                }
            } else if(year > 2013) {
                if(fsDriver.getPointsPosition() < 17) { //&& fsDriver.getRacesRun() == 26){
                    wildCardPlayoffsDrivers.add(fsDriver);
                } else if(fsDriver.getPointsPosition() > 16 && fsDriver.getPointsPosition() < 31 && fsDriver.getRacesRun() == 26 && fsDriver.getWins() > 0) { // points position removed for 2023
                    wildCardPlayoffsDrivers.add(fsDriver);
                }
            }
        } else if(year > 2013 && (racesRun == 29 || racesRun == 32 || racesRun == 35)) {
            int cutoff = 13;
            if(racesRun == 32) {
                cutoff = 9;
            }
            if(racesRun == 35) {
                cutoff = 5;
            }
            if(fsDriver.getPointsPosition() < cutoff || (fsDriver.isInPostSeason() && fsDriver.getPlayoffWins() > 0)) {
                wildCardPlayoffsDrivers.add(fsDriver);
            } else if(fsDriver.isInPostSeason()){
                fsDriver.setInPostSeason(false);
                fsDriver.setPoints(fsDriver.getSeasonPlayoffPoints());
            }
        }
    }

    //still needs to be tested
    public static void gnsTrucksPlayoffs(SeasonDriver fsDriver, ArrayList<SeasonDriver> wildCardPlayoffsDrivers, Series series, int year, int racesRun, int raceCutoff) {
        boolean condition = series.equals(Series.TRUCKS) && year < 2020;
        if(year > 2015 && racesRun == raceCutoff) {
            int pStartCutoff = condition ? 9 : PLAYOFF_CUTOFF.get(series);
            if(fsDriver.getPointsPosition() < pStartCutoff) { //13 or 11 or 9
                wildCardPlayoffsDrivers.add(fsDriver);
            } else if(fsDriver.getPointsPosition() < 21 && fsDriver.getRacesRun() == raceCutoff && fsDriver.getWins() > 0) {
                wildCardPlayoffsDrivers.add(fsDriver);
            }
        } else if(year > 2015 && (racesRun == (raceCutoff+3) || racesRun == (raceCutoff+6))) {
            int cutoff = condition ? 7 : 9;
            if(racesRun == (raceCutoff+6))
                cutoff = 5;
            if(fsDriver.getPointsPosition() < cutoff || (fsDriver.isInPostSeason() && fsDriver.getPlayoffWins() > 0)) {
                wildCardPlayoffsDrivers.add(fsDriver);
            } else if(fsDriver.isInPostSeason()) {
                fsDriver.setInPostSeason(false);
                fsDriver.setPoints(fsDriver.getSeasonPlayoffPoints());
            }
        }
    }

    public static void setCupWildCardAndPlayoffs(ArrayList<SeasonDriver> wildCardPlayoffsDrivers, int raceNum, int year) {
        int resetPoints = 3000;
        int cutoff = 12;
        if(raceNum == 26) {
            Collections.sort(wildCardPlayoffsDrivers, new SortByWinsThenPoints());
        } else if(raceNum == 29 || raceNum == 32 || raceNum == 35) {
            Collections.sort(wildCardPlayoffsDrivers, new SortByPlayoffWinsThenPoints());
            if(raceNum == 32){
                resetPoints = 4000;
                cutoff = 8;
            } else if(raceNum == 35) {
                resetPoints = 5000;
                cutoff = 4;
            }
        }
        if(year < 2014) {
            wildCardPlayoffsDrivers.get(0).setInPostSeason(true);
            wildCardPlayoffsDrivers.get(0).setPoints(2000);
            wildCardPlayoffsDrivers.get(1).setInPostSeason(true);
            wildCardPlayoffsDrivers.get(1).setPoints(2000);
        } else if(raceNum == 26) {
            for(int i=0; i < 16; i++) {
                wildCardPlayoffsDrivers.get(i).setInPostSeason(true);
                if(year < 2017){
                    wildCardPlayoffsDrivers.get(i).setPoints((wildCardPlayoffsDrivers.get(i).getWins()*3) + 2000);
                } else {
                    int pointsFinish = (wildCardPlayoffsDrivers.get(i).getPointsPosition() < 11) ? wildCardPlayoffsDrivers.get(i).getPointsPosition() : 0;
                    wildCardPlayoffsDrivers.get(i).addPlayoffPoints(TOP_10_POINTS_17[pointsFinish]);
                    wildCardPlayoffsDrivers.get(i).setPoints(wildCardPlayoffsDrivers.get(i).getPlayoffPoints() + 2000);
                }
                wildCardPlayoffsDrivers.get(i).setSeasonPlayoffPoints(wildCardPlayoffsDrivers.get(i).getPoints());
            }
        } else if(raceNum != 36) {
            for(int i=0; i < cutoff; i++) {
                wildCardPlayoffsDrivers.get(i).setPlayoffWins(0);
                if(year < 2017) {
                    wildCardPlayoffsDrivers.get(i).setPoints(resetPoints);
                } else {
                    wildCardPlayoffsDrivers.get(i).setPoints(resetPoints + wildCardPlayoffsDrivers.get(i).getPlayoffPoints());
                }
            }
            for(int i = cutoff; i < wildCardPlayoffsDrivers.size(); i++) {
                wildCardPlayoffsDrivers.get(i).setInPostSeason(false);
                wildCardPlayoffsDrivers.get(i).setPoints(wildCardPlayoffsDrivers.get(i).getSeasonPlayoffPoints());
            }
        }
    }

    //still needs to be tested
    public static void setGnsTrucksPlayoffs(ArrayList<SeasonDriver> playoffsDrivers, Series series, int year, int racesRun, int raceCutoff) {
        if(racesRun == raceCutoff) {
            Collections.sort(playoffsDrivers, new SortByWinsThenPoints());

            int pStartCutoff = (series.equals(Series.TRUCKS) && year < 2020) ? 9 : PLAYOFF_CUTOFF.get(series);
            for(int i=0; i<pStartCutoff-1; i++) {
                SeasonDriver driver = playoffsDrivers.get(i);
                driver.setInPostSeason(true);
                if(year < 2017) {
                    driver.setPoints((driver.getWins()*3) + 2000);
                } else {
                    int pointsFinish = (driver.getPointsPosition() < 11) ? driver.getPointsPosition() : 0;
                    driver.addPlayoffPoints(TOP_10_POINTS_17[pointsFinish]);
                    driver.setPoints(driver.getPlayoffPoints() + 2000);
                }
            }
            playoffsDrivers.forEach(driver -> {
                driver.setInPostSeason(true);
                if(year < 2017) {
                    driver.setPoints((driver.getWins()*3) + 2000);
                }
            });
        } else if(racesRun == (raceCutoff+3) || racesRun == (raceCutoff+6)) {
            Collections.sort(playoffsDrivers, new SortByPlayoffWinsThenPoints());

            int resetPoints = 3000;
            int cutoff = (series.equals(Series.TRUCKS) && year < 2020) ? 6 : 8;
            if (racesRun == (raceCutoff+6)) {
                resetPoints = 4000;
                cutoff = 4;
            }
            for(int i=0; i<cutoff; i++) {
                playoffsDrivers.get(i).setPlayoffWins(0);
                if(year < 2017) {
                    playoffsDrivers.get(i).setPoints(resetPoints);
                } else {
                    playoffsDrivers.get(i).setPoints(resetPoints + playoffsDrivers.get(i).getPlayoffPoints());
                }
            }
            for(int i=cutoff; i<playoffsDrivers.size(); i++) {
                playoffsDrivers.get(i).setInPostSeason(false);
                playoffsDrivers.get(i).setPoints(playoffsDrivers.get(i).getSeasonPlayoffPoints());
            }
        }
    }

    //Comparators
    public static class SortByWinsThenPoints implements Comparator<SeasonDriver> {
        public int compare(SeasonDriver driverOne, SeasonDriver driverTwo) {
            int ret = Integer.compare(driverTwo.getWins(), driverOne.getWins());
            if(ret == 0)
                ret = Integer.compare(driverTwo.getPoints(), driverOne.getPoints());
            if(ret == 0)
                ret = Double.compare(driverOne.getAvgFinish(), driverTwo.getAvgFinish());
            if(ret == 0)
                ret = (driverTwo.getT5() != driverOne.getT5()) ? Integer.compare(driverTwo.getT5(), driverOne.getT5()) : Integer.compare(driverTwo.getT10(), driverOne.getT10());

            return ret;
        }
    }

    public static class SortByPlayoffWinsThenPoints implements Comparator<SeasonDriver> {
        public int compare(SeasonDriver driverOne, SeasonDriver driverTwo) {
            int ret = Integer.compare(driverTwo.getPlayoffWins(), driverOne.getPlayoffWins());
            if(ret == 0)
                ret = Integer.compare(driverTwo.getPoints(), driverOne.getPoints());
            if(ret == 0)
                ret = Double.compare(driverOne.getAvgFinish(), driverTwo.getAvgFinish());
            if(ret == 0)
                ret = (driverTwo.getT5() != driverOne.getT5()) ? Integer.compare(driverTwo.getT5(), driverOne.getT5()) : Integer.compare(driverTwo.getT10(), driverOne.getT10());

            return ret;
        }
    }

    public static class SortByPoints implements Comparator<SeasonDriver> {
        public int compare(SeasonDriver driverOne, SeasonDriver driverTwo) {
            int ret = Integer.compare(driverTwo.getPoints(), driverOne.getPoints());
            if(ret == 0)
                ret = Integer.compare(driverTwo.getWins(), driverOne.getWins());
            if(ret == 0)
                ret = Double.compare(driverOne.getAvgFinish(), driverTwo.getAvgFinish());
            if(ret == 0)
                ret = (driverTwo.getT5() != driverOne.getT5()) ? Integer.compare(driverTwo.getT5(), driverOne.getT5()) : Integer.compare(driverTwo.getT10(), driverOne.getT10());

            return ret;
        }
    }

}
