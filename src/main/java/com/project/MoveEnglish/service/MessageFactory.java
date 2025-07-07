package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Slf4j
@Service
public class MessageFactory {
    private static final String CLASS_NAME = "MessageFactory";

    public SendMessage createMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode(ParseMode.HTML);

        log.info("{}: " + CLASS_NAME + ". Message (to chat: {}) was created", LogEnum.SERVICE, chatId);
        return message;
    }

    public SendMessage createMessageWithEmoji(Long chatId, String emoji, String text) {
        String fullText = emoji.isEmpty() ? text : emoji + " " + text;
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(fullText);
        message.setParseMode(ParseMode.HTML);
        return message;
    }

    /*
    public static SendPhoto createPhotoMessage(String imageUrl, String caption) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(imageUrl));
        photo.setCaption(caption);
        photo.setParseMode(ParseMode.HTML);
        return photo;
    }

     */

    public EditMessageText editMessage(Long chatId, Integer messageId, String text) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode(ParseMode.HTML);

        log.info("{}: " + CLASS_NAME + "Message (mes id: {}, to chat: {}) was edited", LogEnum.SERVICE, messageId, chatId);
        return editMessage;
    }
}