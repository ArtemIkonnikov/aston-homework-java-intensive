package ru.aston.userservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.userservice.console.ConsoleMenu;
import ru.aston.userservice.dao.UserDao;
import ru.aston.userservice.dao.UserDaoImpl;
import ru.aston.userservice.util.HibernateUtil;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting user-service application");

        UserDao userDao = new UserDaoImpl();
        ConsoleMenu menu = new ConsoleMenu(userDao);

        try {
            menu.run();
        } catch (Exception e) {
            log.error("Application failed unexpectedly: {}", e.getMessage(), e);
        } finally {
            HibernateUtil.shutdown();
            log.info("Application stopped");
        }
    }
}
