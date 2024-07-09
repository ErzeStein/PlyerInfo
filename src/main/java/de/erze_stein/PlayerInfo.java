package de.erze_stein;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class PlayerInfo implements CommandExecutor {

    private final Main plugin;
    private final Messages messages;

    public PlayerInfo(Main plugin) {
        this.plugin = plugin;
        this.messages = new Messages(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(messages.getMessage("usage"));
            return false;
        }

        if(!sender.hasPermission("playerinfo.use")){
            sender.sendMessage(messages.getMessage("nopermission"));

        }

        String input = args[args.length - 1];
        OfflinePlayer targetPlayer;

        try {
            UUID uuid = UUID.fromString(input);
            targetPlayer = Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            targetPlayer = Bukkit.getOfflinePlayer(input);
        }

        if (targetPlayer == null) {
            sender.sendMessage(messages.getMessage("player_not_found", "%player%", input));
            return true;
        }

        if (args.length == 1) {
            sendPlayerInfo(sender, targetPlayer);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(messages.getMessage("player_only_command"));
                return true;
            }

            Player playerSender = (Player) sender;
            String action = args[0];

            if ("inv".equalsIgnoreCase(action)) {
                openPlayerInventory(playerSender, targetPlayer);
            } else if ("end".equalsIgnoreCase(action)) {
                openPlayerEnderChest(playerSender, targetPlayer);
            } else {
                sender.sendMessage(messages.getMessage("invalid_action"));
            }
        }

        return true;
    }

    private void sendPlayerInfo(CommandSender sender, OfflinePlayer targetPlayer) {
        sender.sendMessage(messages.getMessage("player_name", "%playername%", targetPlayer.getName()));
        sender.sendMessage(messages.getMessage("player_uuid", "%playeruuid%", targetPlayer.getUniqueId().toString()));
        sender.sendMessage(messages.getMessage("has_played_before", "%playedbefore%", (targetPlayer.hasPlayedBefore() ? "Yes" : "No")));
        sender.sendMessage(messages.getMessage("last_online", "%lastonline%", (targetPlayer.isOnline() ? "Currently online" : formatTime(targetPlayer.getLastPlayed()))));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Economy economy = plugin.getEconomy();
            if (economy != null) {
                double balance = economy.getBalance(targetPlayer);
                sender.sendMessage(messages.getMessage("balance", "%balance%", economy.format(balance)));
            } else {
                sender.sendMessage(messages.getMessage("economy_not_found"));
            }

            Permission permissions = plugin.getPermissions();
            Chat chat = plugin.getChat();
            if (permissions != null && chat != null) {
                String[] groups = permissions.getPlayerGroups(null, targetPlayer);
                if (groups.length > 0) {
                    String prefix = chat.getGroupPrefix((String) null, groups[0]);
                    if (prefix == null || prefix.isEmpty()) {
                        prefix = "No prefix found";
                    }
                    sender.sendMessage(messages.getMessage("rank", "%rank%", prefix));
                } else {
                    sender.sendMessage(messages.getMessage("no_rank_found"));
                }
            } else {
                sender.sendMessage(messages.getMessage("permissions_not_found"));
            }
        });
    }

    private void openPlayerInventory(Player sender, OfflinePlayer targetPlayer) {
        Inventory inventory;
        if (targetPlayer.isOnline()) {
            Player onlinePlayer = (Player) targetPlayer;
            inventory = onlinePlayer.getInventory();
        } else {
            inventory = OfflineDataUtils.loadInventoryFromData(targetPlayer, "Inventory");
            if (inventory == null) {
                inventory = Bukkit.createInventory(null, 36);
            }
        }

        sender.openInventory(inventory);
    }

    private void openPlayerEnderChest(Player sender, OfflinePlayer targetPlayer) {
        Inventory enderChest;
        if (targetPlayer.isOnline()) {
            Player onlinePlayer = (Player) targetPlayer;
            enderChest = onlinePlayer.getEnderChest();
        } else {
            enderChest = OfflineDataUtils.loadInventoryFromData(targetPlayer, "EnderChest");
            if (enderChest == null) {
                enderChest = Bukkit.createInventory(null, 27);
            }
        }

        sender.openInventory(enderChest);
    }

    private String formatTime(long time) {
        if (time == 0) return "Nie";
        return new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date(time));
    }
}
