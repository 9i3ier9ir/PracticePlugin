package com.practice.plugin.managers;

import com.practice.plugin.models.PlayerStats;
import com.practice.plugin.utils.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * Manages all player ELO ratings across all gamemodes
 */
public class EloManager {
    private ConfigManager configManager;
    private Map<String, Map<String, PlayerStats>> playerStats; // gamemode -> (playerName -> PlayerStats)
    private static final int STARTING_ELO = 100;

    public EloManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.playerStats = new HashMap<>();
        loadAllEloData();
    }

    private void loadAllEloData() {
        FileConfiguration config = configManager.getEloConfig();
        if (config.contains("players")) {
            for (String playerName : config.getConfigurationSection("players").getKeys(false)) {
                for (String gamemodeName : config.getConfigurationSection("players." + playerName).getKeys(false)) {
                    int elo = config.getInt("players." + playerName + "." + gamemodeName, STARTING_ELO);
                    PlayerStats stats = new PlayerStats(playerName, gamemodeName, elo);
                    addPlayerStats(gamemodeName, stats);
                }
            }
        }
    }

    private void addPlayerStats(String gamemodeName, PlayerStats stats) {
        playerStats.computeIfAbsent(gamemodeName, k -> new HashMap<>())
                .put(stats.getPlayerName(), stats);
    }

    public PlayerStats getOrCreatePlayerStats(String playerName, String gamemodeName) {
        Map<String, PlayerStats> gamemodeStats = playerStats.computeIfAbsent(gamemodeName, k -> new HashMap<>());
        
        if (!gamemodeStats.containsKey(playerName)) {
            PlayerStats stats = new PlayerStats(playerName, gamemodeName, STARTING_ELO);
            gamemodeStats.put(playerName, stats);
            savePlayerElo(playerName, gamemodeName, STARTING_ELO);
        }
        
        return gamemodeStats.get(playerName);
    }

    public void updatePlayerElo(String playerName, String gamemodeName, int newElo) {
        PlayerStats stats = getOrCreatePlayerStats(playerName, gamemodeName);
        stats.setElo(newElo);
        savePlayerElo(playerName, gamemodeName, newElo);
    }

    public void addPlayerElo(String playerName, String gamemodeName, int eloGain) {
        PlayerStats stats = getOrCreatePlayerStats(playerName, gamemodeName);
        int newElo = Math.max(0, stats.getElo() + eloGain);
        stats.addElo(eloGain);
        savePlayerElo(playerName, gamemodeName, newElo);
    }

    private void savePlayerElo(String playerName, String gamemodeName, int elo) {
        FileConfiguration config = configManager.getEloConfig();
        config.set("players." + playerName + "." + gamemodeName, elo);
        configManager.saveEloConfig();
    }

    public int getPlayerElo(String playerName, String gamemodeName) {
        return getOrCreatePlayerStats(playerName, gamemodeName).getElo();
    }

    public List<PlayerStats> getGamemodeLeaderboard(String gamemodeName) {
        Map<String, PlayerStats> gamemodeStats = playerStats.getOrDefault(gamemodeName, new HashMap<>());
        List<PlayerStats> leaderboard = new ArrayList<>(gamemodeStats.values());
        leaderboard.sort((a, b) -> Integer.compare(b.getElo(), a.getElo()));
        return leaderboard;
    }

    public Map<String, Integer> getPlayerAllGamemodeElo(String playerName) {
        Map<String, Integer> allElo = new HashMap<>();
        for (Map.Entry<String, Map<String, PlayerStats>> entry : playerStats.entrySet()) {
            String gamemodeName = entry.getKey();
            Map<String, PlayerStats> gamemodeStats = entry.getValue();
            if (gamemodeStats.containsKey(playerName)) {
                allElo.put(gamemodeName, gamemodeStats.get(playerName).getElo());
            }
        }
        return allElo;
    }

    public void recordWin(String playerName, String gamemodeName) {
        PlayerStats stats = getOrCreatePlayerStats(playerName, gamemodeName);
        stats.incrementWins();
    }

    public void recordLoss(String playerName, String gamemodeName) {
        PlayerStats stats = getOrCreatePlayerStats(playerName, gamemodeName);
        stats.incrementLosses();
    }
}
