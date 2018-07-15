package de.iolite.apps.ioliteslackbot.dialogflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Device to cast to DialogFlow
 */
public class Device extends Entity {
    private String value;
    private List<String> synonyms;

    public Device(String value, List<String> synonyms) {
        this.value = value;
        this.synonyms = synonyms;
    }

    public Device(String value) {
        this.value = value;
        List<String> synonyms = new ArrayList();
        this.synonyms = synonyms;
    }

    public void addSynonym(String synonym) {
        synonyms.add(synonym);
    }

    public List<String> getSynonym(String synonym) {
        return synonyms;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
