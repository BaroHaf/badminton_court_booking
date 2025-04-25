package Util;

import Dao.CourtDao;
import Dao.ProductDao;
import Dao.UserDao;
import Dao.VenueDao;
import Model.Constant.Rank;
import Model.Constant.Role;
import Model.Court;
import Model.Product;
import Model.User;
import Model.Venue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class StartListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("badminton_court");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        if (users.isEmpty()) {
            String password = "$2a$10$5C.8P6dGpCAYjkt3noBrAORPdyrX.Uy07Z46lMmsClcC1oCy0pBwi"; // 123456
            User admin = new User("admin@gmail.com", "admin", password, "uploads/default-avatar.png", "0123456787", true, false, Role.ADMIN, null);
            User customer = new User("customer@gmail.com", "customer", password, "uploads/default-avatar.png", "0123456789", true, false, Role.CUSTOMER, Rank.BRONZE);
            User owner = new User("owner@gmail.com", "owner", password, "uploads/default-avatar.png", "0123456788", true, false, Role.COURT_OWNER, null);
            User owner1 = new User("owner1@gmail.com", "owner1", password, "uploads/default-avatar.png", "0123456788", true, false, Role.COURT_OWNER, null);

            users.addAll(List.of(admin, customer, owner, owner1));
            new UserDao().saveAll(users);
            users = new UserDao().getAll();

            // Sử dụng equals() thay vì == để so sánh chuỗi
            for (User user : users) {
                if (user.getUsername().equals("owner")) {
                    owner = user;
                }
            }
            System.out.println("Done seed users");

            Venue venue1 = new Venue("Sân cầu lông Đa Phước", "Khu thể thao Tuyên Sơn, Hải Châu, Đà Nẵng",
                    "uploads/premium_photo-1663039984787-b11d7240f592.jpg", LocalTime.parse("06:00:00"),
                    LocalTime.parse("22:00:00"), owner);

            Venue venue2 = new Venue("Sân cầu lông Pinpon", "Sân cầu lông Lê Quang Đạo, Ngũ Hành Sơn, Đà Nẵng",
                    "uploads/tt-1724321381033.jpg", LocalTime.parse("07:00:00"), LocalTime.parse("22:30:00"), owner);

            Venue venue3 = new Venue("Sân cầu lông Arora", "Sân cầu lông Thanh Khê, Đà Nẵng",
                    "uploads/tt-1729344365430.jpg", LocalTime.parse("05:30:00"), LocalTime.parse("22:00:00"), owner);

            Venue venue4 = new Venue("Sân cầu lông IndexSport", "Sân cầu lông Tiến Sơn, Hải Châu, Đà Nẵng",
                    "uploads/badminton.jpg", LocalTime.parse("06:00:00"), LocalTime.parse("21:30:00"), owner);
            List<Venue> venues = new ArrayList<>(List.of(venue1, venue2, venue3, venue4));
            new VenueDao().saveAll(venues);
            venues = new VenueDao().getAll();
            System.out.println("Done seed venues");

            Product p1 = new Product("Coca-Cola", 10000.0, owner, false);
            Product p2 = new Product("Pepsi", 9500.0, owner, false);
            Product p3 = new Product("7Up", 10500.0, owner, false);
            Product p4 = new Product("Sprite", 9900.0, owner, false);
            Product p5 = new Product("Fanta", 11000.0, owner, false);
            List<Product> products = new ArrayList<>(List.of(p1, p2, p3, p4, p5));
            new ProductDao().saveAll(products);

            List<Court> courts = new ArrayList<>();
            for (int i = 0; i < venues.size(); i++) {
                for (int j = 0; j < 12; j++) {
                    courts.add(new Court(String.valueOf(j + 1), true, 80000, venues.get(i), false));
                }
            }
            new CourtDao().saveAll(courts);
            System.out.println("Done seed courts");
        }
        em.getTransaction().commit();
        em.close();
    }
}
