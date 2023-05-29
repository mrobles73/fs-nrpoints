package co.sevenwide.nrpointsapi.service;

import co.sevenwide.nrpointsapi.pointslogic.calc.NRUtils;
import co.sevenwide.nrpointsapi.pointslogic.models.RaceResults;
import co.sevenwide.nrpointsapi.pointslogic.models.Season;
import co.sevenwide.nrpointsapi.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeasonService implements SeasonManager {
    @Autowired
    SeasonRepository seasonRepository;

    public List<Season> getAllSeasons() {
        return seasonRepository.findAll();
    }

    @Override
    public List<Season> findByUserId(String userId) {
        //verify userid?
        return seasonRepository.findSeasonsByUserId(userId);
    }
    @Override
    public Optional<Season> findById(String id) {
        return seasonRepository.findById(id);
    }
    @Override
    public Season createSeason(RaceResults results) {
        NRUtils.setResultsPoints(results);
        Season newSeason = new Season(results.getYear(), results.getSeries());
        newSeason.addRaces(results);
        return newSeason;
    }
    @Override
    public Season saveOrUpdateSeason(Season season) {

        return seasonRepository.save(season);
    }
    @Override
    public void deleteSeasonById(String id) {
        seasonRepository.deleteById(id);
    }
}
