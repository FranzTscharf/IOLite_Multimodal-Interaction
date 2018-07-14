package de.iolite.apps.ioliteslackbot.dialogflow.model;

import java.util.ArrayList;
import java.util.List;

public class Entities {

    private String name;
    private List<Entity> entries;

    public Entities(String name){
        this.name = name;
        this.entries = new ArrayList<Entity>();
    }
    public Entities(String name, List<Entity> entriesList){
        this.name = name;
        this.entries = entriesList;
    }
    public void setEntries(List<Entity> entriesList){
        this.entries = entriesList;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Entity> getEntries() {
        return entries;
    }

}
