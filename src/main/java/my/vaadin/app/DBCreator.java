package my.vaadin.app;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * Только для создания начальных сущностей в БД
 */
public class DBCreator {
    public static void main(String[] args) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default-persistence-unit");
        EntityManager em = emf.createEntityManager();
        final Query q = em.createNativeQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
        System.out.println("Got result: "+q.getSingleResult());
    }
}
