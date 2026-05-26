package com.practice.plugin.models;

import org.bukkit.entity.Player;

/**
 * Represents a duel between two players (does not affect ELO)
 */
public class Duel {
    private Player player1;
    private Player player2;
    private String gamemodeName;
    private GameMap map;
    private long startTime;
    private int roundsNeeded;
    private int player1Wins;
    private int player2Wins;

    public Duel(Player player1, Player player2, String gamemodeName, GameMap map, int roundsNeeded) {
        this.player1 = player1;
        this.player2 = player2;
        this.gamemodeName = gamemodeName;
        this.map = map;
        this.roundsNeeded = roundsNeeded;
        this.startTime = System.currentTimeMillis();
        this.player1Wins = 0;
        this.player2Wins = 0;
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

    public int getRoundsNeeded() {
        return roundsNeeded;
    }

    public int getPlayer1Wins() {
        return player1Wins;
    }

    public int getPlayer2Wins() {
        return player2Wins;
    }

    public void player1WinsRound() {
        this.player1Wins++;
    }

    public void player2WinsRound() {
        this.player2Wins++;
    }

    public boolean isDuelOver() {
        return player1Wins >= roundsNeeded || player2Wins >= roundsNeeded;
    }

    public Player getWinner() {
        if (player1Wins >= roundsNeeded) return player1;
        if (player2Wins >= roundsNeeded) return player2;
        return null;
    }

    public Player getLoser() {
        if (player1Wins >= roundsNeeded) return player2;
        if (player2Wins >= roundsNeeded) return player1;
        return null;
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
