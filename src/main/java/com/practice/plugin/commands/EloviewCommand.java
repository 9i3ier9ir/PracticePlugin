package com.practice.plugin.commands;

import com.practice.plugin.managers.EloManager;
import com.practice.plugin.utils.EloCalculator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Handles /eloview command to display player ELO stats
 */
public class EloviewCommand implements CommandExecutor {
    private EloManager eloManager;

    public EloviewCommand(EloManager eloManager) {
        this.eloManager = eloManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practiceplugin.eloview")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        String playerName;

        if (args.length > 0) {
            playerName = args[0];
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cUsage: /eloview [player]");
                return true;
            }
            playerName = ((Player) sender).getName();
        }

        displayEloStats(sender, playerName);
        return true;
    }

    private void displayEloStats(CommandSender sender, String playerName) {
        Map<String, Integer> allElo = eloManager.getPlayerAllGamemodeElo(playerName);

        if (allElo.isEmpty()) {
            sender.sendMessage("§c" + playerName + " has no stats yet!");
            return;
        }

        sender.sendMessage("§6═════════════════════════════");
        sender.sendMessage("§b§l" + playerName + "'s ELO Statistics");
        sender.sendMessage("§6═════════════════════════════");

        for (Map.Entry<String, Integer> entry : allElo.entrySet()) {
            String gamemodeName = entry.getKey();
            int elo = entry.getValue();
            String rank = EloCalculator.formatRank(elo);
            sender.sendMessage("§b" + gamemodeName + ": §f" + rank);
        }

        sender.sendMessage("§6═════════════════════════════");
    }
}
