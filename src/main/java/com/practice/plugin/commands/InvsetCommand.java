package com.practice.plugin.commands;

import com.practice.plugin.managers.GamemodeManager;
import com.practice.plugin.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles /invset command to save player inventory as kit
 */
public class InvsetCommand implements CommandExecutor {
    private ConfigManager configManager;
    private GamemodeManager gamemodeManager;

    public InvsetCommand(ConfigManager configManager, GamemodeManager gamemodeManager) {
        this.configManager = configManager;
        this.gamemodeManager = gamemodeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (!sender.hasPermission("practiceplugin.invset")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§cUsage: /invset <gamemode>");
            return true;
        }

        String gamemodeName = args[0].toLowerCase();

        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            player.sendMessage("§cGamemode '" + gamemodeName + "' does not exist!");
            return true;
        }

        // Save inventory to config
        ItemStack[] inventory = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();

        String path = "kits." + player.getName() + "." + gamemodeName;
        var config = configManager.getKitsConfig();

        // Save armor
        config.set(path + ".helmet", armor[3]);
        config.set(path + ".chestplate", armor[2]);
        config.set(path + ".leggings", armor[1]);
        config.set(path + ".boots", armor[0]);

        // Save inventory items
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                config.set(path + ".inventory." + i, inventory[i]);
            }
        }

        configManager.saveKitsConfig();
        player.sendMessage("§aInventory kit saved for " + gamemodeName + "!");

        return true;
    }
}
