package com.practice.plugin.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

/**
 * Represents player statistics for a specific gamemode
 */
public class PlayerStats {
    private String playerName;
    private String gamemodeName;
    private int elo;
    private int wins;
    private int losses;
    private ItemStack[] kit;
    private long queueJoinTime;

    public PlayerStats(String playerName, String gamemodeName, int startingElo) {
        this.playerName = playerName;
        this.gamemodeName = gamemodeName;
        this.elo = startingElo;
        this.wins = 0;
        this.losses = 0;
        this.kit = new ItemStack[36]; // Inventory size
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getGamemodeName() {
        return gamemodeName;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int newElo) {
        this.elo = Math.max(0, newElo);
    }

    public void addElo(int amount) {
        this.elo = Math.max(0, elo + amount);
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public double getWinRate() {
        int total = wins + losses;
        if (total == 0) return 0;
        return (double) wins / total * 100;
    }

    public ItemStack[] getKit() {
        return kit;
    }

    public void setKit(ItemStack[] newKit) {
        this.kit = Arrays.copyOf(newKit, newKit.length);
    }

    public long getQueueJoinTime() {
        return queueJoinTime;
    }

    public void setQueueJoinTime(long time) {
        this.queueJoinTime = time;
    }

    public long getQueueWaitTime() {
        return System.currentTimeMillis() - queueJoinTime;
    }
}
