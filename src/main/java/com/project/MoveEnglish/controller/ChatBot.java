package com.project.MoveEnglish.controller;

import com.project.MoveEnglish.config.BotConfig;

import com.project.MoveEnglish.entity.user.UserService;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatBot extends TelegramLongPollingBot {

    //private final String appName;
    //private final BotConfig botConfig;
    private final BotDialogHandler dialogHandler;
    private final UserService userService;


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
        userService.create(chatId, update);

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
                doCommandStop(chatId, update);
            }
            /* Settings
            if (msgCommand.equals("/settings") || msgCommand.endsWith(new String("Налаштування".getBytes(), StandardCharsets.UTF_8))) {
                isCommandPerformed = true;
                doCommandSettings(chatId, update);
            }

             */

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
                case "WELCOME" -> doCommandStart(chatId, update);
            }

        }
    }

    public void doCommandStart(Long chatId, Update update) {
        SendMessage ms = dialogHandler.createWelcomeMessage(chatId);
        try {
            execute(ms);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void doCommandStop(Long chatId, Update update) {
        SendMessage ms = dialogHandler.createMessage(chatId,
                            """
                            ❗Вашу підписку на отримання курсів валют деактивовано!❗ 
                            Якщо ви бажаєте активувати її наново, будь ласка введіть або натисніть на команду /start
                            Також в налаштуваннях ви маєте обрати зручний для вас час отримання повідомлень з курсами валют.
                            """);
        sendMessage(ms);
        userService.delete(chatId);
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
}
