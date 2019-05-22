package ua.tournament.grid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.tournament.grid.entities.Match;
import ua.tournament.grid.exceptions.NotFoundException;
import ua.tournament.grid.services.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/set-winner")
    public void setMatchWinner(@RequestParam Long matchId, double firstTeamResult, double secondTeamResult) throws NotFoundException {
        matchService.setMatchWinner(matchId, firstTeamResult, secondTeamResult);
    }

    @GetMapping("/{id}")
    public Match getMatch(@PathVariable Long id) throws NotFoundException {
        return matchService.getMatch(id);
    }
}
