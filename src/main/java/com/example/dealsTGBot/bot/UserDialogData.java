package com.example.dealsTGBot.bot;

import lombok.Data;

@Data
public class UserDialogData {
    private DialogState state = DialogState.NONE;
    private Long betId;
    private String description;
    private String decision;
} 