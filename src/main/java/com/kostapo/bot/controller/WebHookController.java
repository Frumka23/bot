package com.kostapo.bot.controller;

import com.kostapo.bot.Bot;
import com.kostapo.bot.SendMess;
import com.kostapo.bot.model.Qiwi;
import com.kostapo.bot.model.QiwiVkl;
import com.kostapo.bot.model.User;
import com.kostapo.bot.model.Withdraw;
import com.kostapo.bot.repository.QiwiRepository;
import com.kostapo.bot.repository.QiwiVklRepository;
import com.kostapo.bot.repository.UserRepository;
import com.kostapo.bot.repository.WithdrawRepository;
import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.io.File;
import java.math.BigDecimal;
import java.net.*;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class WebHookController {
    private static final Logger log = Logger.getLogger(WebHookController.class.getName());

    private final Bot myBot;
    private final UserRepository userRepository;
    private final QiwiRepository qiwiRepository;
    private final QiwiVklRepository qiwiVklRepository;
    private final WithdrawRepository withdrawRepository;
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
    InlineKeyboardMarkup inlineKeyboardMarkup3 = new InlineKeyboardMarkup();

    @Value("${bot.amountFirst}")
    private BigDecimal amountFirst;

    @Value("${bot.amountSecond}")
    private BigDecimal amountSecond;

    @Value("${bot.amountTre}")
    private BigDecimal amountTre;

    @Value("${bot.amountFour}")
    private BigDecimal amountFour;

    @Value("${bot.amountFive}")
    private BigDecimal amountFive;

    @Value("${bot.amountSix}")
    private BigDecimal amountSix;

    @Value("${bot.amountSeven}")
    private BigDecimal amountSeven;

    @Value("${bot.amountEight}")
    private BigDecimal amountEight;

    @Value("${bot.amountNine}")
    private BigDecimal amountNine;

    @Value("${bot.amountTen}")
    private BigDecimal amountTen;


    private final String startCommand = "*Авторизация*" +
            "\n" +
            "Чтобы авторизоваться в боте, " +
            "необходимо подписаться на:\n" +
            "\n" +
            "✅ https://t.me/utngroup\n" +
            "\n" +
            "\uD83D\uDE80 *Подпишитесь, а затем нажмите на* \uD83D\uDC49 /start";

    private final String subCommand =
            "Чтобы авторизоваться в боте, " +
                    "необходимо подписаться на:\n" +
                    "\n" +
                    "✅ https://t.me/utngroup\n" +
                    "\n" +
                    "\uD83D\uDE80 *Подпишитесь, а затем нажмите на* \uD83D\uDC49 /start";

    private final String lvlNull =
            "*Ваш уровень: 0*\n" +
                    "\n" +
                    "*1 уровень*, открывает возможность:\n" +
                    "+ 49 р за реферала \uD83D\uDCB5\n\n" +
                    "\n" +
                    "\uD83D\uDC4CПри оплате первого уровня, ваш баланс будет пополнен на 49 руб" +
                    "\n" +
                    "\n" +
                    "❗️*Важно!* После оплаты первого уровня, вам так же *будет выдан логин и пароль* для входа в личный кабинет на сайте.\n" +
                    "\n✅ Вы сможете удобно управлять своими финансами после запуска криптовалюты, весь *баланс по вкладу* будет автоматически продублирован на сайте в вашем ЛК.\n" +
                    "\n" +
                    "\uD83D\uDCB0*Инвестируй в криптовалюту с “Unit Coin“*";

    private final String lvlOne =
            "*Ваш уровень: 1*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 49 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*2 уровень*:\n" +
                    "+ 99 р за реферала \uD83D\uDCB0\n";

    private final String lvlTwo =
            "*Ваш уровень: 2*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 99 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*3 уровень*:\n" +
                    "+ 149 р за реферала \uD83D\uDCB0\n";

    private final String lvlThree =
            "*Ваш уровень: 3*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 149 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*4 уровень*:\n" +
                    "+ 199 р за реферала \uD83D\uDCB0\n";

    private final String lvlFour =
            "*Ваш уровень: 4*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 199 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*5 уровень*:\n" +
                    "+ 249 р за реферала \uD83D\uDCB0\n";

    private final String lvlFive =
            "*Ваш уровень: 5*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 249 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*6 уровень*:\n" +
                    "+ 299 р за реферала \uD83D\uDCB0\n";

    private final String lvlSix =
            "*Ваш уровень: 6*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 299 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*7 уровень*:\n" +
                    "+ 349 р за реферала \uD83D\uDCB0\n";

    private final String lvlSeven =
            "*Ваш уровень: 7*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 349 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*8 уровень*:\n" +
                    "+ 399 р за реферала \uD83D\uDCB0\n";

    private final String lvlEight =
            "*Ваш уровень: 8*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 399 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*9 уровень*:\n" +
                    "+ 449 р за реферала \uD83D\uDCB0\n";

    private final String lvlNine =
            "*Ваш уровень: 9*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 449 р за реферала \uD83D\uDCB0\n" +
                    "\n\n" +
                    "*10 уровень*:\n" +
                    "+ 499 р за реферала \uD83D\uDCB0\n";

    private final String lvlTen =
            "*Ваш уровень: 10*\n" +
                    "\n" +
                    "*Сейчас вам доступно:* \n" +
                    "+ 499 р за реферала \uD83D\uDCB0\n";


    private final String infoPay =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость первого уровня: 149 рублей\n" +
                    "Доход от каждого реферала соответствующего уровня: 49 рублей\n" +
                    "\n" +
                    "\n" +
                    "Так же начислим 49 приветственных рублей!";

    private final String infoPayOne =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость второго уровня: *299 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *99 рублей*";

    private final String infoPayTwo =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость третьего уровня: *449 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *149 рубля*";

    private final String infoPayThree =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "Стоимость четвертого уровня: *599 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *199 рубля*";

    private final String infoPayFour =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость пятого уровня: *749 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *249 рубля*";

    private final String infoPayFive =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость шестого уровня: *899 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *299 рубля*";

    private final String infoPaySix =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость седьмого уровня: *1049 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *349 рубля*";

    private final String infoPaySeven =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость восьмого уровня: *1199 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *399 рубля*";

    private final String infoPayEight =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость девятого уровня: *1349 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *449 рубля*";

    private final String infoPayNine =
            "\uD83D\uDD36Для повышения уровня необходимо произвести оплату на Qiwi кошелек\n" +
                    "\uD83C\uDFE6Оплата поддерживается всеми банками\n" +
                    "\n" +
                    "Стоимость десятого уровня: *1499 рублей*\n" +
                    "Доход от каждого реферала соответствующего уровня: *499 рубля*";

    private final String infoPayTen =
            "У вас максимальный уровень \uD83D\uDD25";

    private final String info =
            "*📃 Правила проекта* \uD83D\uDCDD\n" +
                    "\n" +
                    "*1.* Запрещается любого вида накрутка, буксы, а также использование нескольких аккаунтов. ❌\n" +
                    "\n" +
                    "*2.* Запрещается оскорблять администрацию и участников чата, общаться неуважительно. ❌\n" +
                    "\n" +
                    "*3.* В чате запрещен мат, флуд и обмен ссылками. ❌\n" +
                    "\n" +
                    "❗️*Администрация оставляет за собой право заморозить баланс при наличии подозрений о накрутке и недобросовестности.*";

    private final String info2 =
            "*Партнер, изучи главную статью в обязательном порядке* \uD83D\uDCDD\n" +
                    "https://telegra.ph/UNIT-GROUP-11-04";

    private final String
            admin =
            "\uD83D\uDC68\u200D\uD83D\uDCBBГорячая линия службы поддержки ответит на все ваши вопросы в рабочие часы.\n" +
                    "\uD83D\uDCC6 Семь дней в неделю, с 8:00 до 18:00 по Московскому времени.\n" +
                    "\n" +
                    "t.me/unitsupport";

    private final String menu =
            "\uD83D\uDD25*Зарабатывай от 49 руб за каждого реферала.*\n" +
                    "\n" +
                    "\uD83E\uDE99*Unit Coin* - Это криптовалюта, разработка которой осуществляется на территории РФ. \n" +
                    "\n" +
                    "\uD83E\uDD16*Данный бот был создан с единственной целью:\n" +
                    "- Окружить криптовалюту надёжными партнерами*_, за счёт универсальной схемы " +
                    "реферального сотрудничества (Позови друга, получи деньги)\n" +
                    "- *Бонусным жестом* для партнёров является возможность инвестировать бюджет до выхода криптовалюты " +
                    "(Предварительные продажи по стартовой цене пройдут для нашей партнёрской сети).  " +
                    "ICO будет через несколько дней после официального релиза монеты._\n";

    private final String menu2 =
            "\uD83D\uDC68\u200D\uD83D\uDCBBСмотреть обзор проекта \n";


    public WebHookController(Bot myBot, UserRepository userRepository, QiwiRepository qiwiRepository, WithdrawRepository withdrawRepository, QiwiVklRepository qiwiVklRepository) {
        this.myBot = myBot;
        this.userRepository = userRepository;
        this.qiwiRepository = qiwiRepository;
        this.withdrawRepository = withdrawRepository;
        this.qiwiVklRepository = qiwiVklRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Object onUpdateReceived(@RequestBody Update update) throws URISyntaxException, TelegramApiException {
        if(update.getMessage() == null && update.getInlineQuery() == null && update.getChosenInlineQuery() == null && update.getCallbackQuery() == null && update.getEditedMessage() == null && update.getChannelPost() == null && update.getEditedChannelPost() == null && update.getShippingQuery() == null && update.getPreCheckoutQuery() == null){
            System.out.println(ConsoleColors.RED + "Null сообщение" + ConsoleColors.RESET);
            return null;
        }

        String secretKey = "eyJ2ZXJzaW9uIjoiUDJQIiwiZGF0YSI6eyJwYXlpbl9tZXJjaGFudF9zaXRlX3VpZCI6ImtqNTA2dS0wMCIsInVzZXJfaWQiOiI3OTY0NDc3OTc3OSIsInNlY3JldCI6IjZiZDc4YzJhNDYzYTE0ODI2YjdkZDhiNjNkMzY2ZGM4YjE3MTJhYjY1MTMyZDRkZDBhYjFiZmFhNGI3NDc2MjYifX0=";
        BillPaymentClient client = BillPaymentClientFactory.createDefault(secretKey);
        Support support = new Support(myBot, qiwiVklRepository, qiwiRepository, userRepository);
        Qiwi qiwi = new Qiwi();
        User user = new User();
        QiwiVkl qiwiVkl = new QiwiVkl();
        Withdraw withdraw = new Withdraw();
        Date date = new Date();
        GetChatMember getChatMember = new GetChatMember();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Перейти к видео");
        inlineKeyboardButton2.setText("Смотреть видео");
        inlineKeyboardButton3.setText("Смотреть видео");
        inlineKeyboardButton1.setCallbackData("video");
        inlineKeyboardButton2.setCallbackData("ViewVideo");
        inlineKeyboardButton3.setCallbackData("ViewVideo2");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList2 = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList3 = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList2.add(keyboardButtonsRow2);
        rowList3.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);
        inlineKeyboardMarkup2.setKeyboard(rowList2);
        inlineKeyboardMarkup3.setKeyboard(rowList3);
        if (update.hasMessage()) {

            if (update.getMessage().getText().matches("/[start ]+[0-9]{1,15}")) {

                if (userRepository.findById(Math.toIntExact(update.getMessage().getChatId())).isPresent()) {
                    if (chekBan(update)) {
                        return new SendMessage(update.getMessage().getChatId(), "*Вы забанены за нарушение прав.*").setParseMode(ParseMode.MARKDOWN);
                    }
                    if (chekSub(update)) {
                        return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                    }
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "_Проверка пройдена_ ✅").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "_Воспользуйтесь главным меню_").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
                } else {
                    String[] reff;
                    reff = update.getMessage().getText().split(" ");
                    String reff_2 = userRepository.getReferral(Integer.parseInt(reff[1]));
                    user.setId(Math.toIntExact(update.getMessage().getChatId()));
                    user.setChat_id(Math.toIntExact(update.getMessage().getChatId()));
                    user.setUser_name(update.getMessage().getChat().getUserName());
                    user.setReferral(reff[1]);
                    user.setReferral_2(reff_2);
                    userRepository.save(user);
                    return new SendMessage(update.getMessage().getChatId(), startCommand).setParseMode(ParseMode.MARKDOWN);
                }
            } else if (Objects.equals(update.getMessage().getText(), "/start")) {

                if (userRepository.findById(Math.toIntExact(update.getMessage().getChatId())).isPresent()) {
                    if (chekBan(update)) {
                        return new SendMessage(update.getMessage().getChatId(), "*Вы забанены за нарушение прав.*").setParseMode(ParseMode.MARKDOWN);
                    }
                    if (chekSub(update)) {
                        return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                    }
                    getMessage(update.getMessage().getText(),update);
                    userRepository.updateBlockFalse(Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "*Проверка пройдена* ✅").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                } else {
                    user.setId(Math.toIntExact(update.getMessage().getChatId()));
                    user.setChat_id(Math.toIntExact(update.getMessage().getChatId()));
                    user.setUser_name(update.getMessage().getChat().getUserName());
                    user.setReferral("1016547568");
                    user.setReferral_2("1016547568");
                    userRepository.save(user);

                    return new SendMessage(update.getMessage().getChatId(), startCommand).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
            }


        }
        Long chatGroup = -1001617807188L;

        try {

            if(update.hasChannelPost()){
                return null;
            }
            if (update.hasCallbackQuery()) {
                if (update.getCallbackQuery().getData().equals("video")) {
                    long chatid = update.getCallbackQuery().getMessage().getChatId();
                    userRepository.updateLastMessage("🎥 Видеообзор бота", Math.toIntExact(chatid));
                    return new SendMessage(chatid, "*В разработке*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                if (update.getCallbackQuery().getData().equals("ViewVideo")) {
                    long chatid = update.getCallbackQuery().getMessage().getChatId();
                    userRepository.updateLastMessage("Cмотреть видео", Math.toIntExact(chatid));
                    return new SendMessage(chatid, "*В разработке*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }
                if (update.getCallbackQuery().getData().equals("ViewVideo2")) {
                    long chatid = update.getCallbackQuery().getMessage().getChatId();
                    userRepository.updateLastMessage("Cмотреть видео2", Math.toIntExact(chatid));
                    return new SendMessage(chatid, "*В разработке*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

            }
            if (update.hasMessage()) {
                getMessage(update.getMessage().getText(),update);
                List<String> referralList_lvl_10 = userRepository.ref_lvl_10(String.valueOf(update.getMessage().getChatId()));
                List<String> referralList_2_lvl_5 = userRepository.ref_2_lvl_5(String.valueOf(update.getMessage().getChatId()));
                Integer chatID = Math.toIntExact(update.getMessage().getChatId());
                int lvl = userRepository.getlevel(chatID);
                String ref = userRepository.getReferral(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                String ref_2 = userRepository.getReferral_2(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));

                if (chekBan(update)) {
                    return new SendMessage(update.getMessage().getChatId(), "*Вы забанены за нарушение прав.*").setParseMode(ParseMode.MARKDOWN);
                }
                if (chekSub(update)) {
                    return new SendMessage(update.getMessage().getChatId(), subCommand).setParseMode(ParseMode.MARKDOWN);
                }

                if(referralList_lvl_10.size() >= 4 && referralList_2_lvl_5.size() >= 8 && userRepository.getTop(chatID) == 3.0){
                    userRepository.updatePlusUnt(0.3,chatID);
                    userRepository.updatePlusUnt(0.03,Math.toIntExact(Long.parseLong(ref)));
                    log.info("Пользователь " + chatID + " достиг уровня 3 в Топ партнёре");
                    myBot.execute(new SendMessage(1016547568L, "Пользователь с id: " + update.getMessage().getChatId() +
                            "\nNickname: " + userRepository.getUsername(chatID) +
                            "\nДостиг уровня 3 в Топ партнёре"));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "\uD83E\uDD29*Поздравляем! " +
                            "Ты достиг третьего уровня в категории \"ТОП партнёр\"*\n" +
                            "\n" +
                            "\uD83D\uDCB0Твоя награда составляет - *0.3 UTN* \n" +
                            "\n" +
                            "\uD83D\uDD25_Теперь тебе доступны 12% по вкладу " +
                            "(как максимальный портфель, с соблюдением стандартных условий)_\n" +
                            "\n" +
                            "\uD83D\uDC8EСледующий уровень, даёт тебе высший статус среди партнёров сети!" +
                            " А так же *1 UTN* на счёт, и ещё несколько привилегий о которых тебе расскажет" +
                            " глава отдела по работе с партнёрами.\n" +
                            "\n" +
                            "\uD83E\uDD17 *Твой наставник поучает 10%* от суммы твоей премии 0.03  UTN," +
                            " ты сделал внушительный бонус себе и своему товарищу. " +
                            "Каждый из твоих партнёров может сделать такой же подарок тебе." +
                            " Всё в твоих руках.\n" +
                            "\n" +
                            "\uD83D\uDE4B _Твоя партнёрская сеть благодарна тебе. " +
                            "Масштабируй её, помоги новым партнёрам выйти на самый высокий уровень." +
                            " Все мы заинтересованы в росте, масштабе возможностей и финансов._\n" +
                            "\n" +
                            "\uD83D\uDCA5*Ещё раз поздравляем тебя! В канале будет опубликована твоя команда," +
                            " главой которой ты являешься! Следи за новостями*\uD83D\uDE09").setParseMode(ParseMode.MARKDOWN)) ;
                    userRepository.updateTop(3.1, chatID);

                }

                if(referralList_lvl_10.size() >= 3 && referralList_2_lvl_5.size() >= 6 && userRepository.getTop(chatID) == 2.0){
                    userRepository.updatePlusUnt(0.2,chatID);
                    userRepository.updatePlusUnt(0.02,Math.toIntExact(Long.parseLong(ref)));
                    log.info("Пользователь " + chatID + " достиг уровня 2 в Топ партнёре");
                    myBot.execute(new SendMessage(1016547568L, "Пользователь с id: " + update.getMessage().getChatId() +
                            "\nNickname: " + userRepository.getUsername(chatID) +
                            "\nДостиг уровня 2 в Топ партнёре"));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "\uD83E\uDD29*Поздравляем! " +
                            "Ты достиг второго уровня в категории \"ТОП партнёр\"*\n" +
                            "\n" +
                            "\uD83D\uDCB0Твоя награда составляет - *0.2 UTN* \n" +
                            "\n" +
                            "\uD83D\uDD25_Ты двигаешься очень стремительными темпами," +
                            " до 3 уровня осталось пару рывков_. \n" +
                            "\n" +
                            "\uD83E\uDD17 *Твой наставник поучает 10%* от суммы твоей премии 0.02  UTN," +
                            " ты сделал внушительный бонус себе и своему товарищу." +
                            " Каждый из твоих партнёров может сделать такой же подарок тебе." +
                            " Всё в твоих руках.\n" +
                            "\n" +
                            "\uD83D\uDE4B _Твоя партнёрская сеть благодарна тебе." +
                            " Масштабируй её, помоги новым партнёрам выйти на самый высокий уровень." +
                            " Все мы заинтересованы в росте, масштабе возможностей и финансов._\n" +
                            "\n" +
                            "\uD83D\uDCA5*Ещё раз поздравляем тебя! В канале будет опубликована твоя команда," +
                            " главой которой ты являешься! Следи за новостями*\uD83D\uDE09").setParseMode(ParseMode.MARKDOWN)) ;
                    userRepository.updateTop(2.1, chatID);

                }

                if(referralList_lvl_10.size() >= 2 && referralList_2_lvl_5.size() >= 4 && userRepository.getTop(chatID) == 1.0){
                    userRepository.updatePlusUnt(0.1,chatID);
                    userRepository.updatePlusUnt(0.01,Math.toIntExact(Long.parseLong(ref)));
                    log.info("Пользователь " + chatID + " достиг уровня 1 в Топ партнёре");
                    myBot.execute(new SendMessage(1016547568L, "Пользователь с id: " + update.getMessage().getChatId() +
                            "\nNickname: " + userRepository.getUsername(chatID) +
                            "\nДостиг уровня 1 в Топ партнёре"));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "\uD83E\uDD29*Поздравляем!" +
                            " Ты достиг первого уровня в категории \"ТОП партнёр\"*\n" +
                            "\uD83D\uDCB0Твоя награда составляет - *0.1 UTN* \n" +
                            "\n" +
                            "\uD83D\uDD25_Это первый, но очень важный шаг на пути к статусу DIAMOND (Бриллиант)._\n" +
                            "\n" +
                            "\uD83E\uDD17 *Твой наставник получает 10%* от суммы твоей премии," +
                            " ты сделал внушительный бонус себе и своему товарищу." +
                            " Каждый из твоих партнёров может сделать такой же подарок тебе. Всё в твоих руках.\n" +
                            "\n" +
                            "\uD83D\uDE4B _Твоя партнёрская сеть благодарна тебе." +
                            " Масштабируй её, помоги новым партнёрам выйти на самый высокий уровень." +
                            " Все мы заинтересованы в росте, масштабе возможностей и финансов._\n" +
                            "\n" +
                            "\uD83D\uDCA5*Ещё раз поздравляем тебя! В канале будет опубликована твоя команда," +
                            " главой которой ты являешься! Следи за новостями*\uD83D\uDE09").setParseMode(ParseMode.MARKDOWN));

                    userRepository.updateTop(1.1, chatID);
                }

                if (update.getMessage().getText().matches("[0-9]{1,6}")) {

                    if (Objects.equals(userRepository.getLastMessage(Math.toIntExact(update.getMessage().getChatId())), "💸 Пополнить баланс")) {
                        log.info("Пользователь " + chatID + " начал пополнение баланса на сумму " + update.getMessage().getText() + " рублей");
                        return new SendMessage(update.getMessage().getChatId(), "\uD83D\uDCE5<i>Для поплнения кошелька на сумму</i> " + Long.parseLong(update.getMessage().getText()) + " <i>перейдите по ссылке и произведите оплату.</i>\n" +
                                "<i>⏳После чего ожидайте зачисления или нажмите на кнопку</i> <b>\"Проверить оплату\"</b>\n" +
                                "\uD83D\uDCF2<b>Ссылка на оплату Qiwi:</b>\n" +
                                support.CreateBillVkl(update)).setReplyMarkup(replyKeyboardMarkup).setParseMode(ParseMode.HTML);
                    } else if (Objects.equals(userRepository.getLastMessage(Math.toIntExact(update.getMessage().getChatId())), "💴 Вывод средств")) {
                        if (Objects.equals(userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))), (double) 0)) {
                            return new SendMessage(update.getMessage().getChatId(), "*Недостаточно средств* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }
                        if (Double.parseDouble(update.getMessage().getText()) <= userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) && !(Double.parseDouble(update.getMessage().getText()) == 0)) {
                            if (userRepository.getPurse(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) != null) {
                                Double sum = Double.parseDouble(update.getMessage().getText());
                                Double div = Double.parseDouble(update.getMessage().getText()) / 100 * 4.5;
                                Double div1 = Double.parseDouble(update.getMessage().getText()) / 100 * 4;
                                Double div2 = Double.parseDouble(update.getMessage().getText()) / 100 * 3.5;
                                Double div3 = Double.parseDouble(update.getMessage().getText()) / 100 * 3;
                                Double sumIt = sum - div;
                                Double sumIt1 = sum - div1;
                                Double sumIt2 = sum - div2;
                                Double sumIt3 = sum - div3;
                                Double sumV = 0.0;
                                Double procent = 0.0;
                                Double procent1 = 0.0;
                                if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) <= 2) {
                                    withdraw.setAmount(sumIt);
                                    sumV = sumIt;
                                    procent = 4.5;
                                    procent1 = div;
                                } else if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) <= 5) {
                                    withdraw.setAmount(sumIt1);
                                    sumV = sumIt1;
                                    procent = 4.0;
                                    procent1 = div1;
                                } else if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) <= 8) {
                                    withdraw.setAmount(sumIt2);
                                    sumV = sumIt2;
                                    procent = 3.5;
                                    procent1 = div2;
                                } else if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) <= 10) {
                                    withdraw.setAmount(sumIt3);
                                    sumV = sumIt3;
                                    procent = 3.0;
                                    procent1 = div3;
                                }


                                withdraw.setData(date.toString());
                                withdraw.setStatus("WAIT");
                                withdraw.setId_user(String.valueOf(update.getMessage().getChatId()));
                                withdraw.setPurse(String.valueOf(userRepository.getPurse(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                                withdrawRepository.save(withdraw);
                                userRepository.updateMinusBalance(Double.parseDouble(update.getMessage().getText()), Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                                log.info("Пользователь " + chatID + " создал заявку на вывод в размере " + sumV + " рублей");

                                myBot.execute(new SendMessage("1016547568", "Пользователь: *" + userRepository.getUsername(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))
                                        + "* создал заявку на вывод № " + withdrawRepository.getIdDraw(String.valueOf(update.getMessage().getChatId()))
                                        + "\nСумма вывода: " + sumV).setParseMode(ParseMode.MARKDOWN));
                                return new SendMessage(update.getMessage().getChatId(), "Сумма вывода: " + sumV + " руб." +
                                        "\nКомиссия составила: " + procent1 + " руб. (" + procent + " %)" +
                                        "\nВаш баланс: " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) +
                                        "\n\nЗаявка на вывод создана ✅ \nОжидайте выплаты в течении *24 часов*.").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                            } else {

                                return new SendMessage(update.getMessage().getChatId(), "Пожалуйста *обновите* свой Qiwi кошелек \uD83D\uDD04").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                            }
                        }
                        if (Double.parseDouble(update.getMessage().getText()) > userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))) {

                            return new SendMessage(update.getMessage().getChatId(), "Вы ввели значение *больше*, чем имеется на балансе ❌\n" +
                                    "*Понизьте сумму*.").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }
                    } else if (Objects.equals(userRepository.getLastMessage(Math.toIntExact(update.getMessage().getChatId())), "💰 Перевод средств на вклад")) {
                        if (Double.parseDouble(update.getMessage().getText()) > userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))) {

                            return new SendMessage(update.getMessage().getChatId(), "Вы ввели значение *больше*, чем имеется на балансе ❌\n" +
                                    "*Понизьте сумму*.").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }
                        if (Objects.equals(userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))), (double) 0)) {
                            return new SendMessage(update.getMessage().getChatId(), "*Недостаточно средств* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }
                        if (Double.parseDouble(update.getMessage().getText()) <= userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) && !(Double.parseDouble(update.getMessage().getText()) == 0)) {

                            Double sum = Double.parseDouble(update.getMessage().getText());
                            Double div = Double.parseDouble(update.getMessage().getText()) / 100 * 2;
                            Double div1 = div / 100 * 75;
                            Double div2 = div / 100 * 25;
                            Double sumIt = sum - div;
                            int chatId = Math.toIntExact(update.getMessage().getChatId());

                            userRepository.updateMinusBalance(sum, chatId);
                            userRepository.updatePlusBalanceVkl(sumIt, chatId);
                            userRepository.updatePlusBalance(div1, Math.toIntExact(Long.parseLong(ref)));
                            userRepository.updatePlusBalance(div2, Math.toIntExact(Long.parseLong(ref_2)));
                            log.info("Пользователь " + chatID + " перевел деньги на вклад: " + sumIt + " рублей");

                            myBot.execute(new SendMessage(ref, "\uD83D\uDD25*Ваш реферал перевел деньги на вклад*" +
                                    "\n\n\uD83D\uDCB8_1.5% его перевода теперь Ваши_" +
                                    "\n\nЗачислено: " + div1 + " руб.")
                                    .setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));

                            myBot.execute(new SendMessage(ref_2, "\uD83D\uDD25*Реферал Вашего реферал перевел деньги на вклад*" +
                                    "\n\n\uD83D\uDCB8_0.5% его перевода теперь Ваши_" +
                                    "\n\nЗачислено: " + div2 + " руб.")
                                    .setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));

                            return new SendMessage(update.getMessage().getChatId(), "Баланс по вкладу пополнен на " + sumIt + " руб." +
                                    "\nКомиссия составила: " + div + " руб." +
                                    "\n\n\uD83C\uDFE6Баланс по вкладу: " + userRepository.getBalanceVkl(Math.toIntExact(update.getMessage().getChatId())) +
                                    "\n\uD83D\uDE4B\u200D♂️Баланс за рефералов: " + userRepository.getBalance(Math.toIntExact(update.getMessage().getChatId())))
                                    .setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }


                    }
                }

                if (update.getMessage().getText().matches("([0-9])([0-9]{3})([0-9]{3})([0-9]{4})")) {
                    userRepository.updatePurse(update.getMessage().getText(), Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                    log.info("Пользователь " + chatID + " обновил свой кошелек");
                    return new SendMessage(update.getMessage().getChatId(), "_Кошелек успешно сохранен_ ☑️").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "✅ Профиль") | Objects.equals(update.getMessage().getText(), "Назад \uD83D\uDD19") | Objects.equals(update.getMessage().getText(), "Назад↩️")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Профиль'");

                    userRepository.updateLastMessage("✅ Профиль", Math.toIntExact(update.getMessage().getChatId()));

                    String url = "https://edge.qiwi.com/sinap/crossRates";

                    URL obj = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                    connection.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String a = response.toString().substring(response.indexOf("{\"set\":\"General\",\"from\":\"643\",\"to\":\"840\",\"rate\":") + 48, response.indexOf("},{\"set\":\"General\",\"from\":\"643\",\"to\":\"972\",\"rate\":"));
                    System.out.println(a);
                    Double usdcon = Double.parseDouble(a);
                    Double USD = userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) / usdcon;
                    Double USD_VKL = userRepository.getBalanceVkl(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) / usdcon;

                    double roundOff = Math.round(USD * 100.0) / 100.0;
                    double roundOffVkl = Math.round(USD_VKL * 100.0) / 100.0;
                    List<User> referralList = userRepository.findAllByReferral(String.valueOf(update.getMessage().getChatId()));
                    List<String> referralList_2 = userRepository.ref_2_lvl(String.valueOf(update.getMessage().getChatId()));
                    DecimalFormat df = new DecimalFormat("###.##");

                    return new SendMessage(update.getMessage().getChatId(), "*Ваш уровень:* " + userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) + " \uD83D\uDD1D" +
                            "\n\n*Баланс:* \n"
                            + df.format(userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))) + " руб\n"
                            + roundOff + " usd" +
                            "\n" + df.format(userRepository.getUnt(chatID))  + " UNT" +
                            "\n\n*Баланс по вкладу:*\n" +
                            df.format(userRepository.getBalanceVkl(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))) + " руб\n"
                            + roundOffVkl + " usd" +
                            "\n\n*Рефералы 1 уровня:* " + referralList.size() + " _( С 10 уровнем: " + referralList_lvl_10.size() +" )_"
                            + "\n\n*Рефералы 2 уровня:* " + referralList_2.size() + " _( С 5 уровнем и выше: " + referralList_2_lvl_5.size() +" )_").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "ℹ️Информация")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Информация'");

                    userRepository.updateLastMessage("ℹ️Информация", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), info2).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "📃 Правила проекта")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Правила проекта'");

                    return new SendMessage(update.getMessage().getChatId(), info).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "📱 Регистрация Qiwi")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Регистрация Qiwi'");

                    return new SendMessage(update.getMessage().getChatId(), "\uD83C\uDFC6*QIWI входит в топ онлайн банков России и СНГ.*\n" +
                            "\n" +
                            "⏳*Регистрация займёт не более 1 минуты*_, а вывод средств в любой банк " +
                            "осуществляется с минимальной комиссией  2%, но выводить не обязательно!_\n" +
                            "\n" +
                            "\uD83D\uDCF2_Наша команда рекомендует выпустить виртуальную карту_ *QIWI*_, для оплаты всех покупок._ \n" +
                            "\n" +
                            "✅_Зачем?  Лучшая кэшбек система (возврата средств за покупки).\n" +
                            "Мы делаем сервис удобным и выгодным для каждого партнёра \"Unit Cash\"_\n" +
                            "\n" +
                            "\uD83D\uDE80*Зарегистрировать QIWI кошелёк сейчас*\n" +
                            "Реферальная ссылка").setReplyMarkup(replyKeyboardMarkup).setParseMode(ParseMode.MARKDOWN);

                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDC68\u200D\uD83D\uDCBB Поддержка")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Поддержка'");

                    userRepository.updateLastMessage("\uD83D\uDC68\u200D\uD83D\uDCBB Поддержка", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), admin).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDD25 Повысить уровень")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Повысить уровень'");

                    userRepository.updateLastMessage("\uD83D\uDD25 Повысить уровень", Math.toIntExact(update.getMessage().getChatId()));
                    if (Objects.equals(qiwiRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "WAITING")) {
                        return new SendMessage(update.getMessage().getChatId(), "У вас имеется неоплаченный счет, проверьте").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                    if (userRepository.getlevel(chatID) == 0) {
                        return new SendMessage(update.getMessage().getChatId(), infoPay).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 1) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayOne).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 2) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayTwo).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 3) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayThree).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 4) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayFour).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 5) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayFive).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 6) {
                        return new SendMessage(update.getMessage().getChatId(), infoPaySix).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 7) {
                        return new SendMessage(update.getMessage().getChatId(), infoPaySeven).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 8) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayEight).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 9) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayNine).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatID) == 10) {
                        return new SendMessage(update.getMessage().getChatId(), infoPayTen).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDD25 Партнерство") | Objects.equals(update.getMessage().getText(), "Назад ◀")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Партнерство'");

                    userRepository.updateLastMessage("\uD83D\uDD25 Партнерство", Math.toIntExact(update.getMessage().getChatId()));
                    File file = new File("C:\\Users\\79644\\Desktop\\SpringTelegramBot-master\\SpringTelegramBot-master\\src\\main\\resources\\static\\images\\34.png");
                    myBot.execute(new SendPhoto().setChatId(update.getMessage().getChatId()).setPhoto(file).setCaption("Выплаты партнёрам за рефералов"));
                    if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) == 0) {
                        return new SendMessage(update.getMessage().getChatId(), lvlNull).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                    int lvlNext = lvl + 1;
                    int amount;
                    int amountNext = 0;
                    switch (lvl){
                        case 1:
                            amount = 49;
                            amountNext = 99;
                            break;
                        case 2:
                            amount = 99;
                            amountNext =149;
                            break;
                        case 3:
                            amount = 149;
                            amountNext = 199;
                            break;
                        case 4:
                            amount = 199;
                            amountNext = 249;
                            break;
                        case 5:
                            amount = 249;
                            amountNext = 299;
                            break;
                        case 6:
                            amount = 299;
                            amountNext = 349;
                        break;
                        case 7:
                            amount = 349;
                            amountNext = 399;
                        break;
                        case 8:
                            amount = 399;
                            amountNext = 449;
                        break;
                        case 9:
                            amount = 449;
                            amountNext = 499;
                            break;
                        case 10:
                            amount = 499;
                            break;
                        default:
                            throw new IllegalStateException("Несуществующее значение: " + lvl);
                    }
                    if (userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))) == 10) {
                        return new SendMessage(update.getMessage().getChatId(), lvlTen).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else {
                        return new SendMessage(update.getMessage().getChatId(), "*Ваш уровень: " + lvl + "*\n" +
                                "\n" +
                                "*Сейчас вам доступно:* \n" +
                                "+ " + amount +" руб. за реферала \uD83D\uDCB0\n" +
                                "\n\n" +
                                "*" + lvlNext + " уровень*:\n" +
                                "+ " + amountNext + " руб. за реферала \uD83D\uDCB0\n").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDE0E Топ партнерам")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Топ партнерам'");

                    userRepository.updateLastMessage("\uD83D\uDE0E Топ партнерам", Math.toIntExact(update.getMessage().getChatId()));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "✅ *Цель данного блока* в том, чтобы научить наших топовых партнеров презентовать продукт, понимать цели и задачи, делать правильные акценты.\n" +
                            "\n" +
                            "\uD83D\uDD25 Будет раскрыто много секретов и методик. \n" +
                            "\uD83D\uDC4C Данные познания помогут приблизить твой финансовый достаток.\n" +
                            "\uD83D\uDCAF Мы расскажем, как сформировать фундамент и приумножить его кратно.\n" +
                            "\n" +
                            "*Первый лайфхак.*\n" +
                            "Поделись вводным материалом, в формате видео\n" +
                            "_Ссылка_\n" +
                            "\n" +
                            "\uD83D\uDC4CТак часть основной интеллектуальной нагрузки оно возьмёт на себя. \uD83C\uDFAFТебе останется грамотно ответить на поставленные вопросы и акцентировать внимание на деталях.\n" +
                            "\n" +
                            "⚡️*Успехов. Мы заинтересованы в твоём достатке не меньше чем ты и это нас объединяет.*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
                    return new SendMessage(update.getMessage().getChatId(), "\uD83C\uDFA5 Видеообзор для наших партнеров").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(inlineKeyboardMarkup3);
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDCB0 Пассивный доход") | Objects.equals(update.getMessage().getText(), "💲 Покупка UnitCoin") | Objects.equals(update.getMessage().getText(), "Назад⏪")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Пассивный доход'");

                    userRepository.updateLastMessage("\uD83D\uDCB0 Пассивный доход", Math.toIntExact(update.getMessage().getChatId()));
                    DecimalFormat df = new DecimalFormat("###.##");
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "\uD83D\uDCB0Пассивный доход с \"UnitCash\"\n" +
                            "\uD83D\uDCC8Вклад каждого партнёра формирует курс \"Unit Coin\" инвестируй и приумножай свой капитал за счёт роста криптовалюты.\n" +
                            "\n" +
                            "\uD83C\uDFE6Баланс по вкладу: " + df.format(userRepository.getBalanceVkl(Math.toIntExact(update.getMessage().getChatId())))  +
                            "\n\uD83D\uDE4B\u200D♂️Баланс за рефералов: " + df.format(userRepository.getBalance(Math.toIntExact(update.getMessage().getChatId())))  +
                            "\n\n" +
                            "\uD83D\uDCF2Вы можете перевести баланс с счёта за рефералов на счёт по вкладу (Кнопка) \n" +
                            "\"\uD83D\uDCB0 Перевод средств на вклад\" комиссия 2%.\n" +
                            "\n" +
                            "❌По тех причинам пассивный доход временно недоступен\n" +
                            "✅Автоматически (с момента перезапуска системы) баланс по вкладу начинает работать.\n" +
                            "\n" +
                            "\uD83D\uDC4CСразу после перезапуска направления \"Пассивный доход\" мы известим всех партнёров в канале и личным сообщением!")
                            .setParseMode(ParseMode.MARKDOWN)
                            .setReplyMarkup(replyKeyboardMarkup));
                    return new SendMessage(update.getMessage().getChatId(), "\uD83D\uDC68\u200D\uD83D\uDCBBПодробный видеообзор\n")
                            .setParseMode(ParseMode.MARKDOWN)
                            .setReplyMarkup(inlineKeyboardMarkup2);
                }

                if (Objects.equals(update.getMessage().getText(), "Проверить оплату \uD83C\uDD97")) {
                    log.info("Пользователь " + chatID + " проверяет оплату уровня");

                    userRepository.updateLastMessage("Проверить оплату \uD83C\uDD97", Math.toIntExact(update.getMessage().getChatId()));
                    if(chekPay(update)){
                       return new SendMessage(update.getMessage().getChatId(), "Новой оплаты не поступало ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                    String amount;
                    String allAmount = "0";
                    switch (lvl){
                        case 0:
                            amount = "149.00";
                            break;
                        case 1:
                            amount = "299.00";
                            break;
                        case 2:
                            amount = "449.00";
                            break;
                        case 3:
                            amount = "599.00";
                            allAmount = "6969.00";
                            break;
                        case 4:
                            amount = "749.00";
                            allAmount = "6399.00";
                            break;
                        case 5:
                            amount = "899.00";
                            allAmount = "5699.00";
                            break;
                        case 6:
                            amount = "1049.00";
                            allAmount = "4799.00";
                            break;
                        case 7:
                            amount = "1199.00";
                            allAmount = "3839.00";
                            break;
                        case 8:
                            amount = "1349.00";
                            allAmount = "2699.00";
                            break;
                        case 9:
                            amount = "1499.00";
                            break;
                        default:
                            throw new IllegalStateException("Несуществующее значение: " + lvl);
                    }
                    support.sendMessageBuy(update,replyKeyboardMarkup,amount,allAmount);
                    return null;
                }

                if (Objects.equals(update.getMessage().getText(), "🔄 Обновить Qiwi кошелек")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Обновить Qiwi кошелек'");

                    userRepository.updateLastMessage("🔄 Обновить Qiwi кошелек", Math.toIntExact(update.getMessage().getChatId()));
                    if (userRepository.getPurse(Math.toIntExact(update.getMessage().getChatId())) == null) {
                        return new SendMessage(update.getMessage().getChatId(), "Введите свой номер Qiwi кошелька в формате " +
                                "\n *79201234567*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    } else {
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "Ваш кошелек: " +
                                userRepository.getPurse(Math.toIntExact(update.getMessage().getChatId()))).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
                        return new SendMessage(update.getMessage().getChatId(), "Введите свой номер Qiwi кошелька в формате " +
                                "\n *79201234567*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                }

                if (Objects.equals(update.getMessage().getText(), "🤝 Реферальная ссылка")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Реферальная ссылка'");

                    int chatId = Math.toIntExact(update.getMessage().getChatId());
                    userRepository.updateLastMessage("🤝 Реферальная ссылка", Math.toIntExact(update.getMessage().getChatId()));
                    if (userRepository.getlevel(chatId) == 1 && userRepository.getBalance(chatId) > 48) {
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "❗️*Важно*❗️\n" +
                                "\uD83D\uDE4C _Прежде чем рекомендовать другу подписаться на наш проект," +
                                " убедись лично в том, что всё работает и ты можешь без проблем вывести деньги!_\n" +
                                "\n" +
                                "\uD83D\uDE04 _Нет мы не сомневаемся в платёжной системе," +
                                " просто тебе самому нужно быть уверенным в фактах," +
                                " тем более лучше всего если ты_ *предоставишь реальные доказательства*" +
                                " _работоспособности системы_ *в виде скриншота* _вывода средств._ \n" +
                                "\n" +
                                "\uD83C\uDFE6 _Уже сейчас ты можешь вывести свои первые деньги." +
                                " На данный момент у тебя на счету " + userRepository.getBalance(chatId) + " рублей, " +
                                "пусть это не большая сумма, но этого будет более" +
                                " чем достаточно для проведения теста._ \n" +
                                "\n" +
                                "\uD83D\uDCE3 *Как быстро масштабировать партнёрскую сеть?*" +
                                " _Для этого ничего не нужно придумывать, мы уже всё передумали заблаговременно!" +
                                " Просто перейди в блок \"Топовым партнёрам\" это то что тебе точно нужно знать!_").setParseMode(ParseMode.MARKDOWN));

                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "\uD83D\uDD25 *Ваша реферальная ссылка:* ").setParseMode(ParseMode.MARKDOWN));
                        return new SendMessage(update.getMessage().getChatId(), "\nt.me/unitcash_bot?start=" + update.getMessage().getChatId()).setReplyMarkup(replyKeyboardMarkup);

                    } else if (userRepository.getlevel(chatId) == 0) {
                        return new SendMessage(update.getMessage().getChatId(), "Для получения реферальной ссылки, необходимо повысить уровень до 1").setReplyMarkup(replyKeyboardMarkup);
                    } else if (userRepository.getlevel(chatId) > 1) {
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "\uD83D\uDD25 *Ваша реферальная ссылка:* ").setParseMode(ParseMode.MARKDOWN));
                        return new SendMessage(update.getMessage().getChatId(), "\nt.me/unitcash_bot?start=" + update.getMessage().getChatId()).setReplyMarkup(replyKeyboardMarkup);

                    }
                }

                if (Objects.equals(update.getMessage().getText(), "💴 Вывод средств")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Вывод средств'");

                    userRepository.updateLastMessage("💴 Вывод средств", Math.toIntExact(update.getMessage().getChatId()));
                    if (Objects.equals(userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId()))), (double) 0)) {
                        myBot.execute(new SendMessage(update.getMessage().getChatId(), "Ваш баланс: " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                        return new SendMessage(update.getMessage().getChatId(), "*Недостаточно средств* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "Комиссия на вывод средств составляет:\n" +
                            "Уровень (1 - 2) 4,5%\n" +
                            "Уровень (3 - 5) 4%\n" +
                            "Уровень (6 - 8) 3,5%\n" +
                            "Уровень (9 - 10) 3%"));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "Ваш баланс: " + userRepository.getBalance(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))
                            + "\nВаш уровень: " + userRepository.getlevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId())))));
                    return new SendMessage(update.getMessage().getChatId(), "Пожалуйста, введите сумму для вывода").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83C\uDD99 Получить " + (lvl + 1) + " уровень")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Получить Х уровень'");

                    return support.sendMessageBuyLvl(update, replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDD1D Получить все уровни")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Получить все уровни'");

                    return support.sendMessageBuyAllLvl(update, replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Главное меню")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Главное меню'");

                    userRepository.updateLastMessage("Главное меню", Math.toIntExact(update.getMessage().getChatId()));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), menu).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
                    return new SendMessage(update.getMessage().getChatId(), menu2).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(inlineKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Списать с баланса")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Списать с баланса'");

                    SendMess send = new SendMess();
                    send.CheckOptions(update);
                    userRepository.updateLastMessage("Списать с баланса", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "*В разработке*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Сгенерировать QR код")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Сгенерировать QR код'");

                    int chatId = Math.toIntExact(update.getMessage().getChatId());
                    userRepository.updateLastMessage("Сгенерировать QR код", Math.toIntExact(update.getMessage().getChatId()));
                    File file = QRCode.from("t.me/unitcash_bot?start=" + update.getMessage().getChatId()).to(ImageType.PNG)
                            .withSize(200, 200)
                            .file();
                    if (userRepository.getlevel(chatId) == 0) {
                        return new SendMessage(update.getMessage().getChatId(), "Для получения QR кода, необходимо повысить уровень до 1").setReplyMarkup(replyKeyboardMarkup);
                    } else {
                        myBot.execute(new SendPhoto().setChatId(update.getMessage().getChatId()).setPhoto(file));
                        return new SendMessage(update.getMessage().getChatId(), "Ваша 🤝 Реферальная ссылка теперь в QR-коде").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }

                
                /*if (userRepository.intScore() < 10) {
                    userRepository.setIntScore();
                } else {
                    userRepository.updateIntScore(0);
                }
                System.out.println(userRepository.intScore());*/

                }

                if (Objects.equals(update.getMessage().getText(), "ID")) {
                    userRepository.updateLastMessage("ID", Math.toIntExact(update.getMessage().getChatId()));

                    System.out.println(userRepository.findAll());
                    List<User> users = userRepository.findAll();
                    System.out.println(users);
                    System.out.println(users.toString());

                    return support.sendMessage(Math.toIntExact(update.getMessage().getChatId()), "*Ваш ID:* " + update.getMessage().getChatId());
                }

                if (Objects.equals(update.getMessage().getText(), "\uD83D\uDCB2Баланс")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Баланс'");

                    userRepository.updateLastMessage("Баланс", Math.toIntExact(update.getMessage().getChatId()));
                    DecimalFormat df = new DecimalFormat("###.##");
                    return new SendMessage(update.getMessage().getChatId(), "\uD83D\uDE4B\u200D♂️*Баланс за рефералов: *" +
                            df.format(userRepository.getBalance(Math.toIntExact(update.getMessage().getChatId()))) +
                            "\n\uD83C\uDFE6*Баланс по вкладу: *" +
                            df.format(userRepository.getBalanceVkl(Math.toIntExact(update.getMessage().getChatId()))) +
                            "\n\n\uD83D\uDCB5*Как ты можешь распорядиться своими средствами?*\n" +
                            "\n" +
                            "*1 - Повысить свой уровень в 2 клика* (При наличии средств) тем самым прибыль за " +
                            "каждый новый уровень твоего реферала становится больше и достигает максимума на 10 уровне! " +
                            "Не нужно закидывать деньги с карты, просто масштабируй партнёрскую сеть, получай выплаты, " +
                            "*пользуйся ресурсами грамотно!*\n" +
                            "\n" +
                            "*2 - Вывести деньги себе на карту* с комиссией соответствующей твоему уровню:\n" +
                            "Уровень (1 - 2) 4,5%\n" +
                            "Уровень (3 - 5) 4%\n" +
                            "Уровень (6 - 8) 3,5%\n" +
                            "Уровень (9 - 10) 3%\n" +
                            "\n" +
                            "*3 - Перевести деньги на вклад и получать пассивный доход ежемесячно*, " +
                            "увеличиваю свою процентную ставку с 6% до 10% каждый месяц по 0,5% в месяц " +
                            "(Больше информации и выгодных предложений в статье \"Пассивный доход\" кнопка " +
                            "\"Пассивный доход\")").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "💸 Пополнить баланс")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Пополнить баланс'");

                    if (Objects.equals(qiwiVklRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "WAITING")) {

                        return new SendMessage(update.getMessage().getChatId(), "У вас имеется неоплаченный счет, проверьте").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                    userRepository.updateLastMessage("💸 Пополнить баланс", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "Введите сумму для зачисления").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "🎥 Видеообзор бота")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Видеообзор бота'");

                    userRepository.updateLastMessage("🎥 Видеообзор бота", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "*В разработке*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "📖 Читать статью")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Читать статью'");

                    userRepository.updateLastMessage("📖 Читать статью", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "https://telegra.ph/Navyk-prodavat-11-05").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "💰 Перевод средств на вклад")) {
                    log.info("Пользователь " + chatID + " перешел на вкладку 'Перевод средст на вклад'");

                    userRepository.updateLastMessage("💰 Перевод средств на вклад", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "Укажите, какую сумму вы хотели бы перевести на вклад?" +
                            "\n\n\uD83D\uDE4B\u200D♂️Ваш баланс за рефералов: " + userRepository.getBalance(Math.toIntExact(update.getMessage().getChatId()))).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Проверить оплату")) {
                    log.info("Пользователь " + chatID + " проверяет оплату на вклад");

                    userRepository.updateLastMessage("Проверить оплату", Math.toIntExact(update.getMessage().getChatId()));
                    if (qiwiVklRepository.findByBillId(String.valueOf(update.getMessage().getChatId())) == null) {
                        return new SendMessage(update.getMessage().getChatId(), "Новой оплаты не поступало ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                    }
                    String billId = qiwiVklRepository.findByBillId(String.valueOf(update.getMessage().getChatId()));
                    BillResponse response = client.getBillInfo(billId);
                    if (Objects.equals(response.getStatus().getValue().getValue(), "PAID")) {
                        if (Objects.equals(qiwiVklRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "PAID")) {
                            return new SendMessage(update.getMessage().getChatId(), "Новой оплаты не поступало ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        } else {
                            String sum = qiwiVklRepository.sumBalanceVkl(billId);
                            System.out.println(response);
                            qiwiVklRepository.updateStatus(billId);
                            userRepository.updatePlusBalanceVkl(Double.parseDouble(sum), Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
                            userRepository.updatePayment(BigDecimal.valueOf(Double.parseDouble(sum)), Integer.parseInt(String.valueOf(update.getMessage().getChatId())));

                            return new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                        }
                    }


                    return new SendMessage(update.getMessage().getChatId(), "*Оплата не пройдена ❌*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Администрирование") & Objects.equals(update.getMessage().getChatId(), 1016547568L)) {
                    userRepository.updateLastMessage("Администрирование", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "Администратор: " + userRepository.getUsername(Math.toIntExact(update.getMessage().getChatId()))).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Рассылка") & Objects.equals(update.getMessage().getChatId(), 1016547568L)) {
                    userRepository.updateLastMessage("/send", Math.toIntExact(update.getMessage().getChatId()));
                    return new SendMessage(update.getMessage().getChatId(), "Напишите сообщение для рассылки").setReplyMarkup(replyKeyboardMarkup);
                }

                if (update.getMessage().getText().matches("[\\s\\S\\w\\W\\d\\D]+") & Objects.equals(update.getMessage().getChatId(), 1016547568L) & Objects.equals(userRepository.getLastMessage(1016547568), "/send")) {
                    userRepository.updateLastMessage("send", Math.toIntExact(update.getMessage().getChatId()));
                    List<Integer> listUserBan = userRepository.findUserBan();
                    int userNumber = 0;
                    try {

                        myBot.execute(new SendMessage(1016547568L, "*Отправка сообщений*").setParseMode(ParseMode.MARKDOWN));
                        for (Integer person : listUserBan) {
                            try {
                                userNumber++;
                                myBot.execute(new SendMessage(String.valueOf(person), update.getMessage().getText()).setParseMode(ParseMode.MARKDOWN));
                                System.out.println(ConsoleColors.GREEN + "Отправка пользователям  :: " + userNumber + " из " + listUserBan.size() + ConsoleColors.RESET);
                                Thread.sleep(40);
                            } catch (TelegramApiException ex){
                                System.out.println("Пользователь: " + person + " заблокировал бота");
                                userRepository.updateBlockTrue(person);
                            }


                        }
                    } catch (NullPointerException | InterruptedException e) {
                        myBot.execute(new SendMessage(1016547568L, "*Отправка не удалась*").setParseMode(ParseMode.MARKDOWN));

                        e.printStackTrace();
                    }
                    return new SendMessage(update.getMessage().getChatId(), "*Отправка завершена*").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Пользователи") & Objects.equals(update.getMessage().getChatId(), 1016547568L)) {
                    userRepository.updateLastMessage("Пользователи", Math.toIntExact(update.getMessage().getChatId()));
                    List<Integer> listId = userRepository.findId();
                    List<Integer> listLvl0 = userRepository.findLvl(0);
                    List<Integer> listLvl1 = userRepository.findLvl(1);
                    List<Integer> listLvl2 = userRepository.findLvl(2);
                    List<Integer> listLvl3 = userRepository.findLvl(3);
                    List<Integer> listLvl4 = userRepository.findLvl(4);
                    List<Integer> listLvl5 = userRepository.findLvl(5);
                    List<Integer> listLvl6 = userRepository.findLvl(6);
                    List<Integer> listLvl7 = userRepository.findLvl(7);
                    List<Integer> listLvl8 = userRepository.findLvl(8);
                    List<Integer> listLvl9 = userRepository.findLvl(9);
                    List<Integer> listLvl10 = userRepository.findLvl(10);

                    return new SendMessage(update.getMessage().getChatId(), "Количество пользователей: " + listId.size()
                            + "\n\nC 0 уровнем: " + listLvl0.size()
                            + "\n\nC 1 уровнем: " + listLvl1.size() +
                            "\n\nC 2 уровнем: " + listLvl2.size() +
                            "\n\nC 3 уровнем: " + listLvl3.size() +
                            "\n\nC 4 уровнем: " + listLvl4.size() +
                            "\n\nC 5 уровнем: " + listLvl5.size() +
                            "\n\nC 6 уровнем: " + listLvl6.size() +
                            "\n\nC 7 уровнем: " + listLvl7.size() +
                            "\n\nC 8 уровнем: " + listLvl8.size() +
                            "\n\nC 9 уровнем: " + listLvl9.size() +
                            "\n\nC 10 уровнем: " + listLvl10.size()).setReplyMarkup(replyKeyboardMarkup);
                }

                if (Objects.equals(update.getMessage().getText(), "Доход") & Objects.equals(update.getMessage().getChatId(), 1016547568L)) {
                    userRepository.updateLastMessage("Доход", Math.toIntExact(update.getMessage().getChatId()));
                    myBot.execute(new SendMessage(update.getMessage().getChatId(), "Пополнено: " + qiwiRepository.sumBalance()).setReplyMarkup(replyKeyboardMarkup));
                    return new SendMessage(update.getMessage().getChatId(), "Выведено: " + withdrawRepository.sumWithdraw()).setReplyMarkup(replyKeyboardMarkup);
                }
            }

        } catch (NullPointerException | MalformedURLException e) {
            System.out.println("Ошибка: NullPointerException");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SendMessage(update.getMessage().getChatId(), "Воспользуйтесь главным меню!").setReplyMarkup(replyKeyboardMarkup);
    }

    private boolean chekBan(@RequestBody Update update) {
        if (update.getMessage().getFrom().getBot()) {

            userRepository.ban(true, Math.toIntExact(update.getMessage().getChatId()));
            return true;
        }
        return userRepository.findById(Math.toIntExact(update.getMessage().getChatId())).isPresent() && userRepository.getBan(Math.toIntExact(update.getMessage().getChatId()));
    }

    private boolean chekSub(@RequestBody Update update) throws TelegramApiException {
        Integer chatId = Math.toIntExact(update.getMessage().getChatId());
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId("-1001617807188");
        getChatMember.setUserId(chatId);
        ChatMember member = myBot.execute(getChatMember);
        return Objects.equals(member.getStatus(), "left");
    }

    private boolean chekPay(@RequestBody Update update) {
        if (qiwiRepository.findByBillId(String.valueOf(update.getMessage().getChatId())) == null) {
            return true;
        }
        return Objects.equals(qiwiRepository.findStatus(String.valueOf(update.getMessage().getChatId())), "PAID");
    }


    @Scheduled(fixedDelay = 60000)
    void deleteChatMessagesAutomatically() {
        System.out.println(ConsoleColors.BLUE);
        ZonedDateTime now = ZonedDateTime.now().minusHours(1);
        List<Qiwi> list = qiwiRepository.Data(now);
        List<QiwiVkl> listVkl = qiwiVklRepository.Data(now);
        qiwiRepository.deleteAll(list);
        qiwiVklRepository.deleteAll(listVkl);
        System.out.println("Очистка неоформленных покупок  ::::  " + now + ConsoleColors.RESET);
    }

    public String getMessage(String msg, Update update) {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThreeRow = new KeyboardRow();
        KeyboardRow keyboardFourRow = new KeyboardRow();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        if (msg.equals("/start") | msg.equals("Главное меню")) {
            if (Objects.equals(update.getMessage().getChatId(), 1016547568L)) {
                keyboardFirstRow.add("\uD83D\uDD25 Партнерство");
                keyboardFirstRow.add("\uD83D\uDCB0 Пассивный доход");
                keyboardSecondRow.add("ℹ️Информация");
                keyboardSecondRow.add("✅ Профиль");
                keyboardThreeRow.add("\uD83D\uDE0E Топ партнерам");
                keyboardThreeRow.add("\uD83D\uDC68\u200D\uD83D\uDCBB Поддержка");
                keyboardFourRow.add("Администрирование");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                keyboard.add(keyboardThreeRow);
                keyboard.add(keyboardFourRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
            } else {
                keyboardFirstRow.add("\uD83D\uDD25 Партнерство");
                keyboardFirstRow.add("\uD83D\uDCB0 Пассивный доход");
                keyboardSecondRow.add("ℹ️Информация");
                keyboardSecondRow.add("✅ Профиль");
                keyboardThreeRow.add("\uD83D\uDE0E Топ партнерам");
                keyboardThreeRow.add("\uD83D\uDC68\u200D\uD83D\uDCBB Поддержка");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                keyboard.add(keyboardThreeRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
            }
            System.out.println(update.getMessage().getChatId());
            return "Выбрать...";
        }
        if (msg.equals("\uD83D\uDD25 Партнерство") | msg.equals("Назад ◀")) {
            keyboardFirstRow.add("\uD83D\uDD25 Повысить уровень");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83D\uDCB0 Пассивный доход") | msg.equals("💲 Покупка UnitCoin") | msg.equals("Назад⏪")) {
            keyboardFirstRow.add("📖 Читать статью");
            keyboardFirstRow.add("💸 Пополнить баланс");
            keyboardSecondRow.add("💰 Перевод средств на вклад");
            keyboardThreeRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            keyboard.add(keyboardThreeRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83D\uDD25 Повысить уровень")) {
            if (Objects.equals(qiwiRepository.findStatus(String.valueOf(Math.toIntExact(update.getMessage().getChatId()))), "WAITING")) {
                keyboardFirstRow.add("Проверить оплату \uD83C\uDD97");
                keyboardSecondRow.add("Назад ◀");
                keyboardSecondRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
            } else if (userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId())) == 10) {
                keyboardFirstRow.add("💲 Покупка UnitCoin");
                keyboardSecondRow.add("Назад ◀");
                keyboardSecondRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
            } else if (userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId())) >= 3) {
                keyboardFirstRow.add("\uD83C\uDD99 Получить " + (userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId())) + 1) + " уровень");
                keyboardSecondRow.add("\uD83D\uDD1D Получить все уровни");
                keyboardThreeRow.add("Назад ◀");
                keyboardThreeRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                keyboard.add(keyboardThreeRow);
            } else {
                keyboardFirstRow.add("\uD83C\uDD99 Получить " + (userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId())) + 1) + " уровень");
                keyboardSecondRow.add("Назад ◀");
                keyboardSecondRow.add("Главное меню");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
            }

            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83C\uDD99 Получить " + (userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId())) + 1) + " уровень") | msg.equals("\uD83D\uDD1D Получить все уровни")) {
            keyboardFirstRow.add("Проверить оплату \uD83C\uDD97");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("✅ Профиль") | msg.equals("Назад \uD83D\uDD19") | msg.equals("Назад↩️")) {
            keyboardFirstRow.add("🤝 Реферальная ссылка");
            keyboardFirstRow.add("💴 Вывод средств");
            keyboardFirstRow.add("\uD83D\uDCB2Баланс");
            keyboardSecondRow.add("🔄 Обновить Qiwi кошелек");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("🤝 Реферальная ссылка")) {
            keyboardFirstRow.add("Сгенерировать QR код");
            keyboardSecondRow.add("Назад↩️");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("💴 Вывод средств")) {
            keyboardFirstRow.add("📱 Регистрация Qiwi");
            keyboardSecondRow.add("Назад \uD83D\uDD19");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("ℹ️Информация")) {
            keyboardFirstRow.add("📃 Правила проекта");
            keyboardSecondRow.add("🎥 Видеообзор бота");
            keyboardSecondRow.add("🎥 Видеообзор монетизации");
            keyboardThreeRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            keyboard.add(keyboardThreeRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("\uD83D\uDE0E Топ партнерам")) {
            keyboardFirstRow.add("\uD83C\uDFC6Топ партнёрам");
            keyboardFirstRow.add("\uD83E\uDD1DДиалог с партнёром");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (msg.equals("💸 Пополнить баланс")) {
            keyboardFirstRow.add("Проверить оплату");
            keyboardSecondRow.add("Назад⏪");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (Objects.equals(update.getMessage().getChatId(), 1016547568L) & msg.equals("Администрирование")) {
            keyboardFirstRow.add("Пользователи");
            keyboardFirstRow.add("Доход");
            keyboardFirstRow.add("Рассылка");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }
        if (Objects.equals(update.getMessage().getChatId(), 1016547568L) & msg.equals("Пользователи")) {
            keyboardFirstRow.add("Макс. баланс");
            keyboardSecondRow.add("Главное меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "...";
        }


        return "Выбрать...";
    }

    public class ConsoleColors {
        // Reset
        public static final String RESET = "\033[0m";  // Text Reset

        // Regular Colors
        public static final String BLACK = "\033[0;30m";   // BLACK
        public static final String RED = "\033[0;31m";     // RED
        public static final String GREEN = "\033[0;32m";   // GREEN
        public static final String YELLOW = "\033[0;33m";  // YELLOW
        public static final String BLUE = "\033[0;34m";    // BLUE
        public static final String PURPLE = "\033[0;35m";  // PURPLE
        public static final String CYAN = "\033[0;36m";    // CYAN
        public static final String WHITE = "\033[0;37m";   // WHITE

        // Bold
        public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
        public static final String RED_BOLD = "\033[1;31m";    // RED
        public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
        public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
        public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
        public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
        public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
        public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

        // Underline
        public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
        public static final String RED_UNDERLINED = "\033[4;31m";    // RED
        public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
        public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
        public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
        public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
        public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
        public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

        // Background
        public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
        public static final String RED_BACKGROUND = "\033[41m";    // RED
        public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
        public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
        public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
        public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
        public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
        public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

        // High Intensity
        public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
        public static final String RED_BRIGHT = "\033[0;91m";    // RED
        public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
        public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
        public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
        public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
        public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
        public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

        // Bold High Intensity
        public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
        public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
        public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
        public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
        public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
        public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
        public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
        public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

        // High Intensity backgrounds
        public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
        public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
        public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
        public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
        public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
        public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
        public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
        public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE
    }
}




