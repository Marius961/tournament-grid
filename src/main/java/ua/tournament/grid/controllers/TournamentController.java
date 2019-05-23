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
    public Page<Tournament> getAllTournaments(@RequestParam(name = "p") int page) {
        return tournamentService.getAllTournaments(page);
    }

    @DeleteMapping("/{id}")
    public void deleteTournament(@PathVariable Long id) throws NotFoundException {
        tournamentService.deleteTournament(id);
    }

    @GetMapping("/{id}")
    public Tournament getTournament(@PathVariable Long id) throws NotFoundException {
        return tournamentService.getTournament(id);
    }

    @PostMapping
    public void createTournament(@RequestBody Tournament tournament) throws NotFoundException {
        tournamentService.createTournament(tournament);
    }
}
