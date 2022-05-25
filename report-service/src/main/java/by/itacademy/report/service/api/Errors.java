package by.itacademy.report.service.api;

public enum Errors {
    INCORRECT_DATA("Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз"),
    NOT_ENOUGH_DATA("Запрос не содержит всех необходимых данных. Дополните запрос и отправьте его ещё раз"),
    SQL_ERROR("Ошибка выполнения SQL");


    private String message;

    Errors(String message) {
        this.message = message;
    }
}
