package net.moonfall.mooncore.db;

import net.moonfall.mooncore.MoonCore;
import net.moonfall.mooncore.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DatabaseConnectionWatcher extends BukkitRunnable {

    private final MoonCore plugin;
    private final DatabaseManager database;
    private final PlayerDataManager playerDataManager;

    private boolean lastConnected = true;

    public DatabaseConnectionWatcher(MoonCore plugin, DatabaseManager database, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.database = database;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void run() {
        boolean connected = database.isConnected();

        if (connected && !lastConnected) {
            plugin.getLogger().warning("Database reconnected. Flushing cached player data...");
            playerDataManager.flushCache();
        }

        if (!connected && lastConnected) {
            plugin.getLogger().warning("Database connection lost. Switching to cache mode.");
        }

        lastConnected = connected;
    }

    public void start() {
        this.runTaskTimerAsynchronously(plugin, 20 * 10L, 20 * 30L); // 10s delay, 30s interval
    }
}
