package com.example.telegrambots.options;


import com.example.telegrambots.bot.Bot;
import com.example.telegrambots.command.ParsedCommand;
import org.telegram.telegrambots.api.objects.Update;

public abstract class AbstractOption {
    Bot bot;

    AbstractOption(Bot bot) {
        this.bot = bot;
    }

    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);
}

