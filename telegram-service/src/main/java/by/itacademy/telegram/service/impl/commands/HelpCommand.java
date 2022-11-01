package by.itacademy.telegram.service.impl.commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand extends ServiceCommand {

    public HelpCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = user.getFirstName();

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Тут что-то информативное!!!");
    }
}
