package ua.tournament.grid.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tournament_match")
public class Match {


    public Match() {
    }

    public Match(Tournament tournament, Stage stage, TournamentTeam firstTeam, TournamentTeam secondTeam) {
        this.tournament = tournament;
        this.stage = stage;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    @JsonIgnore
    private Tournament tournament;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stage_id")
    private Stage stage;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_team_id")
    private TournamentTeam firstTeam;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_team_id")
    private TournamentTeam secondTeam;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private TournamentTeam winner;

    private double firstTeamResult;

    private double secondTeamResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public TournamentTeam getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(TournamentTeam firstTeam) {
        this.firstTeam = firstTeam;
    }

    public TournamentTeam getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(TournamentTeam secondTeam) {
        this.secondTeam = secondTeam;
    }

    public TournamentTeam getWinner() {
        return winner;
    }

    public void setWinner(TournamentTeam winner) {
        this.winner = winner;
    }

    public double getFirstTeamResult() {
        return firstTeamResult;
    }

    public void setFirstTeamResult(double firstTeamResult) {
        this.firstTeamResult = firstTeamResult;
    }

    public double getSecondTeamResult() {
        return secondTeamResult;
    }

    public void setSecondTeamResult(double secondTeamResult) {
        this.secondTeamResult = secondTeamResult;
    }
}
