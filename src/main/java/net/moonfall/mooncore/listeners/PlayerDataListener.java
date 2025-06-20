package net.moonfall.mooncore.listeners;

import net.moonfall.mooncore.MoonCore;
import net.moonfall.mooncore.data.PlayerData;
import net.moonfall.mooncore.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {

    private final MoonCore plugin;
    private final PlayerDataManager dataManager;

    public PlayerDataListener(MoonCore plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getPlayerDataManager();
        Bukkit.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        dataManager.load(player).thenAccept(data -> {
            // Apply saved inventory (optional)
            Bukkit.getScheduler().runTask(plugin, () -> dataManager.applyInventory(player));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = dataManager.getOrCreate(player);

        // Save current inventory state
        dataManager.updateInventory(player);

        // Async save
        dataManager.save(data);

        // Optional: unload from cache if desired
        dataManager.unload(player.getUniqueId());
    }
}
