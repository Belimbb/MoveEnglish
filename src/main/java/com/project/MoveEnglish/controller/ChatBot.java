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
        TelegramBotsApi api;
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Long getChatId(Update update) {
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
        // check or add user
        if (!userService.existById(chatId)) {
            userService.create(chatId, update);
        }

        // Messages processing
        if (update.hasMessage()) {
            messageProcessing(update, chatId);
        }

        // Callbacks processing
        if (update.hasCallbackQuery()) {
            callBackProcessing(update, chatId);
        }
    }

    private void messageProcessing(Update update, Long chatId){
        String msgCommand = update.getMessage().getText();
        log.info("onUpdate: msgCommand: {}  User: {}", msgCommand, chatId);

        // Start
        if (checkCommand(msgCommand, "/start", "початок")) {
            sendMessage(dialogHandler.createWelcomeMessage(chatId, extractName(update)));
            log.info("{}: " + CLASS_NAME + ". Executed welcome message (chatId: {}) ", LogEnum.CONTROLLER, chatId);
        }
        // Stop
        else if (checkCommand(msgCommand, "/stop", "Вийти")) {
            sendMessage(dialogHandler.createStopMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed stop message (chatId: {})", LogEnum.CONTROLLER, chatId);
            userService.delete(chatId);
        }
        // Menu
        else if (checkCommand(msgCommand, "/menu", "Меню") ||
                checkCommand(msgCommand, "/back", "Назад")) {
            sendMessage(dialogHandler.createMenuMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed menu message (chatId: {})", LogEnum.CONTROLLER, chatId);
        }

        else if (checkCommand(msgCommand, "/about_bot", "ботом?")) {
            sendMessage(dialogHandler.createAboutBotMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed about bot message (chatId: {})", LogEnum.CONTROLLER, chatId);
        }

        else if (checkCommand(msgCommand, "/about_school", "школа?")) {
            sendMessage(dialogHandler.createAboutSchoolMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed about school message (chatId: {})", LogEnum.CONTROLLER, chatId);
        }

        else if (checkCommand(msgCommand, "/promotions", "Акції")) {
            sendMessage(dialogHandler.createPromotionsMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed promotions message (chatId: {})", LogEnum.CONTROLLER, chatId);
        }

        else if (checkCommand(msgCommand, "/collaboration", "Співпраця")) {
            sendMessage(dialogHandler.createCollaborationMessage(chatId));
            sendMessage(dialogHandler.createCollaborationButtonsMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed collaboration messages (chatId: {})", LogEnum.CONTROLLER, chatId);
        }

        else if (checkCommand(msgCommand, "/become_tutor", "репетитором")) {
            sendMessage(dialogHandler.createTutorMessage(chatId));
            log.info("{}: " + CLASS_NAME + ". Executed become a tutor message (chatId: {})", LogEnum.CONTROLLER, chatId);
        }

        else  {
            sendMessage(dialogHandler.createErrorMessage(chatId));
        }
    }

    private void callBackProcessing(Update update, Long chatId){
        String[] btnCommand = update.getCallbackQuery().getData().split("_");
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        log.info("btnCommand: {} btnCommand[] {}  User: {}", update.getCallbackQuery().getData(),
                Arrays.toString(btnCommand), chatId);

        switch (btnCommand[1]) {
            case "tutor" -> sendMessage(dialogHandler.onTutorMessage(chatId, messageId));
        }
    }

    private void sendMessage(SendMessage message) {
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

    private void sendMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Can't sendMessage() EditMessageText", e);
            }
        }
    }

    private String extractName(Update update) {
        return update.getMessage().getFrom().getFirstName();
    }

    private Boolean checkCommand(String msgCommand, String techName, String audienceName) {
        return msgCommand.equals(techName) || msgCommand.endsWith(new String(audienceName.getBytes(), StandardCharsets.UTF_8));
    }
}