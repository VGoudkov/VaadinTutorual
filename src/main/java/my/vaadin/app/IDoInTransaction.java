package my.vaadin.app;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

@FunctionalInterface
public  interface IDoInTransaction <T> {
    /**
     * Интерфейс, определяющий сингнатуру метода, в котором выполняется работа с базой.
     * Выполняется в рамках транзакции {@link my.vaadin.EMWrapper#doInTransaction(Object, IDoInTransaction...)}
     * @param em {@link EntityManager} - внедряемый EntityMananger
     * @param forObject для какого объекта (будет инжектироваться в лямбду)
     * @param ret {@link Ret} - заглушка для возврата значения
     */
     void doInTransaction(EntityManager em, CriteriaBuilder cb, T forObject, Ret ret);
}
