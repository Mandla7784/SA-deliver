package main.java.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.File;

public class DatabaseUtil {
    private static SessionFactory sessionFactory;
    
    private DatabaseUtil() {
        // Private constructor to prevent instantiation
    }
    
    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create the SessionFactory from hibernate.cfg.xml
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                
                // Apply properties for HikariCP
                configuration.setProperty("hibernate.hikari.dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
                configuration.setProperty("hibernate.hikari.dataSource.url", 
                    "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                configuration.setProperty("hibernate.hikari.dataSource.user", "sa");
                configuration.setProperty("hibernate.hikari.dataSource.password", "");
                
                // Build the ServiceRegistry
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .configure()
                    .build();
                
                // Create the SessionFactory
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                
                // Initialize database with sample data
                initializeDatabase();
                
            } catch (Throwable ex) {
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }
    
    private static void initializeDatabase() {
        // This method can be used to populate initial data
        // For example, create an admin user or default categories
    }
    
    public static void shutdown() {
        // Close caches and connection pools
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
