package my.vaadin.app;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static my.vaadin.EMWrapper.doInTransaction;

/**
 * An in memory dummy "database" for the example purposes. In a typical Java app
 * this class would be replaced by e.g. EJB or a Spring based service class.
 * <p>
 * In demos/tutorials/examples, get a reference to this service class with
 * {@link CustomerService#getInstance()}.
 */
public class CustomerService {

    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default-persistence-unit");
    private static CustomerService instance;
    //private final HashMap<Long, Customer> contacts = new HashMap<>();
    private long nextId = 0;

    private CustomerService() {
    }

    /**
     * @return a reference to an example facade for Customer objects.
     */
    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
            instance.ensureTestData();
        }
        return instance;
    }

    /**
     * Вставка объектов в БД в контексте <b>одной</b> транзакции
     *
     * @param em      контекст {@link EntityManager}
     * @param objects список объектов для вставки
     */
    private static void db_persist(EntityManager em, Object... objects) {
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        for (Object o : objects) {
            em.persist(o);
        }
        transaction.commit();
    }

    /**
     * @return all available Customer objects.
     */
    public synchronized List<Customer> findAll() {
        return findAll(null);
    }

    /**
     * Finds all Customer's that match given filter.
     *
     * @param stringFilter filter that returned objects should match or null/empty string
     *                     if all objects should be returned.
     * @return list a Customer objects
     */
    public synchronized List<Customer> findAll(String stringFilter) {
        //TODO: написать код репозитория всё таки
        Ret[] ret = doInTransaction(stringFilter, this::findByStringInDB);
        if (ret[0].isValue()) return (List<Customer>) ret[0].value;
        else {
            throw new RuntimeException("Can't get customers", ret[0].asThrowable());
        }
    }

    private void findByStringInDB(EntityManager em, CriteriaBuilder cb, String filter, Ret ret) {

        /*
        Внутреннее соглашение - если фильтр пустой, то он null
         */
        if( ! (filter == null)){
            if( filter.trim().isEmpty()){
                filter=null;
            }
        }

        //language=HQL
        String hql;
        if (filter == null) {
            hql = "from Customer c";
        } else {
            filter = "%"+filter.toLowerCase().trim()+"%";
            hql = "from Customer c where" +
                    " lower(c.firstName) like :filter" +
                    " OR lower(c.lastName) like :filter" +
                    " OR lower(c.email) like :filter" +
                    //" OR lower(c.birthDate) like :filter" +
                    " order by c.id";
        }
        final TypedQuery<Customer> q = em.createQuery(hql, Customer.class);
        if(filter!=null){
            q.setParameter("filter",filter);
        }
        ret.value = q.getResultList();
    }

    /**
     * @return the amount of all customers in the system
     */
    public synchronized long count() {
        final Ret[] rets = doInTransaction(this::countInDB);
        return (Long) rets[0].value;
    }

    private void countInDB(EntityManager em, CriteriaBuilder cb, Object o, Ret ret) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(Customer.class)));
        ret.value = em.createQuery(cq).getSingleResult();
    }

    /**
     * Deletes a customer from a system
     *
     * @param value the Customer to be deleted
     */
    public synchronized void delete(Customer value) {
        doInTransaction(value, this::deleteInDB);
    }

    private void deleteInDB(EntityManager em, CriteriaBuilder cb, Customer forObject, Ret ret) {
        CriteriaDelete<Customer> dq = cb.createCriteriaDelete(Customer.class);
        Root<Customer> customer = dq.from(Customer.class);
        dq.where(cb.equal(customer.get("id"), forObject.getId()));
    }

    /**
     * Persists or updates customer in the system. Also assigns an identifier
     * for new Customer instances.
     *
     * @param entry
     */
    public synchronized void save(Customer entry) {
        if (entry == null) {
            LOGGER.log(Level.SEVERE,
                    "Customer is null. Are you sure you have connected your form to the application as described in tutorial chapter 7?");
            return;
        }
        if (entry.getId() == null) {
            entry.setId(nextId++);
        }
        try {
            entry = (Customer) entry.clone();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Ret[] ret = doInTransaction(entry, (e, cb, o, r) -> e.persist(o));
        if (ret[0].isThrowable()) throw new RuntimeException(ret[0].asThrowable());

    }

    /**
     * Sample data generation
     */
    public void ensureTestData() {
        if (findAll().isEmpty()) {
            generateVaadinData();
            //generate500KRecords();
        }
    }

    private void generateVaadinData() {
        final String[] names = new String[]{"Gabrielle Patel", "Brian Robinson", "Eduardo Haugen",
                "Koen Johansen", "Alejandro Macdonald", "Angel Karlsson", "Yahir Gustavsson", "Haiden Svensson",
                "Emily Stewart", "Corinne Davis", "Ryann Davis", "Yurem Jackson", "Kelly Gustavsson",
                "Eileen Walker", "Katelyn Martin", "Israel Carlsson", "Quinn Hansson", "Makena Smith",
                "Danielle Watson", "Leland Harris", "Gunner Karlsen", "Jamar Olsson", "Lara Martin",
                "Ann Andersson", "Remington Andersson", "Rene Carlsson", "Elvis Olsen", "Solomon Olsen",
                "Jaydan Jackson", "Bernard Nilsen"};
        Random r = new Random(42);
        for (String name : names) {
            String[] split = name.split(" ");
            Customer c = new Customer();
            c.setFirstName(split[0]);
            c.setLastName(split[1]);
            c.setEmail(split[0].toLowerCase() + "@" + split[1].toLowerCase() + ".com");
            c.setStatus(CustomerStatus.values()[r.nextInt(CustomerStatus.values().length)]);
            int daysOld = 0 - r.nextInt(365 * 15 + 365 * 60);
            c.setBirthDate(LocalDate.now().plusDays(daysOld));
            save(c);
        }
    }

//    private void generate500KRecords() {
//        Random r = new Random(0);
//        for (int i = 0; i < 500_000; i++) {
//            Customer c = new Customer();
//            c.setFirstName("Name" + i);
//            c.setLastName("LastName" + i);
//            c.setEmail("customer" + i + "@customercompany.com");
//            c.setStatus(CustomerStatus.values()[r.nextInt(CustomerStatus.values().length)]);
//            int daysOld = 0 - r.nextInt(365 * 15 + 365 * 60);
//            c.setBirthDate(LocalDate.now().plusDays(daysOld));
//            save(c);
//        }
//    }

}