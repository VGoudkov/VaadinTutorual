package my.vaadin.app;

import my.vaadin.EMWrapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;

public class UserService {

    /*
    Проверяет и при необходимости создаёт пользователей в БД при старте системы
     */
    static {
        ensureTestData();
    }


    /**
     * Create demo users in the DB
     */
    public static void ensureTestData() {
        User[] users = new User[3];
        users[0] = new User("regular@company.com", "rgrl", UserRole.Regular);
        users[1] = new User("manager@company.com", "mngr", UserRole.Manager);
        users[2] = new User("auditor@company.com", "dtr", UserRole.Audior);

        Ret[] rets = EMWrapper.doInTransaction( (em, cb, forObject, ret) -> {
            ret.value = em.createQuery("select count(*) from User", Long.class).getSingleResult();
        });

        if (rets[0].isThrowable()) throw new RuntimeException(rets[0].asThrowable());

        if ((Long) rets[0].value == 0) {
            for (User user : users) {
                EMWrapper.doInTransaction(users, ((em, cb, forObject, ret) -> em.persist(user)));
            }
        }
    }

    /**
     * Возвращает роль пользователя по его паре login/пароль
     *
     * @param login    логин
     * @param password пароль
     * @return UserRole роль
     * @throws RuntimeException в случае, если такого пользователя не найдено
     */
    public static UserRole getRoleFor(String login, String password) {
        Ret[] rets = EMWrapper.doInTransaction(new Creds(login, password), UserService::checkUserInDB);
        if (rets[0].isThrowable()) {
            //FIXME: печать пароля в открытом виде. Пригодно только для отладки
            System.out.printf("Can't verify user [%s] with password [%s]", login, password);
            return null;
        } else {
            return ((User) rets[0].value).getRole();
        }
    }


    private static void checkUserInDB(EntityManager em, CriteriaBuilder cb, Creds forObject, Ret ret) {
        //language=HQL
        final String getUserRoleFor = "from User where login = :login AND password=:password";
        final TypedQuery<User> uq = em.createQuery(getUserRoleFor, User.class);
        uq.setParameter("login", forObject.user);
        uq.setParameter("password", forObject.password);
        ret.value = uq.getSingleResult();
    }

    private static class Creds {
        String user, password;

        Creds(String user, String password) {
            this.user = user;
            this.password = password;
        }
    }
}
