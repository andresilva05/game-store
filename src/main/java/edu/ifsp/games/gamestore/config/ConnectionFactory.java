package edu.ifsp.games.gamestore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.ifsp.games.gamestore.exception.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionFactory {

    private static volatile HikariDataSource dataSource;

    private ConnectionFactory() {
        // Construtor privado para impedir instanciação (classe utilitária Singleton)
    }

    private static HikariDataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (ConnectionFactory.class) {
                if (dataSource == null || dataSource.isClosed()) {
                    dataSource = criarDataSource();
                }
            }
        }
        return dataSource;
    }

    private static HikariDataSource criarDataSource() {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new DataAccessException("Arquivo db.properties não foi encontrado no classpath (src/main/resources).");
            }
            props.load(input);

            String url = resolverPropriedade("DB_URL", props.getProperty("db.url"));
            String user = resolverPropriedade("DB_USER", props.getProperty("db.user"));
            String password = resolverPropriedade("DB_PASS", props.getProperty("db.password"));

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.timeout", "30000")));
            config.setPoolName("GameStoreHikariPool");

            return new HikariDataSource(config);
        } catch (IOException | RuntimeException e) {
            if (e instanceof DataAccessException dae) {
                throw dae;
            }
            throw new DataAccessException("Falha ao inicializar o pool de conexões HikariCP.", e);
        }
    }

    private static String resolverPropriedade(String envVar, String fallback) {
        String envValue = System.getenv(envVar);
        return (envValue != null && !envValue.isBlank()) ? envValue : fallback;
    }

    /**
     * Obtém uma conexão ativa do pool.
     * Sempre utilize dentro de um bloco try-with-resources no DAO!
     */
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("Não foi possível obter uma conexão do pool HikariCP.", e);
        }
    }

    /**
     * Fecha o pool de conexões ao encerrar a aplicação.
     */
    public static synchronized void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }
}
