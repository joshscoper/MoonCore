package net.moonfall.mooncore.commands;

import net.moonfall.mooncore.MoonCore;
import net.moonfall.mooncore.db.DatabaseManager;
import net.moonfall.mooncore.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MoonCoreCommand implements CommandExecutor, TabCompleter {

    private final MoonCore plugin;

    public MoonCoreCommand(MoonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            MessageUtil.send(sender, "<gold>[MoonCore] <yellow>Usage:");
            MessageUtil.send(sender, "<gray> /mooncore reload");
            MessageUtil.send(sender, "<gray> /mooncore flush");
            MessageUtil.send(sender, "<gray> /mooncore debug <player>");
            MessageUtil.send(sender, "<gray> /mooncore connection");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                MessageUtil.send(sender, "<green>MoonCore config reloaded.");
            }
            case "flush" -> {
                plugin.getPlayerDataManager().flushCache();
                MessageUtil.send(sender, "<green>Player data flushed to database.");
            }
            case "debug" -> {
                if (args.length < 2) {
                    MessageUtil.send(sender, "<red>Usage: /mooncore debug <player>");
                    return true;
                }

                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    MessageUtil.send(sender, "<red>Player not found.");
                    return true;
                }

                plugin.getPlayerDataManager().getCached(target.getUniqueId()).ifPresentOrElse(data -> {
                    MessageUtil.send(sender, "<aqua>PlayerData for <white>" + target.getName() + "<aqua>:");
                    MessageUtil.send(sender, "<gray> - Level: <white>" + data.getLevel());
                    MessageUtil.send(sender, "<gray> - XP: <white>" + data.getXp());
                    MessageUtil.send(sender, "<gray> - Balance: <white>" + data.getBalance());
                    MessageUtil.send(sender, "<gray> - Titles: <white>" + data.getTitles());
                }, () -> MessageUtil.send(sender, "<red>No cached data for player."));
            }
            case "connection" -> {
                DatabaseManager db = plugin.getDatabaseManager();
                boolean isConnected = db.isConnected();
                long uptime = db.getUptimeMillis();

                MessageUtil.send(sender, "<gold>[MoonCore] <yellow>Database Status:");
                MessageUtil.send(sender, "<gray> - Connected: " + (isConnected ? "<green>Yes" : "<red>No"));
                MessageUtil.send(sender, "<gray> - Uptime: <white>" + (uptime / 1000) + "s");
                MessageUtil.send(sender, "<gray> - Active Pool Size: <white>" + db.getPoolStats());
            }
            default -> MessageUtil.send(sender, "<red>Unknown subcommand. Use /mooncore for help.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "flush", "debug", "connection");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return Collections.emptyList();
    }
}
