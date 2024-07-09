package de.erze_stein;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Economy economy = null;
    private static Permission permissions = null;
    private Chat chat = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!setupChat()) {
            getLogger().info("Chat-Plugin not found. Economy data will not be available.");
        }
        if (!setupEconomy()) {
            getLogger().info("Vault-Economy not found. Economy data will not be available.");
        }
        if (!setupPermissions()) {
            getLogger().info("Vault-Permissions not found. Permissions data will not be available.");
        }

        this.getLogger().info("Loading Command");
        this.getCommand("playerinfo").setExecutor(new PlayerInfo(this));
        this.getLogger().info("Plugin is ready to use");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        permissions = rsp.getProvider();
        return permissions != null;
    }

    public static Economy getEconomy() {
        return economy;
    }


    private boolean setupChat() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }

    public static Permission getPermissions() {
        return permissions;
    }
    public Chat getChat() {
        return chat;
    }
}
