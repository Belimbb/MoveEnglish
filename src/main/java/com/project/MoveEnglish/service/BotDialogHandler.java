package com.project.MoveEnglish.service;

import com.project.MoveEnglish.exception.LogEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public SendMessage createLessonMessage(Long chatId) {
        String text = """
                Для запису на урок напишіть своє ім'я та прізвище або номер телефону (якщо закритий аккаунт).
                Після підтвердження, адміністратор зв'яжеться з вами і уточнить інфомацію👍
                """;
        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Lesson message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createThanksMessage(Long chatId) {
        String text = """
                💡Дякую за ваше повідомлення.
                Наш адміністратор зв'яжеться з вами у найближчий час.🛎️
                """;
        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Thanks message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createCollaborationMessage(Long chatId) {
        String text = "Вітаю вас у розділі співпраця📈";
        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Collaboration message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public SendMessage createCollaborationButtonsMessage(Long chatId) {
        String text = "Оберіть варіант у якому ви зацікавлені";
        SendMessage sendMessage = messageFactory.createMessage(chatId, text);
        sendMessage.setReplyMarkup(buttonFactory.getInlineKeyboardMarkup(getCollaborationOptions(), "collaboration", new ArrayList<>()));

        log.info("{}: " + CLASS_NAME + ". Collaboration button message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    public EditMessageText onTutorMessage(Long chatId, Integer messageId) {
        String text = """
                Вітаю!👋
                
                Щоб стати викладачем👨‍🏫👩‍🏫, напишіть нижче ваше коротке інтерв'ю. Дайте відповідь на питання.
                1. Розкажіть про себе.
                2. Який рівень англійської мови ви маєте?
                3. По якому графіку ви готові працювати?
                4. Чи маєте ви досвід у викладанні, якщо так, який?
                 Якщо ви нам підходите, ми зв'яжемось з вами у найближчий час!
                """;
        return messageFactory.editMessage(chatId, messageId, text);
    }

    public SendMessage createTutorMessage(Long chatId){
        String text = """
                Вітаю!👋
                
                Щоб стати викладачем👨‍🏫👩‍🏫, напишіть нижче ваше коротке інтерв'ю. Дайте відповідь на питання.
                1. Розкажіть про себе.
                2. Який рівень англійської мови ви маєте?
                3. По якому графіку ви готові працювати?
                4. Чи маєте ви досвід у викладанні, якщо так, який?
                 Якщо ви нам підходите, ми зв'яжемось з вами у найближчий час!
                """;

        SendMessage sendMessage = subtopicMessageCollector(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Become a tutor message was created", LogEnum.SERVICE);
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

    public SendMessage createErrorMessage(Long chatId){
        String text = """
        ❗ <b>Такої команди не існує</b> ❗
        Наразі доступні такі команди:
                <b>start</b> - Запускає Ваше спілкування з ботом
                <b>stop</b> - Закінчує Ваше спілкування з ботом
                <b>menu</b> - Відкриває меню
                <b>back</b> - Повертає до меню
                <b>about_bot</b> - Розкаже як користуватись ботом
                <b>about_school</b> - Розкаже про школу
                <b>promotions</b> - Дізнаєтеся про актуальні акції
        """;
        SendMessage message = messageFactory.createMessage(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Error message was created", LogEnum.SERVICE);
        return message;
    }

    public SendMessage createMessage(Long chatId, String text) {
        SendMessage sendMessage = messageFactory.createMessage(chatId, text);

        log.info("{}: " + CLASS_NAME + ". Custom message was created", LogEnum.SERVICE);
        return sendMessage;
    }

    private Map<String, String> getCollaborationOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("tutor", "Хочу бути викладачем");
        options.put("offers", "Пропозиції");
        options.put("referral", "Робота рефералом");
        options.put("ad", "Реклама");
        return options;
    }

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