package com.practice.plugin.commands;

import com.practice.plugin.managers.QueueManager;
import com.practice.plugin.managers.GamemodeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles /duel and /d commands to challenge another player
 */
public class DuelCommand implements CommandExecutor {
    private QueueManager queueManager;
    private GamemodeManager gamemodeManager;

    public DuelCommand(QueueManager queueManager, GamemodeManager gamemodeManager) {
        this.queueManager = queueManager;
        this.gamemodeManager = gamemodeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (!sender.hasPermission("practiceplugin.duel")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player challenger = (Player) sender;

        if (args.length < 3) {
            challenger.sendMessage("§cUsage: /duel <player> <gamemode> <rounds>");
            challenger.sendMessage("§cExample: /duel Steve boxfight 3");
            return true;
        }

        String targetName = args[0];
        String gamemodeName = args[1].toLowerCase();
        String roundsStr = args[2];

        // Get target player
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            challenger.sendMessage("§cPlayer '" + targetName + "' not found!");
            return true;
        }

        if (target.equals(challenger)) {
            challenger.sendMessage("§cYou can't duel yourself!");
            return true;
        }

        // Check if gamemode exists
        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            challenger.sendMessage("§cGamemode '" + gamemodeName + "' does not exist!");
            return true;
        }

        // Parse rounds
        try {
            int rounds = Integer.parseInt(roundsStr);
            if (rounds < 1) {
                challenger.sendMessage("§cRounds must be at least 1!");
                return true;
            }

            // Check if players are already in matches/duels
            if (queueManager.isInMatch(challenger)) {
                challenger.sendMessage("§cYou're already in a match!");
                return true;
            }

            if (queueManager.isInDuel(challenger)) {
                challenger.sendMessage("§cYou're already in a duel!");
                return true;
            }

            if (queueManager.isInMatch(target)) {
                challenger.sendMessage("§c" + target.getName() + " is already in a match!");
                return true;
            }

            if (queueManager.isInDuel(target)) {
                challenger.sendMessage("§c" + target.getName() + " is already in a duel!");
                return true;
            }

            // Check if maps are available
            if (gamemodeManager.getAvailableMaps(gamemodeName).isEmpty()) {
                challenger.sendMessage("§cNo maps available for " + gamemodeName + "!");
                return true;
            }

            // Start the duel
            queueManager.startDuel(challenger, target, gamemodeName, rounds);
            
        } catch (NumberFormatException e) {
            challenger.sendMessage("§cRounds must be a number!");
        }

        return true;
    }
}
