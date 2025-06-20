package net.moonfall.mooncore.util;

import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Base64;

public class InventorySerializer {

    /**
     * Serializes an array of ItemStacks into a Base64 string.
     *
     * @param items The inventory array (contents, armor, etc.)
     * @return A Base64-encoded string representation of the inventory
     */
    public static String serialize(ItemStack[] items) {
        if (items == null) return "";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeInt(items.length);
            for (ItemStack item : items) {
                oos.writeObject(item);
            }

            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Deserializes a Base64 string back into an ItemStack array.
     *
     * @param base64 The Base64-encoded string
     * @return The deserialized ItemStack array
     */
    public static ItemStack[] deserialize(String base64) {
        if (base64 == null || base64.isEmpty()) return new ItemStack[0];

        try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            int length = ois.readInt();
            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) ois.readObject();
            }

            return items;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }
}
