package my.vaadin.app;

/**
 * Класс-хранилище результаов исполнения функции
 * <b>Единственная цель - обойти ограничения Java на передачу параметров по ссылке</>
 */
public class Ret {
    /**
     * Значение. Конретный тип определяет блок кода, который его заполняет.
     * Разработчик сам ответственнен за корректое приведение типа
     */
    public Object value;

    /**
     * Была ли завершена операция с ошибкой
     *
     * @return
     */
    public boolean isThrowable() {
        return value instanceof Throwable;
    }

    public boolean isValue() {
        return ! isThrowable();
    }

    public Throwable asThrowable() {
        if (isThrowable()) {
            return (Throwable) value;
        } else{
            return new ClassCastException(String.format("Value [%s] is not Throwable.class",value.toString()));
        }
    }

}
