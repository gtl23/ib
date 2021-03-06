package com.truecaller.ib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult {

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("spam_count")
    private Long spamCount;

    public SearchResult(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public SearchResult() {
    }

    public SearchResult(String name, String phone, Long spamCount) {
        this.name = name;
        this.phone = phone;
        this.spamCount = spamCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(Long spamCount) {
        this.spamCount = spamCount;
    }


}
