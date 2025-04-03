import dao.*;
import entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Main {

    private final SessionFactory sessionFactory;

    private final ActorDAO actorDAO;
    private final AddressDAO addressDAO;
    private final CategoryDAO categoryDAO;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final CustomerDAO customerDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;
    private final InventoryDAO inventoryDAO;
    private final LanguageDAO languageDAO;
    private final PaymentDAO paymentDAO;
    private final RentalDAO rentalDAO;
    private final StaffDAO staffDAO;
    private final StoreDAO storeDAO;


    public Main() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/movie");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Film.class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .addProperties(properties)
                .buildSessionFactory();

        this.actorDAO = new ActorDAO(sessionFactory);
        this.addressDAO = new AddressDAO(sessionFactory);
        this.categoryDAO = new CategoryDAO(sessionFactory);
        this.cityDAO = new CityDAO(sessionFactory);
        this.countryDAO = new CountryDAO(sessionFactory);
        this.customerDAO = new CustomerDAO(sessionFactory);
        this.filmDAO = new FilmDAO(sessionFactory);
        this.filmTextDAO = new FilmTextDAO(sessionFactory);
        this.inventoryDAO = new InventoryDAO(sessionFactory);
        this.languageDAO = new LanguageDAO(sessionFactory);
        this.paymentDAO = new PaymentDAO(sessionFactory);
        this.rentalDAO = new RentalDAO(sessionFactory);
        this.staffDAO = new StaffDAO(sessionFactory);
        this.storeDAO = new StoreDAO(sessionFactory);

    }

    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();

        main.returnInventoryToStore();
        main.customerRentInventory(customer);
        main.createFilm();
    }

    private void createFilm() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();


            Language language = languageDAO.getById(1);
            Set<Category> categories = new HashSet<>(categoryDAO.getItems(0, 5));
            Set<Actor> actors = new HashSet<>(actorDAO.getItems(0, 5));

            Film film = new Film();
            film.setActors(actors);
            film.setRating(Rating.NC17);
            film.setSpecialFeaturesFromEnumSet(Set.of(Feature.COMMENTARIES, Feature.TRAILERS));
            film.setLength((short) 22);
            film.setReplacementCost(BigDecimal.valueOf(123));
            film.setRentalRate(BigDecimal.valueOf(20));
            film.setLanguage(language);
            film.setOriginalLanguage(language);
            film.setDescription("This is a film");
            film.setTitle("This is a title");
            film.setRentalDuration((byte) 54);
            film.setCategories(categories);
            film.setReleaseYear(1999);

            filmDAO.save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setTitle("This is a title");
            filmText.setDescription("This is a film");

            filmTextDAO.save(filmText);

            transaction.commit();

        }
    }

    private void customerRentInventory(Customer customer) {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            Film film = filmDAO.getFirstAvailableFilmForRent();

            Store store = storeDAO.getItems(0, 1).getFirst();
            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Staff staff = store.getStaff();

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setStaff(staff);
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setCustomer(customer);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setAmount(BigDecimal.ONE);
            payment.setStaff(staff);
            paymentDAO.save(payment);

            transaction.commit();
        }

    }

    private void returnInventoryToStore() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            Rental rental = rentalDAO.getAnyUnreturnedRental();
            rental.setReturnDate(LocalDateTime.now());
            rentalDAO.save(rental);
            transaction.commit();

        }
    }

    private Customer createCustomer() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();

            Store store = storeDAO.getItems(0, 1).getFirst();
            City city = cityDAO.getByName("Kursk");
            Address address = new Address();
            address.setPhone("9312939311");
            address.setAddress("Some street");
            address.setCity(city);
            address.setDistrict("Some district");
            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setActive(true);
            customer.setFirstName("Ivan");
            customer.setLastName("Ivanov");
            customer.setEmail("customer@gmail.com");
            customer.setAddress(address);
            customer.setStore(store);

            customerDAO.save(customer);

            transaction.commit();
            return customer;
        }
    }
}
