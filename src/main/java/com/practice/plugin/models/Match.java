package com.practice.plugin.models;

import org.bukkit.entity.Player;

/**
 * Represents an active match between two players
 */
public class Match {
    private Player player1;
    private Player player2;
    private String gamemodeName;
    private GameMap map;
    private long startTime;

    public Match(Player player1, Player player2, String gamemodeName, GameMap map) {
        this.player1 = player1;
        this.player2 = player2;
        this.gamemodeName = gamemodeName;
        this.map = map;
        this.startTime = System.currentTimeMillis();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public String getGamemodeName() {
        return gamemodeName;
    }

    public GameMap getMap() {
        return map;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean containsPlayer(Player player) {
        return player1.equals(player) || player2.equals(player);
    }

    public Player getOpponent(Player player) {
        if (player1.equals(player)) {
            return player2;
        } else if (player2.equals(player)) {
            return player1;
        }
        return null;
    }
}
