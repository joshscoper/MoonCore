package net.moonfall.mooncore.api;

import net.moonfall.mooncore.MoonCore;
import net.moonfall.mooncore.data.PlayerData;
import net.moonfall.mooncore.data.PlayerDataManager;
import net.moonfall.mooncore.db.PlayerDataDAO;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MoonCoreAPI {

    private static MoonCore plugin;
    private static boolean warned = false;

    /**
     * Initialize the MoonCoreAPI. Should be called from MoonCore's onEnable.
     */
    public static void init(MoonCore core) {
        if (plugin == null) {
            plugin = core;
        }
    }

    public static boolean isInitialized() {
        return plugin != null;
    }

    private static PlayerDataManager getManager() {
        if (plugin == null) {
            throw new IllegalStateException("MoonCoreAPI has not been initialized. Call MoonCoreAPI.init() during plugin enable.");
        }
        return plugin.getPlayerDataManager();
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

            PlayerDataDAO dao = new PlayerDataDAO(plugin.getDatabaseManager());
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

    // === Titles ===

    public static void setActiveTitle(UUID uuid, String titleId) {
        getManager().getCached(uuid).ifPresent(data -> {
            data.setActiveTitle(titleId);
            savePlayerDataAsync(data);
        });
    }

    public static String getActiveTitle(UUID uuid) {
        return getManager().getCached(uuid)
                .map(PlayerData::getActiveTitle)
                .orElse(null);
    }

    public static void clearActiveTitle(UUID uuid) {
        getManager().getCached(uuid).ifPresent(data -> {
            data.setActiveTitle(null);
            savePlayerDataAsync(data);
        });
    }

    public static List<String> getUnlockedTitles(UUID uuid) {
        return getManager().getCached(uuid)
                .map(data -> {
                    List<String> result = new ArrayList<>();
                    for (Object o : data.getTitles()) {
                        if (o instanceof String s) {
                            result.add(s);
                        }
                    }
                    return result;
                })
                .orElse(List.of());
    }



    public static boolean hasTitleUnlocked(UUID uuid, String title) {
        return getManager().getCached(uuid)
                .map(data -> data.getTitles().contains(title))
                .orElse(false);
    }

    public static void unlockTitle(UUID uuid, String title) {
        getManager().getCached(uuid).ifPresent(data -> {
            if (data.getTitles().add(title)) {
                savePlayerDataAsync(data);
            }
        });
    }

    public static void removeTitle(UUID uuid, String title) {
        getManager().getCached(uuid).ifPresent(data -> {
            if (data.getTitles().remove(title)) {
                savePlayerDataAsync(data);
            }
        });
    }
}
