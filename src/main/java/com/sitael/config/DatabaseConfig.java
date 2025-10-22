package com.sitael.config;

import net.fabricmc.loader.api.FabricLoader;
import com.sitael.ExampleMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DatabaseConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class.getName());

    private final Path configPath;
    private final Properties properties;

    public DatabaseConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        this.configPath = configDir.resolve(ExampleMod.MOD_ID).resolve("database.properties");
        this.properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        setDefaultProperties();

        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
                LOGGER.info("Конфиг загружен из: {}", configPath.toAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Ошибка загрузки конфига, использую значения по умолчанию", e);
            }
        } else {
            saveConfig();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("database.url", "jdbc:postgresql://localhost:5432/minecraft");
        properties.setProperty("database.user", "postgres");
        properties.setProperty("database.password", "postgres");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.connection.pool_size", "10");
    }

    private void saveConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "Database Configuration for MyMod");
                LOGGER.info("Конфиг создан: {}", configPath.toAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка сохранения конфига", e);
        }
    }

    public String getDatabaseUrl() { return properties.getProperty("database.url"); }
    public String getDatabaseUser() { return properties.getProperty("database.user"); }
    public String getDatabasePassword() { return properties.getProperty("database.password"); }
    public String getHibernateDialect() { return properties.getProperty("hibernate.dialect"); }
    public String getHbm2ddlAuto() { return properties.getProperty("hibernate.hbm2ddl.auto"); }
    public boolean showSql() { return Boolean.parseBoolean(properties.getProperty("hibernate.show_sql")); }
    public boolean formatSql() { return Boolean.parseBoolean(properties.getProperty("hibernate.format_sql")); }
    public int getConnectionPoolSize() { return Integer.parseInt(properties.getProperty("hibernate.connection.pool_size")); }
}
