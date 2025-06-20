package net.moonfall.mooncore.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    private final DatabaseManager database;

    public SchemaManager(DatabaseManager database) {
        this.database = database;
    }

    public void createTables() {
        createPlayerDataTable();
    }

    private void createPlayerDataTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_data (
                uuid VARCHAR(36) PRIMARY KEY,
                username VARCHAR(32) NOT NULL,
                first_login BIGINT NOT NULL,
                last_login BIGINT NOT NULL,
                last_ip VARCHAR(45),

                level INT DEFAULT 0,
                xp INT DEFAULT 0,
                playtime_ticks BIGINT DEFAULT 0,
                balance DOUBLE DEFAULT 0,

                inventory LONGTEXT,
                enderchest LONGTEXT,
                armor LONGTEXT,

                tags TEXT,
                titles TEXT,
                inbox TEXT,
                friends TEXT,
                ignored TEXT,

                name_color VARCHAR(16),
                unlocked_name_colors TEXT,

                settlement_name VARCHAR(64),
                settlement_rank VARCHAR(64),

                active_party_id VARCHAR(36),
                last_messaged VARCHAR(36),

                is_muted BOOLEAN DEFAULT FALSE,
                is_banned BOOLEAN DEFAULT FALSE,
                is_shadow_muted BOOLEAN DEFAULT FALSE,
                mute_until BIGINT,
                ban_until BIGINT,
                ban_reason TEXT,
                punishment_log TEXT,

                pvp_enabled BOOLEAN DEFAULT TRUE,
                accepts_messages BOOLEAN DEFAULT TRUE,
                show_join_leave_messages BOOLEAN DEFAULT TRUE,
                allow_teleport_requests BOOLEAN DEFAULT TRUE,
                show_hud BOOLEAN DEFAULT TRUE,

                metadata LONGTEXT
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;

        try (Connection conn = database.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
