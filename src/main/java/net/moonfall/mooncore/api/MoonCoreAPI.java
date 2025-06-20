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

    private static MoonCore instance;

    public static void init(MoonCore core) {
        instance = core;
    }

    private static PlayerDataManager getManager() {
        if (instance == null) throw new IllegalStateException("MoonCoreAPI not initialized.");
        return instance.getPlayerDataManager();
    }

    // === Online Cache Access ===

    public static Optional<PlayerData> getOnlinePlayerData(UUID uuid) {
        return getManager().getCached(uuid);
    }

    public static Optional<PlayerData> getOnlinePlayerData(Player player) {
        return getManager().getCached(player.getUniqueId());
    }

    // === Async Database Access (Offline Supported) ===

    public static CompletableFuture<PlayerData> loadPlayerDataAsync(UUID uuid, String fallbackName) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<PlayerData> cached = getManager().getCached(uuid);
            if (cached.isPresent()) return cached.get();

            PlayerDataDAO dao = new PlayerDataDAO(instance.getDatabaseManager());
            PlayerData data = dao.load(uuid);

            if (data == null) {
                data = new PlayerData(uuid, fallbackName);
                data.setFirstLogin(System.currentTimeMillis());
            }

            return data;
        });
    }

    // === Save Methods ===

    public static void savePlayerData(PlayerData data) {
        getManager().save(data);
    }

    public static CompletableFuture<Void> savePlayerDataAsync(PlayerData data) {
        return CompletableFuture.runAsync(() -> getManager().save(data));
    }

    public static void flushAllCaches() {
        getManager().flushCache();
    }

    // === Social Tools ===

    public static boolean areFriends(UUID uuid1, UUID uuid2) {
        return getManager().getCached(uuid1)
                .map(data -> data.isFriend(uuid2))
                .orElse(false);
    }

    public static boolean isIgnoring(UUID source, UUID target) {
        return getManager().getCached(source)
                .map(data -> data.isIgnoring(target))
                .orElse(false);
    }

    // === Settings Tools ===

    public static boolean acceptsMessages(UUID uuid) {
        return getManager().getCached(uuid)
                .map(PlayerData::acceptsMessages)
                .orElse(true);
    }

    public static boolean isPvpEnabled(UUID uuid) {
        return getManager().getCached(uuid)
                .map(PlayerData::isPvpEnabled)
                .orElse(true);
    }

    public static boolean allowsTPA(UUID uuid) {
        return getManager().getCached(uuid)
                .map(PlayerData::isAllowTeleportRequests)
                .orElse(true);
    }

    public static String getNameColor(UUID uuid) {
        return getManager().getCached(uuid)
                .map(PlayerData::getNameColor)
                .orElse("WHITE");
    }
}
