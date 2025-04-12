package com.example.dealsTGBot.bot;

import com.example.dealsTGBot.model.Bet;
import com.example.dealsTGBot.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BetBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final BetService betService;
    private final Map<Long, UserDialogData> userDialogs = new HashMap<>();

    @Autowired
    public BetBot(@Value("${bot.username}") String botUsername,
                 @Value("${bot.token}") String botToken,
                 BetService betService) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.betService = betService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        try {
            SendMessage response = processMessage(messageText, chatId);
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage processMessage(String messageText, long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));

        UserDialogData dialogData = userDialogs.getOrDefault(chatId, new UserDialogData());

        // Обработка кнопок клавиатуры и команд
        if (messageText.equals("📝 Create Bet") || messageText.startsWith("/create_bet")) {
            if (messageText.startsWith("/create_bet")) {
                String description = messageText.substring("/create_bet".length()).trim();
                if (!description.isEmpty()) {
                    Bet bet = betService.createBet(description);
                    response.setText("✅ Bet created successfully!\n\n" + bet);
                    dialogData.setState(DialogState.NONE);
                } else {
                    dialogData.setState(DialogState.WAITING_FOR_BET_DESCRIPTION);
                    response.setText("Please enter the bet description:");
                }
            } else {
                dialogData.setState(DialogState.WAITING_FOR_BET_DESCRIPTION);
                response.setText("Please enter the bet description:");
            }
        } else if (messageText.equals("🔍 View Bet") || messageText.startsWith("/view_bet")) {
            if (messageText.startsWith("/view_bet")) {
                String[] parts = messageText.split("\\s+");
                if (parts.length == 2) {
                    try {
                        Long id = Long.parseLong(parts[1]);
                        Bet bet = betService.getBet(id);
                        if (bet != null) {
                            response.setText("🔍 Bet Details:\n\n" + bet);
                        } else {
                            response.setText("❌ Bet not found with ID: " + id);
                        }
                    } catch (NumberFormatException e) {
                        response.setText("❌ Invalid bet ID. Please provide a number.");
                    }
                } else {
                    dialogData.setState(DialogState.WAITING_FOR_BET_ID);
                    response.setText("Please enter the bet ID:");
                }
            } else {
                dialogData.setState(DialogState.WAITING_FOR_BET_ID);
                response.setText("Please enter the bet ID:");
            }
        } else if (messageText.equals("✅ Make Decision") || messageText.startsWith("/decide")) {
            if (messageText.startsWith("/decide")) {
                String[] parts = messageText.split("\\s+");
                if (parts.length == 3) {
                    try {
                        Long id = Long.parseLong(parts[1]);
                        String decision = parts[2];
                        Bet bet = betService.decide(id, decision);
                        if (bet != null) {
                            response.setText("✅ Decision updated successfully!\n\n" + bet);
                        } else {
                            response.setText("❌ Bet not found with ID: " + id);
                        }
                    } catch (NumberFormatException e) {
                        response.setText("❌ Invalid bet ID. Please provide a number.");
                    }
                } else {
                    dialogData.setState(DialogState.WAITING_FOR_DECISION_ID);
                    response.setText("Please enter the bet ID:");
                }
            } else {
                dialogData.setState(DialogState.WAITING_FOR_DECISION_ID);
                response.setText("Please enter the bet ID:");
            }
        } else if (messageText.equals("📋 List Bets") || messageText.equals("/list_bets")) {
            var bets = betService.getAllBets();
            if (bets.isEmpty()) {
                response.setText("📋 No bets found.");
            } else {
                StringBuilder sb = new StringBuilder("📋 All Bets:\n\n");
                bets.forEach(bet -> sb.append(bet).append("\n\n"));
                response.setText(sb.toString());
            }
        } else if (messageText.equals("❓ Help") || messageText.startsWith("/help")) {
            response.setText(getHelpMessage());
        } else if (messageText.startsWith("/start")) {
            String welcomeMessage = "🎲 Welcome to the Betting Bot! 🎲\n\n" +
                    "Here are the available commands:\n\n" +
                    "📝 /create_bet <description> - Create a new bet\n" +
                    "   Example: /create_bet Who will win the match?\n\n" +
                    "🔍 /view_bet <id> - View bet details\n" +
                    "   Example: /view_bet 1\n\n" +
                    "✅ /decide <id> <decision> - Make a decision on a bet\n" +
                    "   Example: /decide 1 Win\n\n" +
                    "📋 /list_bets - List all bets\n\n" +
                    "❓ /help - Show this help message\n\n" +
                    "Use the keyboard below to interact with the bot!";
            
            response.setText(welcomeMessage);
        } else {
            // Обработка ввода данных в диалоговом режиме
            switch (dialogData.getState()) {
                case WAITING_FOR_BET_DESCRIPTION:
                    Bet bet = betService.createBet(messageText);
                    response.setText("✅ Bet created successfully!\n\n" + bet);
                    dialogData.setState(DialogState.NONE);
                    break;
                case WAITING_FOR_BET_ID:
                    try {
                        Long id = Long.parseLong(messageText);
                        Bet foundBet = betService.getBet(id);
                        if (foundBet != null) {
                            response.setText("🔍 Bet Details:\n\n" + foundBet);
                        } else {
                            response.setText("❌ Bet not found with ID: " + id);
                        }
                    } catch (NumberFormatException e) {
                        response.setText("❌ Invalid bet ID. Please provide a number.");
                    }
                    dialogData.setState(DialogState.NONE);
                    break;
                case WAITING_FOR_DECISION_ID:
                    try {
                        Long id = Long.parseLong(messageText);
                        dialogData.setBetId(id);
                        dialogData.setState(DialogState.WAITING_FOR_DECISION_TEXT);
                        response.setText("Please enter your decision:");
                    } catch (NumberFormatException e) {
                        response.setText("❌ Invalid bet ID. Please provide a number.");
                        dialogData.setState(DialogState.NONE);
                    }
                    break;
                case WAITING_FOR_DECISION_TEXT:
                    Bet updatedBet = betService.decide(dialogData.getBetId(), messageText);
                    if (updatedBet != null) {
                        response.setText("✅ Decision updated successfully!\n\n" + updatedBet);
                    } else {
                        response.setText("❌ Bet not found with ID: " + dialogData.getBetId());
                    }
                    dialogData.setState(DialogState.NONE);
                    break;
                default:
                    response.setText("❌ Unknown command. Use /help to see available commands.");
                    break;
            }
        }

        userDialogs.put(chatId, dialogData);
        response.setReplyMarkup(createKeyboard());
        return response;
    }

    private String getHelpMessage() {
        return "🎲 Betting Bot Commands 🎲\n\n" +
                "📝 /create_bet <description> - Create a new bet\n" +
                "   Example: /create_bet Who will win the match?\n\n" +
                "🔍 /view_bet <id> - View bet details\n" +
                "   Example: /view_bet 1\n\n" +
                "✅ /decide <id> <decision> - Make a decision on a bet\n" +
                "   Example: /decide 1 Win\n\n" +
                "📋 /list_bets - List all bets\n\n" +
                "❓ /help - Show this help message";
    }

    private ReplyKeyboardMarkup createKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // First row
        KeyboardRow row1 = new KeyboardRow();
        row1.add("📝 Create Bet");
        row1.add("🔍 View Bet");
        keyboard.add(row1);

        // Second row
        KeyboardRow row2 = new KeyboardRow();
        row2.add("✅ Make Decision");
        row2.add("📋 List Bets");
        keyboard.add(row2);

        // Third row
        KeyboardRow row3 = new KeyboardRow();
        row3.add("❓ Help");
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setSelective(true);

        return keyboardMarkup;
    }
} 