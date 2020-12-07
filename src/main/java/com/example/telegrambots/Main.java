package com.example.telegrambots;

import com.example.telegrambots.bot.Bot;
import com.example.telegrambots.service.MessageReciever;
import com.example.telegrambots.service.MessageSender;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.send.SendMessage;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;
    private static final String BOT_ADMIN = "392503121";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        Bot bot = new Bot("JDUkraineBot", "");

        MessageReciever messageReciever = new MessageReciever(bot);
        MessageSender messageSender = new MessageSender(bot);

        bot.botConnect();

        Thread receiver = new Thread(messageReciever);
        receiver.setDaemon(true);
        receiver.setName("MsgReciever");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();

        sendStartReport(bot);
    }

    private static void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился!:)");
        bot.sendQueue.add(sendMessage);
    }
}
