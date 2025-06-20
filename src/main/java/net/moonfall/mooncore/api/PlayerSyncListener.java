package net.moonfall.mooncore.api;

import net.moonfall.mooncore.data.PlayerData;
import org.bukkit.entity.Player;

public interface PlayerSyncListener {

    default void onPreSync(Player player, PlayerData preLoadData) {}

    default void onPostSync(Player player, PlayerData syncedData) {}
}
