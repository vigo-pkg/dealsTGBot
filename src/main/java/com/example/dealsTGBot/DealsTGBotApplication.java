package com.example.dealsTGBot;

import com.example.dealsTGBot.bot.BetBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class DealsTGBotApplication {

    private final BetBot betBot;
    private final TelegramBotsApi telegramBotsApi;

    @Autowired
    public DealsTGBotApplication(BetBot betBot, TelegramBotsApi telegramBotsApi) {
        this.betBot = betBot;
        this.telegramBotsApi = telegramBotsApi;
        try {
            telegramBotsApi.registerBot(betBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DealsTGBotApplication.class, args);
    }
} 