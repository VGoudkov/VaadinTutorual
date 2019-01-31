package my.vaadin;


import my.vaadin.app.IDoInTransaction;
import my.vaadin.app.Ret;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class EMWrapper {
    final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default-persistence-unit");

    public static <T> Ret[] doInTransaction(IDoInTransaction<Object>... blocks) {
        return doInTransaction(null, blocks);
    }


    public static <T> Ret[] doInTransaction(T forObject, IDoInTransaction<T>... blocks) {
        EntityManager em = emf.createEntityManager();
        final int length = blocks.length;
        final Ret[] rets = new Ret[length];
        try {
            final EntityTransaction transaction = em.getTransaction();
            for (int i = 0; i < length; i++) {
                rets[i] = new Ret();
                try {
                    transaction.begin();
                    blocks[i].doInTransaction(em, em.getCriteriaBuilder(), forObject, rets[i]);
                    transaction.commit();
                } catch (Exception ex) {
                    transaction.rollback();
                    rets[i].value = ex;
                    em.close();
                    return rets;
                }
            }
        } finally {
            em.close();
        }
        return rets;
    }
}
