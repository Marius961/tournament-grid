package ua.tournament.grid.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stage {

    public Stage() {
    }

    public Stage(String name, String code, int requiredTeamsCount) {
        this.name = name;
        this.code = code;
        this.requiredTeamsCount = requiredTeamsCount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRequiredTeamsCount() {
        return requiredTeamsCount;
    }

    public void setRequiredTeamsCount(int requiredTeamsCount) {
        this.requiredTeamsCount = requiredTeamsCount;
    }
}
