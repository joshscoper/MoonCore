package net.moonfall.mooncore.db;

import java.sql.*;
import java.util.Map;

public class SchemaUpdater {

    public static void ensureTableAndColumns(DatabaseManager db, String tableName, Map<String, String> columns) {
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement()) {
            DatabaseMetaData meta = conn.getMetaData();

            // Check if table exists
            try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
                if (!rs.next()) {
                    // Create table with all columns
                    StringBuilder createSql = new StringBuilder("CREATE TABLE " + tableName + " (");
                    for (Map.Entry<String, String> entry : columns.entrySet()) {
                        createSql.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
                    }
                    createSql.setLength(createSql.length() - 2); // Remove trailing comma
                    createSql.append(")");
                    stmt.executeUpdate(createSql.toString());
                    return;
                }
            }

            // Check for missing columns and add them
            try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
                Map<String, Boolean> existing = new java.util.HashMap<>();
                while (rs.next()) {
                    existing.put(rs.getString("COLUMN_NAME"), true);
                }

                for (Map.Entry<String, String> entry : columns.entrySet()) {
                    if (!existing.containsKey(entry.getKey())) {
                        String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + entry.getKey() + " " + entry.getValue();
                        stmt.executeUpdate(alterSql);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
