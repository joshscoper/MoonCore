package net.moonfall.mooncore.data;

import net.moonfall.mooncore.MoonCore;
import net.moonfall.mooncore.api.PlayerSyncListener;
import net.moonfall.mooncore.db.DatabaseManager;
import net.moonfall.mooncore.db.PlayerDataDAO;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final MoonCore plugin;
    private final PlayerDataDAO dao;
    private final DatabaseManager databaseManager;

    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerData> writeBuffer = new ConcurrentHashMap<>();
    private final List<PlayerSyncListener> syncListeners = new ArrayList<>();

    public PlayerDataManager(DatabaseManager databaseManager) {
        this.plugin = MoonCore.getInstance();
        this.databaseManager = databaseManager;
        this.dao = new PlayerDataDAO(databaseManager);
    }

    // === Cache Access ===

    public Optional<PlayerData> getCached(UUID uuid) {
        return Optional.ofNullable(cache.get(uuid));
    }

    public PlayerData getOrCreate(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData(uuid, player.getName()));
    }

    // === Load / Save ===

    public CompletableFuture<PlayerData> load(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = player.getUniqueId();
            String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown";

            PlayerData data = dao.load(uuid);
            if (data == null) {
                data = new PlayerData(uuid, player.getName());
                data.setFirstLogin(System.currentTimeMillis());
            }

            data.setUsername(player.getName());
            data.setLastLogin(System.currentTimeMillis());
            data.setLastIp(ip);

            // === Pre-Sync Hook ===
            for (PlayerSyncListener listener : syncListeners) {
                listener.onPreSync(player, data);
            }

            cache.put(uuid, data);

            // === Post-Sync Hook ===
            for (PlayerSyncListener listener : syncListeners) {
                listener.onPostSync(player, data);
            }

            return data;
        });
    }


    public void save(PlayerData data) {
        if (data == null) return;

        if (databaseManager.isConnected()) {
            dao.save(data);
        } else {
            writeBuffer.put(data.getUuid(), data);
        }
    }

    public void saveAll() {
        cache.values().forEach(this::save);
    }

    public void unload(UUID uuid) {
        cache.remove(uuid);
    }

    public void saveAndUnload(PlayerData data) {
        save(data);
        unload(data.getUuid());
    }

    public void flushCache() {
        if (writeBuffer.isEmpty()) {
            plugin.getLogger().info("[MoonCore] No cached player data to flush.");
            return;
        }

        int count = 0;
        for (PlayerData data : writeBuffer.values()) {
            dao.save(data);
            count++;
        }
        writeBuffer.clear();
        plugin.getLogger().info("[MoonCore] Flushed " + count + " cached player data entr" + (count == 1 ? "y" : "ies") + " to the database.");
    }


    // === Inventory ===

    public void updateInventory(Player player) {
        PlayerData data = cache.get(player.getUniqueId());
        if (data == null) return;

        data.setInventory(player.getInventory().getContents());
        data.setEnderChest(player.getEnderChest().getContents());
        data.setArmor(player.getInventory().getArmorContents());
    }

    public void applyInventory(Player player) {
        PlayerData data = cache.get(player.getUniqueId());
        if (data == null) return;

        ItemStack[] inv = data.getInventory();
        ItemStack[] ec = data.getEnderChest();
        ItemStack[] armor = data.getArmor();

        if (inv != null) player.getInventory().setContents(inv);
        if (ec != null) player.getEnderChest().setContents(ec);
        if (armor != null) player.getInventory().setArmorContents(armor);
    }

    public PlayerDataDAO getDao() {
        return dao;
    }


    public void registerSyncListener(PlayerSyncListener listener) {
        syncListeners.add(listener);
    }

}
