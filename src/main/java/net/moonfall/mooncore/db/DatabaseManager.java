package net.moonfall.mooncore.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.moonfall.mooncore.MoonCore;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final MoonCore plugin;
    private HikariDataSource dataSource;
    private long startedAt;

    public DatabaseManager(MoonCore plugin) {
        this.plugin = plugin;
        setupDataSource();
        this.startedAt = System.currentTimeMillis();
    }

    private void setupDataSource() {
        HikariConfig config = new HikariConfig();

        String host = plugin.getConfig().getString("database.host");
        int port = plugin.getConfig().getInt("database.port");
        String db = plugin.getConfig().getString("database.name");
        String user = plugin.getConfig().getString("database.username");
        String pass = plugin.getConfig().getString("database.password");
        boolean ssl = plugin.getConfig().getBoolean("database.useSSL");
        int poolSize = plugin.getConfig().getInt("database.pool-size");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=" + ssl + "&serverTimezone=UTC";

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(pass);

        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(10000);
        config.setPoolName("MoonCore-Hikari");

        // Recommended MySQL optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        plugin.getLogger().info("Connected to MySQL at " + host + ":" + port);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool shut down.");
        }
    }

    public boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public long getUptimeMillis() {
        return System.currentTimeMillis() - startedAt;
    }

    public String getPoolStats() {
        if (dataSource == null) return "Unavailable";
        var bean = dataSource.getHikariPoolMXBean();
        return bean.getActiveConnections() + " active / " + bean.getTotalConnections() + " total";
    }
}
