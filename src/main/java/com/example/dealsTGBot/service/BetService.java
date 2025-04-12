package com.example.dealsTGBot.service;

import com.example.dealsTGBot.model.Bet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BetService {
    private final List<Bet> bets = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Bet createBet(String description) {
        Bet bet = new Bet(idCounter.getAndIncrement(), description);
        bets.add(bet);
        return bet;
    }

    public Bet getBet(Long id) {
        return bets.stream()
                .filter(bet -> bet.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Bet decide(Long id, String decision) {
        Bet bet = getBet(id);
        if (bet != null) {
            bet.setDecision(decision);
        }
        return bet;
    }

    public List<Bet> getAllBets() {
        return new ArrayList<>(bets);
    }
} 