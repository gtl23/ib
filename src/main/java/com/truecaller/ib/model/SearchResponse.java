package com.truecaller.ib.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SearchResponse {

    @JsonProperty("search_results")
    List<SearchResult> searchResults;

    @JsonProperty("total_elements")
    Long totalElements;

    public SearchResponse(List<SearchResult> searchResults, Long totalElements) {
        this.searchResults = searchResults;
        this.totalElements = totalElements;
    }
}
