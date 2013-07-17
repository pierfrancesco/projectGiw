package it.uniroma3.dia.giw.model;

import java.util.Map;

/**
 * User and the number of tweets produced
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StringOccurrences {

    public Map<String, Integer> getOccurrences() {

        return occurrences;
    }

    private Map<String, Integer> occurrences;

    public StringOccurrences(Map<String, Integer> occurrences) {
        this.occurrences = occurrences;
    }

}
