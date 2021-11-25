package com.kostapo.bot;

import com.kostapo.bot.controller.WebHookController;
import com.kostapo.bot.repository.QiwiRepository;
import com.kostapo.bot.repository.QiwiVklRepository;
import com.kostapo.bot.repository.UserRepository;
import com.kostapo.bot.repository.WithdrawRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

public class Bot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.webhookpath}")
    private String webHookPath;


    public Bot(DefaultBotOptions options) {
        super(options);
    }

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        return null;
    }








    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUsername = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

}
