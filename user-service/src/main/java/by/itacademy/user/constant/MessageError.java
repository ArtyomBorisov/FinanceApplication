package by.itacademy.user.constant;

public final class MessageError {
    private MessageError() {
    }

    public static final String LOGIN_BUSY = "Логин недоступен для регистрации";

    public static final String MISSING_FIELD = "Не передано обязательное поле (или передано пустое)";

    public static final String LOGIN_OR_PASSWORD_INCORRECT = "Пароль или логин неверный (ые)";

    public static final String INCORRECT_PARAMS
            = "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз";

    public static final String SERVER_ERROR
            = "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору";
}
