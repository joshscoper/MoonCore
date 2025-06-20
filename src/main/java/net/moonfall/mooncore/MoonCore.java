package net.moonfall.mooncore;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.moonfall.mooncore.api.MoonCoreAPI;
import net.moonfall.mooncore.commands.MoonCoreCommand;
import net.moonfall.mooncore.data.PlayerDataManager;
import net.moonfall.mooncore.db.DatabaseConnectionWatcher;
import net.moonfall.mooncore.db.DatabaseManager;
import net.moonfall.mooncore.db.SchemaManager;
import net.moonfall.mooncore.listeners.PlayerDataListener;
import net.moonfall.mooncore.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class MoonCore extends JavaPlugin {

    private static MoonCore instance;

    private BukkitAudiences audiences;
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Setup Adventure messaging
        this.audiences = BukkitAudiences.create(this);
        MessageUtil.init(audiences);

        // Setup database and schema
        this.databaseManager = new DatabaseManager(this);
        new SchemaManager(databaseManager).createTables();

        // Player data and sync handling
        this.playerDataManager = new PlayerDataManager(databaseManager);
        new DatabaseConnectionWatcher(this, databaseManager, playerDataManager).start();

        // Register core command
        getCommand("mooncore").setExecutor(new MoonCoreCommand(this));
        getCommand("mooncore").setTabCompleter(new MoonCoreCommand(this));


        // Register listeners
        new PlayerDataListener(this);


        MoonCoreAPI.init(this);
        getLogger().info("MoonCore has been enabled.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAll(); // Persist data
        }

        if (databaseManager != null) {
            databaseManager.shutdown(); // Close DB pool
        }

        if (audiences != null) {
            audiences.close(); // Close Adventure
        }

        getLogger().info("MoonCore has been disabled.");
    }

    public static MoonCore getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
