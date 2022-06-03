package by.itacademy.report.service.api;

public final class MessageError {
    private MessageError() {
    }

    public static final String SQL_ERROR = "Ошибка выполнения SQL";

    public static final String INCORRECT_PARAMS = "Переданы некорректные параметры";

    public static final String INVALID_FORMAT = "Передан неверный формат";

    public static final String ID_NOT_EXIST = "Передан несуществующий id";

    public static final String MISSING_FIELD = "Не передано обязательное поле (или передано пустое)";

    public static final String MISSING_OBJECT = "Не передан объект";

    public static final String NO_UNIQUE_FIELD = "Передано не уникальное поле";

    public static final String PAGE_SIZE = "Размер страницы не может быть меньше 1";

    public static final String PAGE_NUMBER = "Номер страницы не может быть меньше 0";

    public static final String INVALID_DT_UPDATE = "Передан неверный параметр последнего обновления";

    public static final String CONVERTER_ERROR = "Ошибка конвертации";
}
