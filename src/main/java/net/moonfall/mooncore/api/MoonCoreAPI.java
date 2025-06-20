package net.moonfall.mooncore.api;

import net.moonfall.mooncore.MoonCore;
import net.moonfall.mooncore.data.PlayerData;
import net.moonfall.mooncore.data.PlayerDataManager;
import net.moonfall.mooncore.db.PlayerDataDAO;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MoonCoreAPI {

    private static final PlayerDataManager manager = MoonCore.getInstance().getPlayerDataManager();

    // === Online Cache Access ===

    public static Optional<PlayerData> getOnlinePlayerData(UUID uuid) {
        return manager.getCached(uuid);
    }

    public static Optional<PlayerData> getOnlinePlayerData(Player player) {
        return manager.getCached(player.getUniqueId());
    }

    // === Async Database Access (Offline Supported) ===

    /**
     * Loads player data from the database or creates a new record if not found.
     * Can be used even when the player is offline.
     *
     * @param uuid UUID of the player
     * @param fallbackName name to use if creating a new player data record
     * @return CompletableFuture with loaded PlayerData
     */
    public static CompletableFuture<PlayerData> loadPlayerDataAsync(UUID uuid, String fallbackName) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<PlayerData> cached = manager.getCached(uuid);
            if (cached.isPresent()) return cached.get();

            PlayerDataDAO dao = new PlayerDataDAO(MoonCore.getInstance().getDatabaseManager());
            PlayerData data = dao.load(uuid);

            if (data == null) {
                data = new PlayerData(uuid, fallbackName);
                data.setFirstLogin(System.currentTimeMillis());
            }

            return data;
        });
    }

    // === Save Methods ===

    /**
     * Synchronously saves player data to the database.
     * Avoid using this on the main thread during heavy server load.
     */
    public static void savePlayerData(PlayerData data) {
        manager.save(data);
    }

    /**
     * Asynchronously saves player data to the database.
     * Safe to call from the main thread.
     *
     * @return CompletableFuture<Void> that completes when the save is finished
     */
    public static CompletableFuture<Void> savePlayerDataAsync(PlayerData data) {
        return CompletableFuture.runAsync(() -> {
            manager.save(data);
        });
    }

    /**
     * Immediately flushes the entire cache to the database.
     * Should only be used for admin tools or shutdown logic.
     */
    public static void flushAllCaches() {
        manager.flushCache();
    }

    // === Social Tools ===

    public static boolean areFriends(UUID uuid1, UUID uuid2) {
        return manager.getCached(uuid1)
                .map(data -> data.isFriend(uuid2))
                .orElse(false);
    }

    public static boolean isIgnoring(UUID source, UUID target) {
        return manager.getCached(source)
                .map(data -> data.isIgnoring(target))
                .orElse(false);
    }

    // === Settings Tools ===

    public static boolean acceptsMessages(UUID uuid) {
        return manager.getCached(uuid)
                .map(PlayerData::acceptsMessages)
                .orElse(true);
    }

    public static boolean isPvpEnabled(UUID uuid) {
        return manager.getCached(uuid)
                .map(PlayerData::isPvpEnabled)
                .orElse(true);
    }

    public static boolean allowsTPA(UUID uuid) {
        return manager.getCached(uuid)
                .map(PlayerData::isAllowTeleportRequests)
                .orElse(true);
    }

    public static String getNameColor(UUID uuid) {
        return manager.getCached(uuid)
                .map(PlayerData::getNameColor)
                .orElse("WHITE");
    }
}
