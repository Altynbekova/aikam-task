package com.altynbekova.aikamtask.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"type", "results"})
public class SearchOutput {
    private static final String TYPE = "search";
    @JsonProperty("results")
    private List<SearchResult> searchResults;

    public SearchOutput(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public String getType() {
        return TYPE;
    }
}