package net.moonfall.mooncore.util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class ItemNBTUtil {

    public static void setTag(ItemStack item, String key, String value) {
        if (item == null) return;
        NBTItem nbt = new NBTItem(item);
        nbt.setString(key, value);
        nbt.applyNBT(item);
    }

    public static void setTag(ItemStack item, String key, boolean value) {
        if (item == null) return;
        NBTItem nbt = new NBTItem(item);
        nbt.setBoolean(key, value);
        nbt.applyNBT(item);
    }

    public static String getStringTag(ItemStack item, String key) {
        if (item == null) return null;
        NBTItem nbt = new NBTItem(item);
        return nbt.getString(key);
    }

    public static boolean getBooleanTag(ItemStack item, String key) {
        if (item == null) return false;
        NBTItem nbt = new NBTItem(item);
        return nbt.getBoolean(key);
    }

    public static boolean hasTag(ItemStack item, String key) {
        if (item == null) return false;
        NBTItem nbt = new NBTItem(item);
        return nbt.hasTag(key);
    }

    public static void removeTag(ItemStack item, String key) {
        if (item == null) return;
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasTag(key)) {
            nbt.removeKey(key);
            nbt.applyNBT(item);
        }
    }
}
