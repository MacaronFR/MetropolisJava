package fr.metropolis.gestion.api;

import fr.metropolis.gestion.api.db.Projects;

import java.util.List;
import java.util.Objects;

public class Project {

    private Projects p;

    public Project(Projects p){
        this.p = Objects.requireNonNullElseGet(p, Projects::new);
    }

    public Project(){
        this(null);
    }

    public List<Project> getAllUserProject(){
        return null;
    }
}
