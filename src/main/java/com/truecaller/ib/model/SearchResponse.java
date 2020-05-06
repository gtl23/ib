package com.truecaller.ib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    @JsonProperty("search_results")
    List<SearchResult> searchResults;

    @JsonProperty("total_elements")
    Long totalElements;

    public SearchResponse(List<SearchResult> searchResults, Long totalElements) {
        this.searchResults = searchResults;
        this.totalElements = totalElements;
    }

    public SearchResponse() {
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
}
