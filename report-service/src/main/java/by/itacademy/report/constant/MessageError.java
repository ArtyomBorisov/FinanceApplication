package by.itacademy.report.constant;

public final class MessageError {
    private MessageError() {
    }

    public static final String ID_NOT_EXIST = "Передан несуществующий id";

    public static final String PAGE_SIZE = "Размер страницы не может быть меньше 1";

    public static final String PAGE_NUMBER = "Номер страницы не может быть меньше 0";

    public static final String REPORT_MAKING_EXCEPTION = "Ошибка создания отчёта";

    public static final String CONVERTER_ERROR = "Ошибка конвертации данных";

    public static final String INCORRECT_PARAMS
            = "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз";

    public static final String SERVER_ERROR
            = "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору";
}
