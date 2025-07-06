package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ButtonFactory {
    private static final String CLASS_NAME = "ButtonFactory";

    // Метод для создания постоянной клавиатуры пользователя
    public ReplyKeyboardMarkup getMainReplyKeyboardMarkup() {
        KeyboardRow row = new KeyboardRow();
        row.add("Меню");
        row.add("Запис на урок");
        row.add("Співпраця");
        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(row);

        log.info("{}: " + CLASS_NAME + ". Main reply keyboard markup was created", LogEnum.SERVICE);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getMenuReplyKeyboardMarkup() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Як користуватись ботом?");
        row1.add("Записатись на урок");
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Як працює ваша школа?");
        row2.add("Акції");
        rows.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Інформація про репетиторів");
        row3.add("Хочу стати репетитором");
        rows.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Назад");
        rows.add(row4);

        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(rows);

        log.info("{}: " + CLASS_NAME + ". Menu reply keyboard markup was created", LogEnum.SERVICE);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getSubtopicReplyKeyboardMarkup() {
        KeyboardRow row = new KeyboardRow();
        row.add("Назад");
        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(row);

        log.info("{}: " + CLASS_NAME + ". Subtopic reply keyboard markup was created", LogEnum.SERVICE);
        return replyKeyboardMarkup;
    }

    // Метод для создания инлайн-клавиатуры на основе предоставленных параметров
    public InlineKeyboardMarkup getInlineKeyboardMarkup(Map<String, String> options, String prefix, List<String> userSelections) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Map.Entry<String, String> option : options.entrySet()) {
            String buttonText = userSelections.contains(option.getKey()) ? "✅ " + option.getValue() : option.getValue();
            buttons.add(createButton(buttonText, prefix + "_" + option.getKey()));
        }

        log.info("{}: " + CLASS_NAME + ". Inline keyboard markup was created", LogEnum.SERVICE);
        return buildInlineKeyboard(buttons);
    }

    // Вспомогательный метод для создания кнопки
    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // Вспомогательный метод для построения клавиатуры из списка кнопок
    private InlineKeyboardMarkup buildInlineKeyboard(List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (InlineKeyboardButton button : buttons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup(KeyboardRow row){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup(List<KeyboardRow> rows){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }
}
