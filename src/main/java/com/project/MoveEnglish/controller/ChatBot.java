package com.project.MoveEnglish.controller;

import com.project.MoveEnglish.entity.user.UserService;
import com.project.MoveEnglish.exception.LogEnum;
import com.project.MoveEnglish.service.BotDialogHandler;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBot extends TelegramLongPollingBot {

    //private final String appName;
    //private final BotConfig botConfig;
    private final BotDialogHandler dialogHandler;
    private final UserService userService;
    private static final String CLASS_NAME = "ChatBot";

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() { return this.botName; }
    @Override
    public String getBotToken() { return this.botToken; }

    @PostConstruct
    public void init() {
        botRun();
    }

    public void botRun() {
        TelegramBotsApi api = null;
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        //AppRegistry.setChatBot(this);
    }

    public Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);
        boolean isCommandPerformed = false;
        // check or add user
        if (!userService.existById(chatId)){
            userService.create(chatId, update);
        }

        // Messages processing
        if (update.hasMessage()) {

            String msgCommand = update.getMessage().getText();
            log.info("onUpdate: msgCommand: {}  User: {}", msgCommand, chatId);

            // Start
            if (msgCommand.equals("/start")) {
                isCommandPerformed = true;
                doCommandStart(chatId, update);
            }
            // Stop / Disable notify
            if (msgCommand.equals("/stop") || msgCommand.endsWith(new String("Вийти".getBytes(), StandardCharsets.UTF_8))) {
                isCommandPerformed = true;
                doCommandStop(chatId);
            }
            // Settings
            if (msgCommand.equals("/menu") || msgCommand.endsWith(new String("Меню".getBytes(), StandardCharsets.UTF_8))) {
                isCommandPerformed = true;
                doCommandMenu(chatId);
            }


            //
            // if no command
            if (!isCommandPerformed) {
                sendErrorMessage(chatId);
            }
        }

        // Callbacks processing
        if (update.hasCallbackQuery()) {
            String[] btnCommand = update.getCallbackQuery().getData().split("_");
            log.info("btnCommand: {} btnCommand[] {}  User: {}", update.getCallbackQuery().getData(),
                    Arrays.toString(btnCommand), chatId);

            switch (btnCommand[0].toUpperCase()) {
                case "MENU" -> doCommandMenu(chatId);
            }

        }
    }

    public void doCommandStart(Long chatId, Update update) {
        sendMessage(dialogHandler.createWelcomeMessage(chatId, extractName(update)));
        log.info("{}: " + CLASS_NAME + ". Executed welcome message (chatId: {}) was created", LogEnum.CONTROLLER, chatId);
    }
    public void doCommandStop(Long chatId) {
        sendMessage(dialogHandler.createStopMessage(chatId));
        userService.delete(chatId);
    }

    public void doCommandMenu(Long chatId){
        sendMessage(dialogHandler.createMenuMessage(chatId));
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                if (e.getMessage().contains("[403] Forbidden")) {
                    log.error("Can't sent sendMessage() Error message: {}", e.getMessage());
                    log.info("User {} left chat, removing...", message.getChatId());
                    userService.delete(Long.parseLong(message.getChatId()));
                } else {
                    log.error("Can't sendMessage() sendMessage", e);
                }
            }
        }
    }

    public void sendMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Can't sendMessage() EditMessageText", e);
            }
        }
    }

    public void sendErrorMessage(Long chatId) {
        SendMessage ms = dialogHandler.createMessage(chatId, "❗ Command not found!");
        sendMessage(ms);
    }

    private String extractName(Update update){
        return update.getMessage().getFrom().getFirstName();
    }
}
