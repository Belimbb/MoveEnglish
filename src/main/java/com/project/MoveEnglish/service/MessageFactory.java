package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public SendMediaGroup createPhotoAlbum(Long chatId, List<String> imageNames, String caption) {
        List<InputMedia> mediaList = new ArrayList<>();
        String filePath = "src/main/resources/images/";

        for (int i = 0; i < imageNames.size(); i++) {
            InputMediaPhoto mediaPhoto = new InputMediaPhoto();
            File image = new File(filePath + imageNames.get(i));
            mediaPhoto.setMedia(image, image.getName());

            if (i == 0) {
                mediaPhoto.setCaption(caption);
                mediaPhoto.setParseMode("HTML");
            }

            mediaList.add(mediaPhoto);
        }

        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(chatId.toString());
        mediaGroup.setMedias(mediaList);

        return mediaGroup;
    }

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