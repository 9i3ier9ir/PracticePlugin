package com.practice.plugin.listeners;

import com.practice.plugin.managers.QueueManager;
import com.practice.plugin.models.Duel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Handles player death events to end matches and duel rounds
 */
public class PlayerDeathListener implements Listener {
    private QueueManager queueManager;

    public PlayerDeathListener(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        Player killer = deadPlayer.getKiller();

        if (killer == null) {
            return;
        }

        // Check if players are in an active duel
        Duel duel = queueManager.getActiveDuel(deadPlayer);
        if (duel != null && queueManager.isInDuel(killer)) {
            queueManager.endDuelRound(killer, deadPlayer);
            return;
        }

        // Check if players are in an active match
        if (queueManager.isInMatch(deadPlayer) && queueManager.isInMatch(killer)) {
            queueManager.endMatch(killer, deadPlayer);
        }
    }
}

