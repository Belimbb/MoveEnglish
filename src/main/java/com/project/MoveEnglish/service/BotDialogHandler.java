package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotDialogHandler {
    private static final String CLASS_NAME = "BotDialogHandler";
    private final ButtonFactory buttonFactory;
    private final MessageFactory messageFactory;

    public SendMessage createWelcomeMessage(Long chatId, String name) {
        String text = String.format("<b>Вітаю, %s!.</b> \nЯ бот MoveEnglish \uD83D\uDE07 \n\nЯк я можу тобі допомогти?", name);
        SendMessage sendMessage = mainMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Welcome message was created", LogEnum.SERVICE);
        return sendMessage;
    }

//    public SendMessage createAboutUsMessage(Long chatId) {
//        String text = "Розробник: <b>JavaCrafters Team</b>\nРепозиторій проєкту: https://github.com/vikadmin88/CurrencyChatBot";
//        SendMessage sendMessage = mainMessageCollector(chatId, text);
//
//        return sendMessage;
//    }

    public SendMessage createMenuMessage(Long chatId) {
        String text = "Головне меню ✅";
        SendMessage sendMessage = menuMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Menu message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createStopMessage(Long chatId){
        String text = """
                        ❗ Ви відписалися від телеграм боту❗ 
                        Якщо ви бажаєте знову ним користуватися, будь ласка введіть або натисніть на команду /start
                      """;
        SendMessage message = messageFactory.createMessage(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Stop message was created", LogEnum.SERVICE);
        return message;
    }

    public SendMessage createMessage(Long chatId, String text){
        return messageFactory.createMessage(chatId, text);
    }


    // Другие методы...

    private Map<String, String> getSettingsOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("bank", "Банки");
        options.put("currency", "Валюти");
        options.put("decimal", "Знаків після коми");
        options.put("notification", "Час сповіщення");
        return options;
    }

    private SendMessage mainMessageCollector (Long chatId, String text){
        SendMessage sendMessage = messageFactory.createMessage(chatId, text);
        sendMessage.setReplyMarkup(buttonFactory.getMainReplyKeyboardMarkup());
        return sendMessage;
    }

    private SendMessage menuMessageCollector (Long chatId, String text){
        SendMessage sendMessage = messageFactory.createMessage(chatId, text);
        sendMessage.setReplyMarkup(buttonFactory.getMenuReplyKeyboardMarkup());
        return sendMessage;
    }
}
