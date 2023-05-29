package co.sevenwide.nrpointsapi.service;

import co.sevenwide.nrpointsapi.pointslogic.models.RaceResults;
import co.sevenwide.nrpointsapi.pointslogic.models.Season;

import java.util.List;
import java.util.Optional;

public interface SeasonManager {

    public Season createSeason(RaceResults results);
    public List<Season> findByUserId(String userId);
    public Optional<Season> findById(String id);
    public Season saveOrUpdateSeason(Season season);
    public void deleteSeasonById(String id);
}
