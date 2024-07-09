package de.erze_stein;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public class Messages {

    private final FileConfiguration config;
    private final File messagesFile;

    public Messages(Main plugin) {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(messagesFile);


    }

    /**
     *
     * @param key
     * @param placeholders
     *
     * @return
     */
    public String getMessage(String key, String... placeholders) {
        String path = "messages." + key;
        String message = config.getString(path);

        if (message == null) {
            String notFoundMessage = "Message not found for key: " + key;
            getLogger().warning(notFoundMessage);

            return notFoundMessage;
        }

        for (int i = 0; i < placeholders.length; i += 2) {
            String placeholder = placeholders[i];
            String replacement = placeholders[i + 1];
            message = message.replace(placeholder, replacement);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }




    /**
     *
     *
     * @param key
     */
    public void addDefaultMessage(String key) {
        String path = "messages." + key;
        if (!config.contains(path)) {
            String defaultMessage = "Message not found for key: " + key;

            config.set(path, defaultMessage);

            try {
                config.save(messagesFile);
            } catch (IOException e) {
                getLogger().warning("Error saving messages.yml: " + e.getMessage());
            }

            // Debugging-Ausgabe
            getLogger().info("Added default message for key '" + key + "' to messages.yml");
        }
    }
}
