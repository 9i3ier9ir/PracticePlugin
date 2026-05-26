package com.practice.plugin.commands;

import com.practice.plugin.managers.QueueManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles /queue, /q, /qu commands
 */
public class QueueCommand implements CommandExecutor {
    private QueueManager queueManager;

    public QueueCommand(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§cUsage: /" + label + " <gamemode>");
            return true;
        }

        String gamemodeName = args[0].toLowerCase();
        queueManager.joinQueue(player, gamemodeName);
        return true;
    }
}
