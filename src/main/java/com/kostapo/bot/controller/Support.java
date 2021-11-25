package com.kostapo.bot.controller;

import com.kostapo.bot.Bot;
import com.kostapo.bot.model.Qiwi;
import com.kostapo.bot.model.QiwiVkl;
import com.kostapo.bot.repository.QiwiRepository;
import com.kostapo.bot.repository.QiwiVklRepository;
import com.kostapo.bot.repository.UserRepository;
import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Support {
    private final Bot myBot;
    private final QiwiVklRepository qiwiVklRepository;
    private final QiwiRepository qiwiRepository;
    private final UserRepository userRepository;
    QiwiVkl qiwiVkl = new QiwiVkl();
    Qiwi qiwi = new Qiwi();
    String secretKey = "eyJ2ZXJzaW9uIjoiUDJQIiwiZGF0YSI6eyJwYXlpbl9tZXJjaGFudF9zaXRlX3VpZCI6ImtqNTA2dS0wMCIsInVzZXJfaWQiOiI3OTY0NDc3OTc3OSIsInNlY3JldCI6IjZiZDc4YzJhNDYzYTE0ODI2YjdkZDhiNjNkMzY2ZGM4YjE3MTJhYjY1MTMyZDRkZDBhYjFiZmFhNGI3NDc2MjYifX0=";
    BillPaymentClient client = BillPaymentClientFactory.createDefault(secretKey);

    public Support(Bot myBot, QiwiVklRepository qiwiVklRepository, QiwiRepository qiwiRepository, UserRepository userRepository) {
        this.myBot = myBot;
        this.qiwiVklRepository = qiwiVklRepository;
        this.qiwiRepository = qiwiRepository;
        this.userRepository = userRepository;
    }

    public BotApiMethod<?> sendMessage(Integer chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        return sendMessage.setChatId(String.valueOf(chatId)).setText(text).setParseMode(ParseMode.MARKDOWN);
    }

    public Message sendMessageBuyLvl(Update update, ReplyKeyboardMarkup replyKeyboardMarkup) throws TelegramApiException, URISyntaxException, IOException {
        int amount;
        int lvl = userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId()));

        switch (lvl) {
            case 0:
                amount = 149;
                break;
            case 1:
                amount = 299;
                break;
            case 2:
                amount = 449;
                break;
            case 3:
                amount = 599;
                break;
            case 4:
                amount = 749;
                break;
            case 5:
                amount = 899;
                break;
            case 6:
                amount = 1049;
                break;
            case 7:
                amount = 1199;
                break;
            case 8:
                amount = 1349;
                break;
            case 9:
                amount = 1499;
                break;
            default:
                throw new IllegalStateException("Несуществующее значение: " + lvl);
        }

        return myBot.execute(new SendMessage(update.getMessage().getChatId(), "Стоимость " + (lvl + 1) + " уровня составляет " + amount + " рублей. \n" +
                "Для оплаты перейдите по ссылке и произведите оплату.\n" +
                "После чего ожидайте зачисления, либо проверьте с помощью команды.\n\n" +
                "Счет на оплату действителен 1 час.\n\n" +
                CreateBill(update, amount)).setReplyMarkup(replyKeyboardMarkup));
    }

    public Message sendMessageBuyAllLvl(Update update, ReplyKeyboardMarkup replyKeyboardMarkup) throws TelegramApiException, URISyntaxException, IOException {
        int amount;
        int otherAmount;
        int lvl = userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId()));
        int otherLvl;

        switch (lvl) {
            case 3:
                otherAmount = 7343;
                amount = 6969;
                otherLvl = 7;
                break;
            case 4:
                otherAmount = 6744;
                amount = 6399;
                otherLvl = 6;
                break;
            case 5:
                otherAmount = 5995;
                amount = 5699;
                otherLvl = 5;
                break;
            case 6:
                otherAmount = 5096;
                amount = 4799;
                otherLvl = 4;
                break;
            case 7:
                otherAmount = 4047;
                amount = 3839;
                otherLvl = 3;
                break;
            case 8:
                otherAmount = 2848;
                amount = 2699;
                otherLvl = 2;
                break;
            default:
                throw new IllegalStateException("Несуществующее значение: " + lvl);
        }

        return myBot.execute(new SendMessage(update.getMessage().getChatId(), "Стоимость оставшихся " + otherLvl + " уровней составляет:\n" +
                "❌ " + otherAmount + " - 5% \n" +
                "✅Итог: " + amount + " рублей\n" +
                "\n" +
                "\uD83D\uDC4C Экономия времени средств даёт конкурентное преимущество.\n" +
                "\n" +
                "\uD83D\uDE4C С 10 уровнем вы получаете максимально выгодные условия.\n" +
                "Так при оплате вашим партнёром пакета с 3-10 уровень (6969р.)\n" +
                "Вы получаете 33% от стоимости вашего уровня.\n" +
                "\n" +
                " - Если у вас 4 уровень вы получите: 496 руб.\n" +
                " - Если ваш уровень максимальный, 10 уровень вы получите: 2740 руб.\n" +
                "Стань первым во всём, получай максимум выгоды.\n" +
                "\n" +
                "Счёт на оплату действует 1 час.\n" +
                CreateBill(update, amount)).setReplyMarkup(replyKeyboardMarkup));
    }


    public Message sendMessageBuy(Update update, ReplyKeyboardMarkup replyKeyboardMarkup, String amount, String allAmount) throws TelegramApiException, URISyntaxException, IOException {

        String billId = qiwiRepository.findByBillId(String.valueOf(update.getMessage().getChatId()));
        BillResponse response = client.getBillInfo(billId);
        String ref_2 = userRepository.getReferral_2(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
        String ref = userRepository.getReferral(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));


        BigDecimal sumLvL = new BigDecimal(amount);
        BigDecimal allSumLvL = new BigDecimal(allAmount);

        if (Objects.equals(response.getStatus().getValue().getValue(), "PAID") & Objects.equals(response.getAmount().getValue(), allSumLvL)) {
            int amountRef;
            int amountRef_2;
            int lvlRef = userRepository.getlevel(Math.toIntExact(Long.parseLong(ref)));
            int lvlRef_2 = userRepository.getlevel(Math.toIntExact(Long.parseLong(ref_2)));
            userRepository.updateLvL(10, Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
            userRepository.updatePayment(allSumLvL , Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
            int lvl = userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId()));


            switch (lvlRef) {
                case 0:
                    amountRef = 0;
                    break;
                case 1:
                    amountRef = 49;
                    break;
                case 2:
                    amountRef = 148;
                    break;
                case 3:
                    amountRef = 297;
                    break;
                case 4:
                    amountRef = 496;
                    break;
                case 5:
                    amountRef = 745;
                    break;
                case 6:
                    amountRef = 1044;
                    break;
                case 7:
                    amountRef = 1393;
                    break;
                case 8:
                    amountRef = 1792;
                    break;
                case 9:
                    amountRef = 2241;
                    break;
                case 10:
                    amountRef = 2740;
                    break;
                default:
                    throw new IllegalStateException("Несуществующее значение: " + lvlRef);
            }

            switch (lvlRef_2) {
                case 0:
                    amountRef_2 = 0;
                    break;
                case 1:
                    amountRef_2 = 10;
                    break;
                case 2:
                    amountRef_2 = 31;
                    break;
                case 3:
                    amountRef_2 = 62;
                    break;
                case 4:
                    amountRef_2 = 104;
                    break;
                case 5:
                    amountRef_2 = 156;
                    break;
                case 6:
                    amountRef_2 = 219;
                    break;
                case 7:
                    amountRef_2 = 292;
                    break;
                case 8:
                    amountRef_2 = 376;
                    break;
                case 9:
                    amountRef_2 = 470;
                    break;
                case 10:
                    amountRef_2 = 575;
                    break;
                default:
                    throw new IllegalStateException("Несуществующее значение: " + lvlRef_2);
            }


            myBot.execute(new SendMessage(ref, "Ваш реферал приобрел полный пакет уровней \uD83D\uDD25 \n" +
                    "\n" +
                    "Ваш уровень: " + lvlRef +
                    "\n" +
                    "\n" +
                    "*Зачислено:* " + amountRef + " рублей").setParseMode(ParseMode.MARKDOWN));
            myBot.execute(new SendMessage(ref_2, "Реферал Вашего реферала приобрел полный пакет уровней \uD83D\uDD25 \n" +
                    "\n" +
                    "Ваш уровень: " + lvlRef_2 +
                    "\n" +
                    "\n" +
                    "*Зачислено:* " + amountRef_2 + " рублей").setParseMode(ParseMode.MARKDOWN));
            qiwiRepository.updateStatus(billId);
            userRepository.updatePlusBalance(Double.parseDouble(String.valueOf(amountRef)), Integer.parseInt(ref));
            userRepository.updatePlusBalance(Double.parseDouble(String.valueOf(amountRef_2)), Integer.parseInt(ref_2));

            return myBot.execute(new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                    "Ваш уровень повышен до 10").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
        }


        if (Objects.equals(response.getStatus().getValue().getValue(), "PAID") & Objects.equals(response.getAmount().getValue(), sumLvL)) {
            int amountRef;
            int amountRef_2;
            int lvlRef = userRepository.getlevel(Math.toIntExact(Long.parseLong(ref)));
            int lvlRef_2 = userRepository.getlevel(Math.toIntExact(Long.parseLong(ref_2)));
            userRepository.updateLevel(Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
            userRepository.updatePayment(sumLvL , Integer.parseInt(String.valueOf(update.getMessage().getChatId())));
            int lvl = userRepository.getlevel(Math.toIntExact(update.getMessage().getChatId()));


            switch (lvlRef) {
                case 0:
                    amountRef = 0;
                    break;
                case 1:
                    amountRef = 49;
                    break;
                case 2:
                    amountRef = 99;
                    break;
                case 3:
                    amountRef = 149;
                    break;
                case 4:
                    amountRef = 199;
                    break;
                case 5:
                    amountRef = 249;
                    break;
                case 6:
                    amountRef = 299;
                    break;
                case 7:
                    amountRef = 349;
                    break;
                case 8:
                    amountRef = 399;
                    break;
                case 9:
                    amountRef = 449;
                    break;
                case 10:
                    amountRef = 499;
                    break;
                default:
                    throw new IllegalStateException("Несуществующее значение: " + lvlRef);
            }

            switch (lvlRef_2) {
                case 0:
                    amountRef_2 = 0;
                    break;
                case 1:
                    amountRef_2 = 10;
                    break;
                case 2:
                    amountRef_2 = 21;
                    break;
                case 3:
                    amountRef_2 = 31;
                    break;
                case 4:
                    amountRef_2 = 42;
                    break;
                case 5:
                    amountRef_2 = 52;
                    break;
                case 6:
                    amountRef_2 = 63;
                    break;
                case 7:
                    amountRef_2 = 73;
                    break;
                case 8:
                    amountRef_2 = 84;
                    break;
                case 9:
                    amountRef_2 = 94;
                    break;
                case 10:
                    amountRef_2 = 105;
                    break;
                default:
                    throw new IllegalStateException("Несуществующее значение: " + lvlRef_2);
            }


            myBot.execute(new SendMessage(ref, "Ваш реферал повысил себе уровень \uD83D\uDD25 \n" +
                    "\n" +
                    "Ваш уровень: " + lvlRef +
                    "\n" +
                    "\n" +
                    "*Зачислено:* " + amountRef + " рублей").setParseMode(ParseMode.MARKDOWN));
            myBot.execute(new SendMessage(ref_2, "Реферал Вашего реферала повысил себе уровень \uD83D\uDD25 \n" +
                    "\n" +
                    "Ваш уровень: " + lvlRef_2 +
                    "\n" +
                    "\n" +
                    "*Зачислено:* " + amountRef_2 + " рублей").setParseMode(ParseMode.MARKDOWN));
            qiwiRepository.updateStatus(billId);
            userRepository.updatePlusBalance(Double.parseDouble(String.valueOf(amountRef)), Integer.parseInt(ref));
            userRepository.updatePlusBalance(Double.parseDouble(String.valueOf(amountRef_2)), Integer.parseInt(ref_2));
            if(lvl == 1) {
                userRepository.updatePlusBalance(49.0, Integer.parseInt(String.valueOf(update.getMessage().getChatId())));

                String password = new Random().ints(10, 33, 122).collect(StringBuilder::new,
                                StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                userRepository.updatePass(password,Math.toIntExact(update.getMessage().getChatId()));
                myBot.execute(new SendMessage(update.getMessage().getChatId(), "❗️СОХРАНИ, НЕ ПОТЕРЯЙ❗️" +
                        "\nВам предоставлен доступ для входа на наш сайт" +
                        "\n\nЛогин: " + update.getMessage().getChatId() +
                        "\n\nПароль: " + password +
                        "\n\nНачислены бонусные 49 рублей \uD83D\uDD25" ).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
            }

            return myBot.execute(new SendMessage(update.getMessage().getChatId(), "Оплата прошла *успешно* ✅\n" +
                    "Ваш уровень повышен до " + lvl ).setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));
        }

        return myBot.execute(new SendMessage(update.getMessage().getChatId(), "Оплата *не пройдена* ❌").setParseMode(ParseMode.MARKDOWN).setReplyMarkup(replyKeyboardMarkup));

    }




    public String CreateBillVkl(Update update) throws URISyntaxException {

        CreateBillInfo billInfo = new CreateBillInfo(
                UUID.randomUUID().toString(),
                new MoneyAmount(
                        BigDecimal.valueOf(Long.parseLong(update.getMessage().getText())),
                        Currency.getInstance("RUB")
                ),
                String.valueOf(update.getMessage().getChatId()),
                ZonedDateTime.now().plusDays(0).plusHours(1),
                new Customer(
                        "pasandreg@gmail.com",
                        UUID.randomUUID().toString(),
                        "79123456789"
                ),
                "t.me/unitcash_bot"
        );
        BillResponse response = client.createBill(billInfo);
        qiwiVkl.setIdPay(response.getBillId());
        qiwiVkl.setAmount(Double.parseDouble(String.valueOf(response.getAmount().getValue())));
        qiwiVkl.setStatus(String.valueOf(response.getStatus().getValue().getValue()));
        qiwiVkl.setData(response.getCreationDateTime());
        qiwiVkl.setUser_id(response.getComment());
        qiwiVklRepository.save(qiwiVkl);

        return response.getPayUrl();
    }

    public String CreateBill(Update update, Integer amount) throws URISyntaxException {

        CreateBillInfo billInfo = new CreateBillInfo(
                UUID.randomUUID().toString(),
                new MoneyAmount(
                        BigDecimal.valueOf(amount),
                        Currency.getInstance("RUB")
                ),
                String.valueOf(update.getMessage().getChatId()),
                ZonedDateTime.now().plusDays(0).plusHours(1),
                new Customer(
                        "pasandreg@gmail.com",
                        UUID.randomUUID().toString(),
                        "79123456789"
                ),
                "t.me/unitcash_bot"
        );

        BillResponse response = client.createBill(billInfo);
        qiwi.setIdPay(response.getBillId());
        qiwi.setAmount(Double.parseDouble(String.valueOf(response.getAmount().getValue())));
        qiwi.setStatus(String.valueOf(response.getStatus().getValue().getValue()));
        qiwi.setData(response.getCreationDateTime());
        qiwi.setUser_id(response.getComment());
        qiwiRepository.save(qiwi);

        return response.getPayUrl();
    }

}
