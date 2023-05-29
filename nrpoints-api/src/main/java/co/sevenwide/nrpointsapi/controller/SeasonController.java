package co.sevenwide.nrpointsapi.controller;

import co.sevenwide.nrpointsapi.dto.SeasonDTO;
import co.sevenwide.nrpointsapi.pointslogic.models.RaceResults;
import co.sevenwide.nrpointsapi.pointslogic.models.Season;
import co.sevenwide.nrpointsapi.service.SeasonManager;
import co.sevenwide.nrpointsapi.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/season")
public class SeasonController {

    @Autowired
    private SeasonManager seasonService;

    //this should be open but rest of routes should be secured
    @PostMapping("/standings")
    public ResponseEntity<SeasonDTO> getStandings(@RequestBody RaceResults results) {
        return ResponseEntity.ok(SeasonDTO.from(seasonService.createSeason(results)));
    }

    @PostMapping("/save")
    public ResponseEntity<SeasonDTO> saveOrUpdateSeason(@RequestBody SeasonDTO season) {
        return ResponseEntity.ok(SeasonDTO.from(seasonService.saveOrUpdateSeason(Season.from(season))));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GenericResponse> deleteSeasonById(@RequestParam String id) {
        seasonService.deleteSeasonById(id);
        return ResponseEntity.ok(new GenericResponse("Season " + id + " deleted successfully"));
    }

    @GetMapping("/id")
    public ResponseEntity<SeasonDTO> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok(SeasonDTO.from(seasonService.findById(id).get())); //maybe make an exception for when season is not found, have a check to make sure the user id on it equals the user id from the person calling
    }

    @GetMapping("/userid")
    public ResponseEntity<List<SeasonDTO>> getByUserId(@RequestParam("userId") String userId) {
        List<SeasonDTO> seasons = new ArrayList<>();
        seasonService.findByUserId(userId).forEach(season -> seasons.add(SeasonDTO.from(season)));
        return ResponseEntity.ok(seasons);
    }
}
