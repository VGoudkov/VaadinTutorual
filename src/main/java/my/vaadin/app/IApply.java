package my.vaadin.app;

/**
 * Функциональный интерфейс для получения объекта без входных параметров
 */
@FunctionalInterface
public  interface IApply <T> {
    void apply(T t);
}
