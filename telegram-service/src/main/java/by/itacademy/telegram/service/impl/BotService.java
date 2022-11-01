package by.itacademy.telegram.service.impl;

import by.itacademy.telegram.service.impl.commands.HelpCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotService extends TelegramLongPollingCommandBot {

    private final String botName;
    private final String botToken;

    public BotService(String botName, String botToken) {
        super();
        this.botName = botName;
        this.botToken = botToken;

        register(new HelpCommand("help", "Помощь"));
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void processNonCommandUpdate(Update update) {

    }
}
