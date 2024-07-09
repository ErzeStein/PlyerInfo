package de.erze_stein;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class OfflineDataUtils {

    public static Inventory loadInventoryFromData(OfflinePlayer player, String type) {
        File playerDataFile = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "playerdata/" + player.getUniqueId() + ".dat");
        if (!playerDataFile.exists()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(playerDataFile);
             BukkitObjectInputStream bis = new BukkitObjectInputStream(fis)) {
            YamlConfiguration data = (YamlConfiguration) bis.readObject();
            Inventory inventory;

            if ("EnderChest".equals(type)) {
                inventory = Bukkit.createInventory(null, 27);
            } else {
                inventory = Bukkit.createInventory(null, 36);
            }

            for (String key : data.getConfigurationSection(type).getKeys(false)) {
                int slot = Integer.parseInt(key);
                ItemStack item = data.getItemStack(type + "." + key);
                inventory.setItem(slot, item);
            }

            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().severe("Error on load Player Data: " + e.getMessage());
            return null;
        }
    }
}
