package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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

    public SendMessage createMenuMessage(Long chatId) {
        String text = "Головне меню ✅";
        SendMessage sendMessage = menuMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Menu message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createAboutBotMessage(Long chatId) {
        String text = """
                Як користуватись ботом?🧩
                
                ✅У кнопці меню ви можете знайти все про нашу школу, як ми співпрацюємо і навіть отримати акцію
                
                ✅У меню ви можете обрати кнопку "співпраця", завдяки цьому у вас є можливість стати викладачем, рефералом або надати нам пропоззиції
                
                ✅Для запису на урок перейдіть у меню по кнопці "Запис на урок". Вкажіть своє ім'я та прізвище.😊
                """;
        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". About Bot message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createAboutSchoolMessage(Long chatId) {
        String text = """
                Як працює ваша школа?😃
                
                ✅Ми проводимо уроки через zoom, discord, skype та інші платформи.
                
                ✅Урок проходить на протязі 50хв.
                
                ✅Ми викладаємо за допомогою книг, які ми підбираємо на пробному занятті. Також використовуємо власні курси, аудіо та відео. Є спеціальні розмовні, аудіо та звичайні заняття.
                На данний момент групові заняття не доступні.
                
                ✅Після закінчення навчання, ви отримуєте диплом про закінчення школи MoveEnglish. З рівнем на якому ви вчились.
                """;
        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". About School message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createPromotionsMessage(Long chatId) {
        String text = """
                Акції та промоакції💵
                
                Акції доступні для учнів школи!
                
                При запрошені нових учнів в школу англійської мови MoveEnglish, ви отримуєте 1 безкоштовне заняття. За умови покупки пакету занять від 12 штук.\s
                
                Промокод на знижку 10% на курс 12 занять. IMOVEENGLISH. Для підтвердження промокоду напишіть його перед оплатою за заняття.
                Промокод дійсний до 01.01.2024
                """;
        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Promotions message was created", LogEnum.SERVICE);
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
//
//    private Map<String, String> getSettingsOptions() {
//        Map<String, String> options = new HashMap<>();
//        options.put("bank", "Банки");
//        options.put("currency", "Валюти");
//        options.put("decimal", "Знаків після коми");
//        options.put("notification", "Час сповіщення");
//        return options;
//    }

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

    private SendMessage subtopicMessageCollector (Long chatId, String text){
        SendMessage sendMessage = messageFactory.createMessage(chatId, text);
        sendMessage.setReplyMarkup(buttonFactory.getSubtopicReplyKeyboardMarkup());
        return sendMessage;
    }
}
