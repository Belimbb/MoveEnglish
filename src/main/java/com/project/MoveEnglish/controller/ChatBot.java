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
        TelegramBotsApi api = null;
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
            // Stop
            if (checkCommand(msgCommand, "/stop", "Вийти")) {
                isCommandPerformed = true;
                doCommandStop(chatId);
            }
            // Menu
            if (checkCommand(msgCommand, "/menu", "Меню") ||
                    checkCommand(msgCommand, "/back", "Назад")){
                isCommandPerformed = true;
                doCommandMenu(chatId);
            }

            if (checkCommand(msgCommand, "/about_bot", "ботом?")){
                isCommandPerformed = true;
                doCommandAboutBot(chatId);
            }

            if (checkCommand(msgCommand, "/about_school", "школа?")){
                isCommandPerformed = true;
                doCommandAboutSchool(chatId);
            }

            if (checkCommand(msgCommand, "/promotions", "Акції")){
                isCommandPerformed = true;
                doCommandPromotions(chatId);
            }


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

    private void doCommandStart(Long chatId, Update update) {
        sendMessage(dialogHandler.createWelcomeMessage(chatId, extractName(update)));
        log.info("{}: " + CLASS_NAME + ". Executed welcome message (chatId: {}) ", LogEnum.CONTROLLER, chatId);
    }
    private void doCommandStop(Long chatId) {
        sendMessage(dialogHandler.createStopMessage(chatId));
        log.info("{}: " + CLASS_NAME + ". Executed stop message (chatId: {})", LogEnum.CONTROLLER, chatId);
        userService.delete(chatId);
    }

    private void doCommandMenu(Long chatId){
        sendMessage(dialogHandler.createMenuMessage(chatId));
        log.info("{}: " + CLASS_NAME + ". Executed menu message (chatId: {})", LogEnum.CONTROLLER, chatId);
    }

    private void doCommandAboutBot(Long chatId){
        sendMessage(dialogHandler.createAboutBotMessage(chatId));
        log.info("{}: " + CLASS_NAME + ". Executed about bot message (chatId: {})", LogEnum.CONTROLLER, chatId);
    }

    private void doCommandAboutSchool(Long chatId){
        sendMessage(dialogHandler.createAboutSchoolMessage(chatId));
        log.info("{}: " + CLASS_NAME + ". Executed about school message (chatId: {})", LogEnum.CONTROLLER, chatId);
    }

    private void doCommandPromotions(Long chatId){
        sendMessage(dialogHandler.createPromotionsMessage(chatId));
        log.info("{}: " + CLASS_NAME + ". Executed promotions message (chatId: {})", LogEnum.CONTROLLER, chatId);
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

    private void sendErrorMessage(Long chatId) {
        SendMessage ms = dialogHandler.createMessage(chatId, """
        ❗ <b>Такої команди не існує</b> ❗
        Наразі доступні такі команди:
                <b>start</b> - Запускає Ваше спілкування з ботом
                <b>stop</b> - Закінчує Ваше спілкування з ботом
                <b>menu</b> - Відкриває меню
                <b>back</b> - Повертає до меню
                <b>about_bot</b> - Розкаже як користуватись ботом
                <b>about_school</b> - Розкаже про школу
                <b>promotions</b> - Дізнаєтеся про актуальні акції
        """);
        sendMessage(ms);
    }

    private String extractName(Update update){
        return update.getMessage().getFrom().getFirstName();
    }

    private Boolean checkCommand (String msgCommand, String techName, String audienceName){
        return msgCommand.equals(techName) || msgCommand.endsWith(new String(audienceName.getBytes(), StandardCharsets.UTF_8));
    }
}
