package com.practice.plugin.commands;

import com.practice.plugin.managers.GamemodeManager;
import com.practice.plugin.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles /setrounds command to set rounds needed for a gamemode
 */
public class SetRoundsCommand implements CommandExecutor {
    private GamemodeManager gamemodeManager;
    private ConfigManager configManager;

    public SetRoundsCommand(GamemodeManager gamemodeManager, ConfigManager configManager) {
        this.gamemodeManager = gamemodeManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practiceplugin.gamemode.create")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /setrounds <gamemode> <rounds>");
            return true;
        }

        String gamemodeName = args[0].toLowerCase();
        String roundsStr = args[1];

        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            sender.sendMessage("§cGamemode '" + gamemodeName + "' does not exist!");
            return true;
        }

        try {
            int rounds = Integer.parseInt(roundsStr);
            
            if (rounds < 1) {
                sender.sendMessage("§cRounds must be at least 1!");
                return true;
            }

            gamemodeManager.getGamemode(gamemodeName).setRoundsNeeded(rounds);
            
            // Save to config
            configManager.getGamemodesConfig().set("gamemodes." + gamemodeName + ".rounds", rounds);
            configManager.saveGamemodesConfig();

            sender.sendMessage("§aRounds set for " + gamemodeName + ": First to " + rounds);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cRounds must be a number!");
        }

        return true;
    }
}
