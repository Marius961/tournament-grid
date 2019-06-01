package ua.tournament.grid.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private int requiredTeamsCount;

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

    public int getRequiredTeamsCount() {
        return requiredTeamsCount;
    }

    public void setRequiredTeamsCount(int requiredTeamsCount) {
        this.requiredTeamsCount = requiredTeamsCount;
    }
}
