package by.eugene.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Properties appProps = new Properties();

            try (InputStream is = HibernateUtil.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties")) {

                if (is == null) {
                    throw new RuntimeException("application.properties not found");
                }
                appProps.load(is);
            }

            Properties hibernateProps = new Properties();
            hibernateProps.put("hibernate.connection.driver_class",
                    System.getProperty("db.driver", appProps.getProperty("db.driver")));
            hibernateProps.put("hibernate.connection.url",
                    System.getProperty("db.url", appProps.getProperty("db.url")));
            hibernateProps.put("hibernate.connection.username",
                    System.getProperty("db.username", appProps.getProperty("db.username")));
            hibernateProps.put("hibernate.connection.password",
                    System.getProperty("db.password", appProps.getProperty("db.password")));
            hibernateProps.put("hibernate.dialect", appProps.getProperty("hibernate.dialect"));
            hibernateProps.put("hibernate.hbm2ddl.auto", "create-drop");
            hibernateProps.put("hibernate.show_sql", "false");
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(by.eugene.model.User.class);
            configuration.addProperties(hibernateProps);
            return configuration.buildSessionFactory();
        } catch (HibernateException e) {
            logger.error("Failed to create SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        } catch (Exception e) {
            logger.error("Configuration error", e);
            throw new RuntimeException(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }


    public static void shutdown() {
        sessionFactory.close();
    }
}

