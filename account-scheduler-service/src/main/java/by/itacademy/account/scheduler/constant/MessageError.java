package by.itacademy.account.scheduler.constant;

public final class MessageError {
    private MessageError() {
    }

    public static final String ID_NOT_EXIST = "Передан несуществующий id";

    public static final String MISSING_FIELD = "Не передано обязательное поле (или передано пустое)";

    public static final String PAGE_SIZE = "Размер страницы не может быть меньше 1";

    public static final String PAGE_NUMBER = "Номер страницы не может быть меньше 0";

    public static final String INVALID_DT_UPDATE = "Передан неверный параметр последнего обновления";

    public static final String INVALID_INTERVAL = "Интервал должен быть положительным";

    public static final String INCORRECT_OPERATION_VALUE = "Сумма операции не может быть равна 0";

    public static final String INCORRECT_DATES = "Дата окончания не может быть раньше даты начала";

    public static final String SCHEDULED_OPERATION_CREATING_EXCEPTION = "Ошибка создания запланированной операции";

    public static final String SCHEDULED_OPERATION_DELETING_EXCEPTION = "Ошибка удаления запланированной операции";

    public static final String INCORRECT_PARAMS
            = "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз";

    public static final String SERVER_ERROR
            = "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору";
}
