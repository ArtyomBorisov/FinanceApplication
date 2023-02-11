package by.itacademy.mail.constant;

public final class MessageError {
    private MessageError() {
    }

    public static final String ERROR = "error";

    public static final String REPORT_GETTING_EXCEPTION = "Ошибка получения отчёта";

    public static final String INCORRECT_PARAMS
            = "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз";

    public static final String SERVER_ERROR
            = "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору";
}
