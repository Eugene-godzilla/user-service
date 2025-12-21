package by.eugene;

import by.eugene.dao.UserDao;
import by.eugene.dao.UserDaoImpl;
import by.eugene.model.User;
import by.eugene.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainConsoleApp {
    private static final Logger logger = LoggerFactory.getLogger(MainConsoleApp.class);
    private final UserDao userDao = new UserDaoImpl();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        MainConsoleApp app = new MainConsoleApp();
        try {
            app.run();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": createUser(); break;
                case "2": listUsers(); break;
                case "3": getUserById(); break;
                case "4": updateUser(); break;
                case "5": deleteUser(); break;
                case "0": return;
                default: System.out.println("Unknown option"); break;
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== User Service ===");
        System.out.println("1) Create user");
        System.out.println("2) List all users");
        System.out.println("3) Get user by id");
        System.out.println("4) Update user");
        System.out.println("5) Delete user");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private void createUser() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Age (number): ");
            Integer age = parseIntOrNull(scanner.nextLine().trim());

            User user = User.builder()
                    .name(name)
                    .email(email)
                    .age(age)
                    .build();

            userDao.save(user);
            System.out.println("Created user id: " + user.getId());
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listUsers() {
        List<User> all = userDao.findAll();
        if (all.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        all.forEach(u -> System.out.printf("%d | %s | %s | %s%n", u.getId(), u.getName(), u.getEmail(), u.getCreatedAt()));
    }

    private void getUserById() {
        System.out.print("Id: ");
        Long id = parseLongOrNull(scanner.nextLine().trim());
        if (id == null) {
            System.out.println("Invalid id");
            return;
        }
        Optional<User> user = userDao.findById(id);
        user.ifPresentOrElse(
                u -> System.out.printf("User: %d | %s | %s | %s | age=%s%n", u.getId(), u.getName(), u.getEmail(), u.getCreatedAt(), u.getAge()),
                () -> System.out.println("User not found")
        );
    }

    private void updateUser() {
        System.out.print("Id to update: ");
        Long id = parseLongOrNull(scanner.nextLine().trim());
        if (id == null) { System.out.println("Invalid id"); return; }

        Optional<User> maybe = userDao.findById(id);
        if (maybe.isEmpty()) { System.out.println("User not found"); return; }
        User user = maybe.get();

        System.out.printf("Current name [%s]: ", user.getName());
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) user.setName(name);

        System.out.printf("Current email [%s]: ", user.getEmail());
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.printf("Current age [%s]: ", user.getAge());
        String ageStr = scanner.nextLine().trim();
        Integer age = parseIntOrNull(ageStr);
        if (age != null) user.setAge(age);

        try {
            userDao.update(user);
            System.out.println("Updated user");
        } catch (Exception e) {
            logger.error("Failed to update", e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.print("Id to delete: ");
        Long id = parseLongOrNull(scanner.nextLine().trim());
        if (id == null) { System.out.println("Invalid id"); return; }
        try {
            boolean ok = userDao.delete(id);
            System.out.println(ok ? "Deleted" : "Not found");
        } catch (Exception e) {
            logger.error("Delete failed", e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Integer parseIntOrNull(String s) {
        try { return s.isEmpty() ? null : Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }

    private Long parseLongOrNull(String s) {
        try { return s.isEmpty() ? null : Long.parseLong(s); }
        catch (NumberFormatException e) { return null; }
    }
}
