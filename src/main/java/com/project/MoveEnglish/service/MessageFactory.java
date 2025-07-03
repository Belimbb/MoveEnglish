package com.project.MoveEnglish.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Service
public class MessageFactory {
    private static Long chatId;

    public static SendMessage createMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode(ParseMode.HTML);
        return message;
    }

    public static SendMessage createMessageWithEmoji(String emoji, String text) {
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

    public static EditMessageText editMessage(Integer messageId, String text) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode(ParseMode.HTML);
        return editMessage;
    }
}