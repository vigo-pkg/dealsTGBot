package com.example.dealsTGBot.model;

public class Bet {
    private final Long id;
    private final String description;
    private String decision;

    public Bet(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    @Override
    public String toString() {
        return String.format("Bet #%d: %s%s", 
            id, 
            description, 
            decision != null ? " (Decision: " + decision + ")" : "");
    }
} 