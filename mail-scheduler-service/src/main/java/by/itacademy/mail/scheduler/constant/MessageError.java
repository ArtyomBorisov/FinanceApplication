package by.itacademy.mail.scheduler.constant;

public final class MessageError {
    private MessageError() {
    }

    public static final String MONTHLY_REPORT_EXIST = "Вы уже подписаны на ежемесячный отчёт";

    public static final String REPORT_MAKING_EXCEPTION = "Ошибка запроса на создание отчёта";

    public static final String REPORT_SENDING_EXCEPTION = "Ошибка запроса на отправку отчёта";

    public static final String REPORT_NOT_EXIST = "Отсутствует сформированный отчёт";

    public static final String INCORRECT_PARAMS
            = "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз";

    public static final String SERVER_ERROR
            = "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору";
}
