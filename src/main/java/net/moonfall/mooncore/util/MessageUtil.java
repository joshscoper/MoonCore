package net.moonfall.mooncore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private static BukkitAudiences audiences;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacyAmp = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer legacySection = LegacyComponentSerializer.legacySection();

    public static void init(BukkitAudiences audiencesInstance) {
        audiences = audiencesInstance;
    }

    public static void send(CommandSender sender, String message) {
        if (sender instanceof Player player && audiences != null) {
            audiences.player(player).sendMessage(parse(message));
        } else {
            sender.sendMessage(legacyAmp.serialize(parse(message)));
        }
    }

    public static Component parse(String input) {
        // MiniMessage parses tags like <gradient> or <hover> and hex codes like <#FF0000>
        try {
            return miniMessage.deserialize(input);
        } catch (Exception e) {
            // fallback for legacy & formatting
            return legacyAmp.deserialize(input);
        }
    }

    public static String strip(String input) {
        return ChatColor.stripColor(input);
    }
}
