package com.example.telegrambots.service;

import com.example.telegrambots.command.Command;
import com.example.telegrambots.command.ParsedCommand;
import com.example.telegrambots.command.Parser;
import com.example.telegrambots.bot.Bot;
import com.example.telegrambots.options.*;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class MessageReciever implements Runnable {
    private static final Logger log = Logger.getLogger(MessageReciever.class);
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    private Bot bot;
    private Parser parser;

    public MessageReciever(Bot bot) {
        this.bot = bot;
        parser = new Parser(bot.getBotName());
    }

    @Override
    public void run() {
        log.info("[STARTED] MsgReciever.  Bot class: " + bot);
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                log.debug("New object for analyze in queue " + object.toString());
                analyze(object);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    private void analyze(Object object) {
        if (object instanceof Update) {
            Update update = (Update) object;
            log.debug("Update recieved: " + update.toString());
            analyzeForUpdate(update);
        } else log.warn("Cant operate type of object: " + object.toString());
    }

    private void analyzeForUpdate(Update update) {
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        ParsedCommand parsedCommand = parser.getParsedCommand(inputText);
        AbstractOption optionForCommand = getOptionForCommand(parsedCommand.getCommand());

        String operationResult = optionForCommand.operate(chatId.toString(), parsedCommand, update);

        if (!"".equals(operationResult)) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(operationResult);
            bot.sendQueue.add(message);
        }
    }

    private AbstractOption getOptionForCommand(Command command) {
        if (command == null) {
            log.warn("Null com.example.telegrambots.command accepted.");
            return new DefaultOption(bot);
        }
        switch (command) {
            case START:
            case HELP:
            case ID:
                SystemOption systemOption = new SystemOption(bot);
                log.info("Option for com.example.telegrambots.command[" + command.toString() + "] is: " + systemOption);
                return systemOption;
      
            case TRAIN:
                TrainOption trainOption=new TrainOption(bot);
                log.info("Option for com.example.telegrambots.command[" + command.toString() + "] is: " + trainOption);
                return trainOption;

            default:
                log.info("Option for com.example.telegrambots.command[" + command.toString() + "] not Set. Return DefaultOption");
                return new DefaultOption(bot);
        }
    }
}
