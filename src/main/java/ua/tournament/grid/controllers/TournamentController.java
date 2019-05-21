package ua.tournament.grid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ua.tournament.grid.entities.Tournament;
import ua.tournament.grid.exceptions.NotFoundException;
import ua.tournament.grid.services.TournamentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public Page<Tournament> getAllTournaments(@Valid @RequestParam(name = "p") int page) {
        return tournamentService.getAllTournaments(page);
    }

    @PostMapping
    public void createTournament(@RequestBody Tournament tournament) throws NotFoundException {
        tournamentService.createTournament(tournament);
    }
}
