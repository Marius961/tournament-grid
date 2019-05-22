package ua.tournament.grid.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 64)
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_stage")
    private Stage currentStage;

    @Size(min = 3, max = 256)
    private String location;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @NotBlank
    @Size(min = 6, max = 2048)
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tournament", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TournamentTeam> tournamentTeams;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tournament", cascade = CascadeType.ALL)
    @OrderBy("stage.id ASC ")
    private Set<Match> matches;

    @OneToOne(fetch = FetchType.EAGER)
    private Team tournamentWinner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TournamentTeam> getTournamentTeams() {
        return this.tournamentTeams;
    }

    public void setTournamentTeams(List<TournamentTeam> tournamentTeams) {
        this.tournamentTeams = tournamentTeams;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    public void setMatches(Set<Match> matches) {
        this.matches = matches;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public Team getTournamentWinner() {
        return tournamentWinner;
    }

    public void setTournamentWinner(Team tournamentWinner) {
        this.tournamentWinner = tournamentWinner;
    }
}
