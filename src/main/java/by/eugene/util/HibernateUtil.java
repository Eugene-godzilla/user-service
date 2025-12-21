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
    private static final SessionFactory sessionFactory = buildSessionFactory();

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
            hibernateProps.put("hibernate.connection.driver_class", appProps.getProperty("db.driver"));
            hibernateProps.put("hibernate.connection.url", appProps.getProperty("db.url"));
            hibernateProps.put("hibernate.connection.username", appProps.getProperty("db.username"));
            hibernateProps.put("hibernate.connection.password", appProps.getProperty("db.password"));

            hibernateProps.put("hibernate.dialect", appProps.getProperty("hibernate.dialect"));
            hibernateProps.put("hibernate.hbm2ddl.auto", appProps.getProperty("hibernate.hbm2ddl.auto"));
            hibernateProps.put("hibernate.show_sql", appProps.getProperty("hibernate.show_sql"));
            hibernateProps.put("hibernate.format_sql", appProps.getProperty("hibernate.format_sql"));

            // optional pool settings
            hibernateProps.put("hibernate.c3p0.min_size", appProps.getProperty("hibernate.c3p0.min_size"));
            hibernateProps.put("hibernate.c3p0.max_size", appProps.getProperty("hibernate.c3p0.max_size"));
            hibernateProps.put("hibernate.c3p0.timeout", appProps.getProperty("hibernate.c3p0.timeout"));

            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
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
        return sessionFactory;
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}

