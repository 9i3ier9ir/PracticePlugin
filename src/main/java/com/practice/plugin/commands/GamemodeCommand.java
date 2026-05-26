package com.practice.plugin.commands;

import com.practice.plugin.managers.GamemodeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles /gamemode commands
 */
public class GamemodeCommand implements CommandExecutor {
    private GamemodeManager gamemodeManager;

    public GamemodeCommand(GamemodeManager gamemodeManager) {
        this.gamemodeManager = gamemodeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practiceplugin.gamemode.create")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /gamemode create <name>");
            return true;
        }

        if (!args[0].equalsIgnoreCase("create")) {
            sender.sendMessage("§cUsage: /gamemode create <name>");
            return true;
        }

        String gamemodeName = args[1].toLowerCase();
        
        if (gamemodeManager.gamemodeExists(gamemodeName)) {
            sender.sendMessage("§cGamemode '" + gamemodeName + "' already exists!");
            return true;
        }

        if (gamemodeManager.createGamemode(gamemodeName)) {
            sender.sendMessage("§aGamemode '" + gamemodeName + "' created successfully!");
        } else {
            sender.sendMessage("§cFailed to create gamemode!");
        }

        return true;
    }
}
