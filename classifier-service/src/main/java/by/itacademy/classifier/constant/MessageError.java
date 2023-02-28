package by.itacademy.classifier.constant;

public final class MessageError {
    private MessageError() {
    }

    public static final String ID_NOT_EXIST = "Передан несуществующий id";

    public static final String MISSING_FIELD = "Не передано обязательное поле (или передано пустое)";

    public static final String NO_UNIQUE_FIELD = "Передано не уникальное поле";

    public static final String PAGE_SIZE = "Размер страницы не может быть меньше 1";

    public static final String PAGE_NUMBER = "Номер страницы не может быть меньше 0";

    public static final String FORBIDDEN = "Forbidden";

    public static final String SECURITY_EXCEPTION = "Ошибка безопасности";

    public static final String INCORRECT_PARAMS
            = "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз";

    public static final String SERVER_ERROR
            = "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору";
}
