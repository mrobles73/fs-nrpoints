package co.sevenwide.nrpointsapi.repository;

import co.sevenwide.nrpointsapi.pointslogic.models.Season;
import co.sevenwide.nrpointsapi.pointslogic.models.Series;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeasonRepository  extends MongoRepository<Season, String> {
    List<Season> findSeasonsByYear(int year);
    List<Season> findSeasonsByUserId(String userId);
    List<Season> findSeasonsBySeries(Series series);
}
