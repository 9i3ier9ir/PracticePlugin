package com.practice.plugin.listeners;

import com.practice.plugin.managers.QueueManager;
import com.practice.plugin.models.Duel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player quit events to remove from queue and forfeit duels/matches
 */
public class PlayerQuitListener implements Listener {
    private QueueManager queueManager;

    public PlayerQuitListener(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remove from duel (forfeit)
        if (queueManager.isInDuel(player)) {
            Duel duel = queueManager.getActiveDuel(player);
            if (duel != null) {
                Player opponent = duel.getOpponent(player);
                if (opponent != null) {
                    opponent.sendMessage("§8" + player.getName() + " disconnected. Duel ended.");
                }
            }
        }

        // Remove from queue if they're in one
        if (queueManager.isInQueue(player)) {
            queueManager.leaveQueue(player);
        }
    }
}
