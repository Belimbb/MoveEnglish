package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class BotDialogHandler {
    private static final String CLASS_NAME = "BotDialogHandler";

    public SendMessage createWelcomeMessage(Long chatId, String name) {
        String text = String.format("<b>Вітаю, %s!.</b> \nЯ бот MoveEnglish \uD83D\uDE07 \n\nЯк я можу тобі допомогти?", name);
        SendMessage sendMessage = messageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Welcome message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createAboutUsMessage(Long chatId) {
        String text = "Розробник: <b>JavaCrafters Team</b>\nРепозиторій проєкту: https://github.com/vikadmin88/CurrencyChatBot";
        SendMessage sendMessage = messageCollector(chatId, text);

        return sendMessage;
    }

    public SendMessage createSettingsMessage(Long chatId) {
        String text = "⚙ <b>Налаштування</b>";
        SendMessage message = MessageFactory.createMessage(chatId, text);
        message.setReplyMarkup(ButtonFactory.getInlineKeyboardMarkup(getSettingsOptions(), "settings", new ArrayList<>()));
        return message;
    }

    public SendMessage createStopMessage(Long chatId){

        return null;
    }

    public SendMessage createMessage(Long chatId, String text){
        return MessageFactory.createMessage(chatId, text);
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

    private SendMessage messageCollector (Long chatId, String text){
        SendMessage sendMessage = MessageFactory.createMessage(chatId, text);
        sendMessage.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return sendMessage;
    }
}
