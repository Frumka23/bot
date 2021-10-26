package com.kostapo.bot.controller;

import com.google.common.escape.UnicodeEscaper;
import com.kostapo.bot.Bot;
import com.kostapo.bot.model.Qiwi;
import com.kostapo.bot.model.User;
import com.kostapo.bot.model.Withdraw;
import com.kostapo.bot.repository.QiwiRepository;
import com.kostapo.bot.repository.UserRepository;
import com.kostapo.bot.repository.WithdrawRepository;
import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class WebHookController {
    private final Bot myBot;
    private final UserRepository userRepository;
    private final QiwiRepository qiwiRepository;
    private final WithdrawRepository withdrawRepository;
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    @Value("${bot.amountFirst}")
    private BigDecimal amountFirst;

    @Value("${bot.amountSecond}")
    private BigDecimal amountSecond;

    private Integer chatID = null;


    private final String startCommand = "*Авторизация*" +
            "\n" +
            "Чтобы авторизоваться в боте, " +
            "необходимо подписаться на:\n" +
            "\n" +
            "✔️ https://t.me/joinchat/RF1UFuYz6wZjYjBi\n" +
            "\n" +
            "_Подпишитесь, а затем нажмите на_ /start\"";

    private final String subCommand =
            "Чтобы авторизоваться в боте, " +
                    "необходимо подписаться на:\n" +
                    "\n" +
                    "✔️ https://t.me/joinchat/RF1UFuYz6wZjYjBi\n" +
                    "\n" +
                    "_Подпишитесь, а затем нажмите на_ /start";

    private final String lvlNull =
            "*Ваш уровень: 0*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 0.5 р за каждого реферала. \uD83D\uDCB8\n" +
                    "\n" +
                    "Получи *1 уровень*, чтобы открыть способность:\n" +
                    "+ 17.5 р за реферала \uD83D\uDCB5\n" +
                    "\n" +
                    "_или_\n" +
                    "\n" +
                    "Получи сразу *2 уровень*, чтобы открыть способность:\n" +
                    "+ 52 р за реферала \uD83D\uDCB0";

    private final String lvlOne =
            "*Ваш уровень: 1*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 17.5 р за реферала \uD83D\uDCB5\n" +
                    "\n" +
                    "Получи *2 уровень*, чтобы открыть способность:\n" +
                    "+ 52 р за реферала \uD83D\uDCB0";

    private final String lvlTwo =
            "*Ваш уровень: 2*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 52 р за реферала \uD83D\uDCB0";

    private final String infoPay =
            "Для повышения уровня необходимо сделать оплату на Qiwi кошелек\n" +
                    "\n" +
                    "Стоимость первого уровня: *99 рублей*\n" +
                    "Доход от каждого реферала будет: *17 рублей*\n" +
                    "\n" +
                    "Стоимость второго уровня: *249 рублей*\n" +
                    "Доход от каждого реферала будет: *52 рубля*";

    private final String infoPayOne =
            "Для повышения уровня необходимо сделать оплату на Qiwi кошелек\n" +
                    "\n" +
                    "Стоимость второго уровня: *249 рублей*\n" +
                    "Доход от каждого реферала будет: *52 рубля*";

    private final String infoPayTwo =
            "У вас максимальный уровень \uD83D\uDD25";

    private final String info =
            "*Правила проекта* \uD83D\uDCDD\n" +
                    "\n" +
                    "*1.* Запрещается любого вида накрутка, буксы, а также использование нескольких аккаунтов. ❌\n" +
                    "\n" +
                    "*2.* Запрещается оскорблять администрацию и участников чата, общаться неуважительно. ❌\n" +
                    "\n" +
                    "*3.* В чате запрещен мат, флуд и обмен ссылками. ❌\n" +
                    "\n" +
                    "❗️*Администрация оставляет за собой право заморозить баланс при наличии подозрений о накрутке и недобросовестности.*";

    private final String
            admin =
            "При возникновении спорных вопросов касаемо правил игры\n" +
                    "\n" +
                    "или\n" +
                    "\n" +
                    "При возникновении непредвиденных ситуаций\n" +
                    "\n" +
                    "Просьба обращаться к администрации проекта:\n" +
                    "\n" +
                    "t.me/pavel_erovlev";

    private final String menu =
            "*Заработай от* 0.5 р за каждого реферала \uD83D\uDCB8\n" +
                    "\n" +
                    "_Повышай уровень в разделе \"Прогресс\", чтобы зарабатывать еще больше._";


    public WebHookController(Bot myBot, UserRepository userRepository, QiwiRepository qiwiRepository, WithdrawRepository withdrawRepository) {
        this.myBot = myBot;
        this.userRepository = userRepository;
        this.qiwiRepository = qiwiRepository;
        this.withdrawRepository = withdrawRepository;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) throws URISyntaxException, TelegramApiException {


        String secretKey = "eyJ2ZXJzaW9uIjoiUDJQIiwiZGF0YSI6eyJwYXlpbl9tZXJjaGFudF9zaXRlX3VpZCI6ImtqNTA2dS0wMCIsInVzZXJfaWQiOiI3OTY0NDc3OTc3OSIsInNlY3JldCI6IjZiZDc4YzJhNDYzYTE0ODI2YjdkZDhiNjNkMzY2ZGM4YjE3MTJhYjY1MTMyZDRkZDBhYjFiZmFhNGI3NDc2MjYifX0=";
        BillPaymentClient client = BillPaymentClientFactory.createDefault(secretKey);
        Qiwi qiwi = new Qiwi();
        User user = new User();
        Withdraw withdraw = new Withdraw();
        Date date = new Date();
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        GetChatMember getChatMember = new GetChatMember();

        try {
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


            if (update.getMessage().getText().matches("/[start ]+[0-9]{1,15}")) {
                if (userRepository.findById(Math.toIntExact(update.getMessage().getChatId())).isPresent()) {
                    getChatMember.setChatId("-1001543433430");
                    getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                    ChatMember member = myBot.execute(getChatMember);
                    if (Objects.equals(member.getStatus(), "left")) {
                        return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                    } else {
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "_Проверка пройдена_ ✅").setParseMode(ParseMode.MARKDOWN));
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "_Воспользуйтесь главным меню_").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
                    }
                } else {
                    String[] ref;
                    ref = update.getMessage().getText().split(" ");
                    user.setId(Math.toIntExact(update.getMessage().getChatId()));
                    user.setChat_id(Math.toIntExact(update.getMessage().getChatId()));
                    user.setUser_name(update.getMessage().getChat().getUserName());
                    user.setReferral(ref[1]);
                    userRepository.save(user);
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    userRepository.updatePlusBalance(0.5, Math.toIntExact(Long.parseLong(ref[1])));
                    return new SendMessage(update.getMessage().getChatId(), startCommand).setParseMode(ParseMode.MARKDOWN);
                }
            } else if (Objects.equals(update.getMessage().getText(), "/start")) {
                if (userRepository.findById(Math.toIntExact(update.getMessage().getChatId())).isPresent()) {
                    getChatMember.setChatId("-1001543433430");
                    getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                    ChatMember member = myBot.execute(getChatMember);
                    if (Objects.equals(member.getStatus(), "left")) {
                        return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                    } else {
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "_Проверка пройдена_ ✅").setParseMode(ParseMode.MARKDOWN));
                    }
                } else {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    user.setId(Math.toIntExact(update.getMessage().getChatId()));
                    user.setChat_id(Math.toIntExact(update.getMessage().getChatId()));
                    user.setUser_name(update.getMessage().getChat().getUserName());
                    user.setReferral("1016547568");
                    userRepository.save(user);
                    return new SendMessage(update.getMessage().getChatId(), startCommand).setParseMode(ParseMode.MARKDOWN);
                }
            }
            if (update.getMessage().getText().matches("[0-9]{1,5}")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                if (Objects.equals(userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))), (double) 0)) {
                    return new SendMessage(update.getMessage().getChatId(), "*Недостаточно средств* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                if (Double.parseDouble(update.getMessage().getText()) <= userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) && !(Double.parseDouble(update.getMessage().getText()) == 0)) {
                    if (userRepository.getPurse(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) != null) {
                        sendMessage.setText(getMessage(update.getMessage().getText()));
                        withdraw.setAmount(Double.parseDouble(update.getMessage().getText()));
                        withdraw.setData(date.toString());
                        withdraw.setStatus("WAIT");
                        withdraw.setId_user(String.valueOf(update.getMessage().getChatId()));
                        withdraw.setPurse(String.valueOf(userRepository.getPurse(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                        withdrawRepository.save(withdraw);
                        userRepository.updateMinusBalance(Double.parseDouble(update.getMessage().getText()), Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "Ваш баланс: " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                        return new SendMessage(update.getMessage().getChatId(), "Заявка на вывод создана ✅ \nОжидайте выплаты в течении *24 часов*.").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else {
                        sendMessage.setText(getMessage(update.getMessage().getText()));
                        return new SendMessage(update.getMessage().getChatId(), "Пожалуйста *обновите* свой Qiwi кошелек \uD83D\uDD04").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                }
                if (Double.parseDouble(update.getMessage().getText()) > userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), "Вы ввели значение *больше*, чем имеется на балансе ❌\n" +
                            "*Понизьте сумму*.").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

            }
            if (update.getMessage().getText().matches("([0-9])([0-9]{3})([0-9]{3})([0-9]{4})")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                userRepository.updatePurse(update.getMessage().getText(), Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                return new SendMessage(update.getMessage().getChatId(), "_Кошелек успешно сохранен_ ☑️").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "\uD83D\uDDE3 Профиль")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                List<User> referralList = userRepository.findAllByReferral(String.valueOf(update.getMessage().getChatId()));
                return new SendMessage(update.getMessage().getChatId(), "*Уровень:* " + userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) +
                        "\n*Баланс:* " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) + " руб" +
                        "\n*Рефералы:* " + referralList.size()).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "\uD83D\uDCB0 Информация")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                return new SendMessage(update.getMessage().getChatId(), info).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "\uD83D\uDC65 Поддержка")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                return new SendMessage(update.getMessage().getChatId(), admin).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "\uD83D\uDD25 Повысить уровень")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                chatID = Math.toIntExact(update.getMessage().getChatId());
                if (Objects.equals(qiwiRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "WAITING")) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), "У вас имеется неоплаченный счет, проверьте").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                if (userRepository.getlevel(chatID) == 0) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), infoPay).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                } else if (userRepository.getlevel(chatID) == 1) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), infoPayOne).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                } else if (userRepository.getlevel(chatID) == 2) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), infoPayTwo).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
            }
            if (Objects.equals(update.getMessage().getText(), "\uD83D\uDD25 Прогресс")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) == 0) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), lvlNull).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) == 1) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), lvlOne).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) == 2) {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    return new SendMessage(update.getMessage().getChatId(), lvlTwo).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
            }
            if (Objects.equals(update.getMessage().getText(), "Обновить Qiwi кошелек")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                return new SendMessage(update.getMessage().getChatId(), "Введите свой номер Qiwi кошелька в формате " +
                        "\n *'79201234567'*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "Реферальная ссылка")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                myBot.execute(new SendMessage(update.getMessage().getChatId(), "*Ваша реферальная ссылка:* ").setParseMode(ParseMode.MARKDOWN));
                return new SendMessage(update.getMessage().getChatId(), "\nt.me/share/url?url=t.me/test_bot_j_p_bot?start=" + update.getMessage().getChatId() + "&text=Зацени!").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "Вывод средств")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                if (Objects.equals(userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))), (double) 0)) {
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "Ваш баланс: " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                    return new SendMessage(update.getMessage().getChatId(), "*Недостаточно средств* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                myBot.execute(new SendMessage(update.getMessage().getChatId(), "Ваш баланс: " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                return new SendMessage(update.getMessage().getChatId(), "Пожалуйста, введите сумму для вывода").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "Получить 1 уровень")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                CreateBillInfo billInfo = new CreateBillInfo(
                        UUID.randomUUID().toString(),
                        new MoneyAmount(
                                BigDecimal.valueOf(9),
                                Currency.getInstance("RUB")
                        ),
                        String.valueOf(update.getMessage().getChatId()),
                        ZonedDateTime.now().plusDays(0).plusHours(1),
                        new Customer(
                                "pasandreg@gmail.com",
                                UUID.randomUUID().toString(),
                                "79123456789"
                        ),
                        "t.me/test_bot_j_p_bot"
                );
                BillResponse response = client.createBill(billInfo);
                qiwi.setIdPay(response.getBillId());
                qiwi.setAmount(Double.parseDouble(String.valueOf(response.getAmount().getValue())));
                qiwi.setStatus(String.valueOf(response.getStatus().getValue().getValue()));
                qiwi.setData(response.getCreationDateTime());
                qiwi.setUser_id(response.getComment());
                qiwiRepository.save(qiwi);
                System.out.println(response.getPayUrl());
                System.out.println(response);
                return new SendMessage(update.getMessage().getChatId(), "Стоимость 1 уровня составляет 99 рублей. \n" +
                        "Для оплаты перейдите по ссылке и произведите оплату.\n" +
                        "После чего ожидайте зачисления, либо проверьте с помощью команды.\n\n" +
                        response.getPayUrl()).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "Получить 2 уровень")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                CreateBillInfo billInfo = new CreateBillInfo(
                        UUID.randomUUID().toString(),
                        new MoneyAmount(
                                BigDecimal.valueOf(24),
                                Currency.getInstance("RUB")
                        ),
                        String.valueOf(update.getMessage().getChatId()),
                        ZonedDateTime.now().plusDays(0).plusHours(1),
                        new Customer(
                                "pasandreg@gmail.com",
                                UUID.randomUUID().toString(),
                                "79123456789"
                        ),
                        "t.me/test_bot_j_p_bot"
                );
                BillResponse response = client.createBill(billInfo);
                qiwi.setIdPay(response.getBillId());
                qiwi.setAmount(Double.parseDouble(String.valueOf(response.getAmount().getValue())));
                qiwi.setStatus(String.valueOf(response.getStatus().getValue().getValue()));
                qiwi.setData(response.getCreationDateTime());
                qiwi.setUser_id(response.getComment());
                qiwiRepository.save(qiwi);
                System.out.println(response.getPayUrl());
                System.out.println(response);
                return new SendMessage(update.getMessage().getChatId(), "Стоимость 2 уровня составляет 249 рублей. \n" +
                        "Для оплаты перейдите по ссылке и произведите оплату.\n" +
                        "После чего ожидайте зачисления, либо проверьте с помощью команды.\n\n" +
                        response.getPayUrl()).setReplyMarkup(replyKeyboardMarkup);
            }
            if (Objects.equals(update.getMessage().getText(), "Проверить оплату \uD83C\uDD97")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                if (qiwiRepository.findByBillId(String.valueOf(update.getMessage().getChatId())) == null) {
                    return new SendMessage(update.getMessage().getChatId(), "Новой оплаты не поступало ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                String billId = qiwiRepository.findByBillId(String.valueOf(update.getMessage().getChatId()));
                BillResponse response = client.getBillInfo(billId);
                if (Objects.equals(response.getStatus().getValue().getValue(), "PAID") & Objects.equals(response.getAmount().getValue(), amountFirst)) {
                    if (Objects.equals(qiwiRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "PAID")) {
                        return new SendMessage(update.getMessage().getChatId(), "Новой оплаты не поступало ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else {
                        System.out.println(response);
                        String ref = userRepository.getReferral(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        qiwiRepository.updateStatus(billId);
                        userRepository.updateLvL_1(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        userRepository.updatePayment(amountFirst, Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        if (userRepository.getlevel(Integer.valueOf(ref)) == 0) {
                            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                                    "\n" +
                                    "Ваш уровень: " + userRepository.getlevel(Integer.parseInt(ref)) +
                                    "\n" +
                                    "\n" +
                                    "*Зачислено:* 0.0 рублей").setParseMode(ParseMode.MARKDOWN));
                            myBot.execute(new SendMessage(ref, "Повысьте себе уровень и *зарабатывайте* больше \uD83D\uDCB0").setParseMode(ParseMode.MARKDOWN));
                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                                    "Ваш уровень повышен до 1").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        } else if (userRepository.getlevel(Integer.valueOf(ref)) == 1) {
                            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                                    "\n" +
                                    "Ваш уровень: " + userRepository.getlevel(Integer.parseInt(ref)) +
                                    "\n" +
                                    "\n" +
                                    "*Зачислено:* 17.0 рублей").setParseMode(ParseMode.MARKDOWN));
                            userRepository.updatePlusBalance(17.0, Integer.parseInt(ref));
                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                                    "Ваш уровень повышен до 1").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        } else if (userRepository.getlevel(Integer.valueOf(ref)) == 2) {
                            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                                    "\n" +
                                    "Ваш уровень: " + userRepository.getlevel(Integer.parseInt(ref)) +
                                    "\n" +
                                    "\n" +
                                    "**Зачислено:* 52.0 рублей*").setParseMode(ParseMode.MARKDOWN));
                            userRepository.updatePlusBalance(52.0, Integer.parseInt(ref));
                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                                    "Ваш уровень повышен до 1").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }

                    }
                } else if (Objects.equals(response.getStatus().getValue().getValue(), "PAID") & Objects.equals(response.getAmount().getValue(), amountSecond)) {
                    if (Objects.equals(qiwiRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "PAID")) {
                        return new SendMessage(update.getMessage().getChatId(), "Новой оплаты не поступало ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else {
                        sendMessage.setText(getMessage(update.getMessage().getText()));
                        System.out.println(response);
                        qiwiRepository.updateStatus(billId);
                        userRepository.updateLvL_2(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        String ref = userRepository.getReferral(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        userRepository.updatePayment(amountSecond, Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                        if (userRepository.getlevel(Integer.valueOf(ref)) == 0) {
                            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                                    "\n" +
                                    "Ваш уровень: " + userRepository.getlevel(Integer.parseInt(ref)) +
                                    "\n" +
                                    "\n" +
                                    "*Зачислено:* 0.0 рублей").setParseMode(ParseMode.MARKDOWN));
                            myBot.execute(new SendMessage(ref, "Повысьте себе уровень и *зарабатывайте* больше \uD83D\uDCB0").setParseMode(ParseMode.MARKDOWN));
                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                                    "Ваш уровень повышен до 2").setReplyMarkup(replyKeyboardMarkup).setParseMode(ParseMode.MARKDOWN);
                        } else if (userRepository.getlevel(Integer.valueOf(ref)) == 1) {
                            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                                    "\n" +
                                    "Ваш уровень: " + userRepository.getlevel(Integer.parseInt(ref)) +
                                    "\n" +
                                    "\n" +
                                    "*Зачислено:* 17.0 рублей").setParseMode(ParseMode.MARKDOWN));
                            userRepository.updatePlusBalance(17.0, Integer.parseInt(ref));
                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                                    "Ваш уровень повышен до 2").setReplyMarkup(replyKeyboardMarkup).setParseMode(ParseMode.MARKDOWN);
                        } else if (userRepository.getlevel(Integer.valueOf(ref)) == 2) {
                            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                                    "\n" +
                                    "Ваш уровень: " + userRepository.getlevel(Integer.parseInt(ref)) +
                                    "\n" +
                                    "\n" +
                                    "*Зачислено:* 52.0 рублей").setParseMode(ParseMode.MARKDOWN));
                            userRepository.updatePlusBalance(52.0, Integer.parseInt(ref));
                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                                    "Ваш уровень повышен до 2").setReplyMarkup(replyKeyboardMarkup).setParseMode(ParseMode.MARKDOWN);
                        }
                    }
                } else {
                    sendMessage.setText(getMessage(update.getMessage().getText()));
                    System.out.println(response);
                    return new SendMessage(update.getMessage().getChatId(), "Оплата *не пройдена* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
            }
            if (Objects.equals(update.getMessage().getText(), "Главное меню")) {
                getChatMember.setChatId("-1001543433430");
                getChatMember.setUserId(Math.toIntExact(update.getMessage().getChatId()));
                ChatMember member = myBot.execute(getChatMember);
                if (Objects.equals(member.getStatus(), "left")) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }
                sendMessage.setText(getMessage(update.getMessage().getText()));
                return new SendMessage(update.getMessage().getChatId(), menu).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
            }
            InputFile file = new InputFile();
            if (Objects.equals(update.getMessage().getText(), "Рассылка")) {

                file.setMedia(new File("https://www.youtube.com/watch?v=iJJyt2ORxkw&ab_channel=Spinnin%27Records"),"VideoOne");
                System.out.println(file.getAttachName());
                System.out.println(file.getNewMediaFile());
                System.out.println(file.getMediaName());
                System.out.println(file.getNewMediaStream());
                myBot.execute(new SendVideo().setChatId(update.getMessage().getChatId()).
                        setVideo("VideoOne"));
            }

            if (Objects.equals(update.getMessage().getText(), "Рассылка1")) {
                System.out.println(file.isNew());
                System.out.println(file.getAttachName());
                System.out.println(file.getNewMediaFile());
                System.out.println(file.getMediaName());
                System.out.println(file.getNewMediaStream());
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return new SendMessage(update.getMessage().getChatId(), "Воспользуйтесь главным меню!").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
    }

    @Scheduled(fixedDelay = 60000)
    void deleteChatMessagesAutomatically() {
        ZonedDateTime now = ZonedDateTime.now().minusHours(1);
        List<Qiwi> list = qiwiRepository.Data(now);
        qiwiRepository.deleteAll(list);
        System.out.println(now);
    }

    public String getMessage(String msg) {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        if (msg.equals("/start") | msg.equals("Главное меню")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("\uD83D\uDD25 Прогресс");
            keyboardFirstRow.add("\uD83D\uDDE3 Профиль");
            keyboardSecondRow.add("\uD83D\uDCB0 Информация");
            keyboardSecondRow.add("\uD83D\uDC65 Поддержка");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "Выбрать...";
        }
        if (msg.equals("\uD83D\uDD25 Прогресс")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("\uD83D\uDD25 Повысить уровень");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83D\uDD25 Повысить уровень")) {
            keyboard.clear();
            keyboardFirstRow.clear();

            if (Objects.equals(qiwiRepository.findStatus(String.valueOf(chatID)), "WAITING")) {
                keyboardFirstRow.add("Проверить оплату \uD83C\uDD97");
                keyboardSecondRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
            } else if (userRepository.getlevel(chatID) == 0) {
                keyboardFirstRow.add("Получить 1 уровень");
                keyboardFirstRow.add("Получить 2 уровень");
                keyboardSecondRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
            } else if (userRepository.getlevel(chatID) == 1) {
                keyboardFirstRow.add("Получить 2 уровень");
                keyboardSecondRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
            } else if (userRepository.getlevel(chatID) == 2) {
                keyboardFirstRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
            }
            return "...";
        }
        if (msg.equals("Получить 1 уровень")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Проверить оплату \uD83C\uDD97");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("Получить 2 уровень")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Проверить оплату \uD83C\uDD97");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83D\uDDE3 Профиль")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Реферальная ссылка");
            keyboardFirstRow.add("Вывод средств");
            keyboardSecondRow.add("Обновить Qiwi кошелек");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83D\uDC65 Поддержка")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("Информация")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }

        return "Выбрать...";
    }


}




