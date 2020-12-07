package com.example.telegrambots.options;

import com.example.telegrambots.command.ParsedCommand;
import com.example.telegrambots.bot.Bot;
import org.telegram.telegrambots.api.objects.Update;

public class DefaultOption extends AbstractOption {

    public DefaultOption(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        return "";
    }
}
