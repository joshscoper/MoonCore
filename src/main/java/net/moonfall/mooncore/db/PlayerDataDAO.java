package net.moonfall.mooncore.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.moonfall.mooncore.data.PlayerData;
import net.moonfall.mooncore.util.InventorySerializer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class PlayerDataDAO {

    private final DatabaseManager db;
    private final Gson gson = new Gson();

    public PlayerDataDAO(DatabaseManager db) {
        this.db = db;
        ensureSchema();
    }

    private void ensureSchema() {
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, "player_data", "active_title")) {
                if (!rs.next()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("ALTER TABLE player_data ADD COLUMN active_title VARCHAR(255)");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void save(PlayerData data) {
        String sql = """
            INSERT INTO player_data (
                uuid, username, first_login, last_login, last_ip,
                level, xp, playtime_ticks, balance,
                inventory, enderchest, armor,
                tags, titles, inbox, friends, ignored,
                name_color, unlocked_name_colors,
                settlement_name, settlement_rank,
                active_party_id, last_messaged,
                is_muted, is_banned, is_shadow_muted, mute_until, ban_until, ban_reason, punishment_log,
                pvp_enabled, accepts_messages, show_join_leave_messages, allow_teleport_requests, show_hud,
                active_title, metadata
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                username = VALUES(username),
                last_login = VALUES(last_login),
                last_ip = VALUES(last_ip),
                level = VALUES(level),
                xp = VALUES(xp),
                playtime_ticks = VALUES(playtime_ticks),
                balance = VALUES(balance),
                inventory = VALUES(inventory),
                enderchest = VALUES(enderchest),
                armor = VALUES(armor),
                tags = VALUES(tags),
                titles = VALUES(titles),
                inbox = VALUES(inbox),
                friends = VALUES(friends),
                ignored = VALUES(ignored),
                name_color = VALUES(name_color),
                unlocked_name_colors = VALUES(unlocked_name_colors),
                settlement_name = VALUES(settlement_name),
                settlement_rank = VALUES(settlement_rank),
                active_party_id = VALUES(active_party_id),
                last_messaged = VALUES(last_messaged),
                is_muted = VALUES(is_muted),
                is_banned = VALUES(is_banned),
                is_shadow_muted = VALUES(is_shadow_muted),
                mute_until = VALUES(mute_until),
                ban_until = VALUES(ban_until),
                ban_reason = VALUES(ban_reason),
                punishment_log = VALUES(punishment_log),
                pvp_enabled = VALUES(pvp_enabled),
                accepts_messages = VALUES(accepts_messages),
                show_join_leave_messages = VALUES(show_join_leave_messages),
                allow_teleport_requests = VALUES(allow_teleport_requests),
                show_hud = VALUES(show_hud),
                active_title = VALUES(active_title),
                metadata = VALUES(metadata)
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getUsername());
            stmt.setLong(3, data.getFirstLogin());
            stmt.setLong(4, data.getLastLogin());
            stmt.setString(5, data.getLastIp());

            stmt.setInt(6, data.getLevel());
            stmt.setInt(7, data.getXp());
            stmt.setLong(8, data.getPlaytimeTicks());
            stmt.setDouble(9, data.getBalance());

            stmt.setString(10, InventorySerializer.serialize(data.getInventory()));
            stmt.setString(11, InventorySerializer.serialize(data.getEnderChest()));
            stmt.setString(12, InventorySerializer.serialize(data.getArmor()));

            stmt.setString(13, gson.toJson(data.getTags()));
            stmt.setString(14, gson.toJson(data.getTitles()));
            stmt.setString(15, gson.toJson(data.getInbox()));
            stmt.setString(16, gson.toJson(data.getFriends()));
            stmt.setString(17, gson.toJson(data.getIgnored()));

            stmt.setString(18, data.getNameColor());
            stmt.setString(19, gson.toJson(data.getUnlockedNameColors()));

            stmt.setString(20, data.getSettlementName());
            stmt.setString(21, data.getSettlementRank());

            stmt.setString(22, data.getActivePartyId() != null ? data.getActivePartyId().toString() : null);
            stmt.setString(23, data.getLastMessaged() != null ? data.getLastMessaged().toString() : null);

            stmt.setBoolean(24, data.isMuted());
            stmt.setBoolean(25, data.isBanned());
            stmt.setBoolean(26, data.isShadowMuted());
            stmt.setLong(27, data.getMuteUntil());
            stmt.setLong(28, data.getBanUntil());
            stmt.setString(29, data.getBanReason());
            stmt.setString(30, gson.toJson(data.getPunishmentLog()));

            stmt.setBoolean(31, data.isPvpEnabled());
            stmt.setBoolean(32, data.acceptsMessages());
            stmt.setBoolean(33, data.isShowJoinLeaveMessages());
            stmt.setBoolean(34, data.isAllowTeleportRequests());
            stmt.setBoolean(35, data.isShowHud());

            stmt.setString(36, data.getActiveTitle());
            stmt.setString(37, gson.toJson(data.getMetadata()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData load(UUID uuid) {
        String sql = "SELECT * FROM player_data WHERE uuid = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PlayerData data = new PlayerData(uuid, rs.getString("username"));
                    data.setFirstLogin(rs.getLong("first_login"));
                    data.setLastLogin(rs.getLong("last_login"));
                    data.setLastIp(rs.getString("last_ip"));

                    data.setLevel(rs.getInt("level"));
                    data.setXp(rs.getInt("xp"));
                    data.setPlaytimeTicks(rs.getLong("playtime_ticks"));
                    data.setBalance(rs.getDouble("balance"));

                    data.setInventory(InventorySerializer.deserialize(rs.getString("inventory")));
                    data.setEnderChest(InventorySerializer.deserialize(rs.getString("enderchest")));
                    data.setArmor(InventorySerializer.deserialize(rs.getString("armor")));

                    Type setStr = new TypeToken<Set<String>>() {}.getType();
                    Type listStr = new TypeToken<List<String>>() {}.getType();
                    Type setUUID = new TypeToken<Set<UUID>>() {}.getType();
                    Type mapObj = new TypeToken<Map<String, Object>>() {}.getType();

                    data.getTags().addAll(gson.fromJson(rs.getString("tags"), setStr));
                    data.getTitles().addAll(gson.fromJson(rs.getString("titles"), listStr));
                    data.getInbox().addAll(gson.fromJson(rs.getString("inbox"), listStr));
                    data.getFriends().addAll(gson.fromJson(rs.getString("friends"), setUUID));
                    data.getIgnored().addAll(gson.fromJson(rs.getString("ignored"), setUUID));

                    data.setNameColor(rs.getString("name_color"));
                    data.getUnlockedNameColors().addAll(gson.fromJson(rs.getString("unlocked_name_colors"), setStr));

                    data.setSettlementName(rs.getString("settlement_name"));
                    data.setSettlementRank(rs.getString("settlement_rank"));

                    String activePartyId = rs.getString("active_party_id");
                    if (activePartyId != null) data.setActivePartyId(UUID.fromString(activePartyId));

                    String lastMessaged = rs.getString("last_messaged");
                    if (lastMessaged != null) data.setLastMessaged(UUID.fromString(lastMessaged));

                    data.setMuted(rs.getBoolean("is_muted"));
                    data.setBanned(rs.getBoolean("is_banned"));
                    data.setShadowMuted(rs.getBoolean("is_shadow_muted"));
                    data.setMuteUntil(rs.getLong("mute_until"));
                    data.setBanUntil(rs.getLong("ban_until"));
                    data.setBanReason(rs.getString("ban_reason"));
                    data.getPunishmentLog().addAll(gson.fromJson(rs.getString("punishment_log"), listStr));

                    data.setPvpEnabled(rs.getBoolean("pvp_enabled"));
                    data.setAcceptsMessages(rs.getBoolean("accepts_messages"));
                    data.setShowJoinLeaveMessages(rs.getBoolean("show_join_leave_messages"));
                    data.setAllowTeleportRequests(rs.getBoolean("allow_teleport_requests"));
                    data.setShowHud(rs.getBoolean("show_hud"));

                    data.setActiveTitle(rs.getString("active_title"));

                    Map<String, Object> meta = gson.fromJson(rs.getString("metadata"), mapObj);
                    if (meta != null) data.getMetadata().putAll(meta);

                    return data;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
