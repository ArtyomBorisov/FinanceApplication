package by.itacademy.telegram.service.impl.commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends ServiceCommand {

    public StartCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = user.getFirstName();

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Приветствуем! Если нужна помощь, нажмите /help");
    }
}
