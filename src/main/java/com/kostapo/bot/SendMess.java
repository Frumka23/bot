package com.kostapo.bot;

import com.kostapo.bot.repository.UserRepository;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

public class SendMess {

    UserRepository userRepository;
    GetChatMember getChatMember = new GetChatMember();
    Bot myBot;
    private final String subCommand =
            "Чтобы авторизоваться в боте, " +
                    "необходимо подписаться на:\n" +
                    "\n" +
                    "✔️ https://t.me/utngroup\n" +
                    "\n" +
                    "_Подпишитесь, а затем нажмите на_ /start";
    public BotApiMethod<?> CheckOptions(Update update) throws TelegramApiException {

        if (update.getMessage().getFrom().getBot()) {
            userRepository.ban(true, Math.toIntExact(update.getMessage().getChatId()));
            return new SendMessage(update.getMessage().getChatId(), "*Вы забанены за нарушение прав.*").setParseMode(ParseMode.MARKDOWN);
        }
        if (userRepository.findById(Math.toIntExact(update.getMessage().getChatId())).isPresent() && userRepository.getBan(Math.toIntExact(update.getMessage().getChatId()))) {
            return new SendMessage(update.getMessage().getChatId(), "*Вы забанены за нарушение прав.*").setParseMode(ParseMode.MARKDOWN);
        }
        if (update.getMessage().getText() == null) {
            return null;
        }
        getChatMember.setChatId("-1001617807188");
        getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
        ChatMember member = myBot.execute(getChatMember);
        if (Objects.equals(member.getStatus(), "left")) {
            return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
        }

        return null;
    }



}
