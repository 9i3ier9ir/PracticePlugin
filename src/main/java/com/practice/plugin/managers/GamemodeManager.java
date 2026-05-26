package com.practice.plugin.managers;

import com.practice.plugin.models.Gamemode;
import com.practice.plugin.models.GameMap;
import com.practice.plugin.utils.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

/**
 * Manages all gamemodes and their configurations
 */
public class GamemodeManager {
    private Map<String, Gamemode> gamemodes;
    private ConfigManager configManager;

    public GamemodeManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.gamemodes = new HashMap<>();
        loadGamemodes();
    }

    private void loadGamemodes() {
        FileConfiguration config = configManager.getGamemodesConfig();
        if (config.contains("gamemodes")) {
            for (String gamemodeName : config.getConfigurationSection("gamemodes").getKeys(false)) {
                Gamemode gamemode = new Gamemode(gamemodeName);
                
                // Load rounds if set
                if (config.contains("gamemodes." + gamemodeName + ".rounds")) {
                    int rounds = config.getInt("gamemodes." + gamemodeName + ".rounds", 1);
                    gamemode.setRoundsNeeded(rounds);
                }
                
                gamemodes.put(gamemodeName, gamemode);
            }
        }
    }

    public boolean createGamemode(String gamemodeName) {
        if (gamemodes.containsKey(gamemodeName)) {
            return false;
        }
        
        Gamemode gamemode = new Gamemode(gamemodeName);
        gamemodes.put(gamemodeName, gamemode);
        
        FileConfiguration config = configManager.getGamemodesConfig();
        config.set("gamemodes." + gamemodeName + ".name", gamemodeName);
        config.set("gamemodes." + gamemodeName + ".maps", 0);
        configManager.saveGamemodesConfig();
        
        return true;
    }

    public Gamemode getGamemode(String gamemodeName) {
        return gamemodes.get(gamemodeName);
    }

    public boolean gamemodeExists(String gamemodeName) {
        return gamemodes.containsKey(gamemodeName);
    }

    public Collection<Gamemode> getAllGamemodes() {
        return gamemodes.values();
    }

    public void addMapToGamemode(String gamemodeName, GameMap map) {
        Gamemode gamemode = gamemodes.get(gamemodeName);
        if (gamemode != null) {
            gamemode.addMap(map);
        }
    }

    public GameMap getMap(String gamemodeName, int mapNumber) {
        Gamemode gamemode = gamemodes.get(gamemodeName);
        if (gamemode != null) {
            return gamemode.getMap(mapNumber);
        }
        return null;
    }

    public List<GameMap> getAvailableMaps(String gamemodeName) {
        Gamemode gamemode = gamemodes.get(gamemodeName);
        if (gamemode != null) {
            return gamemode.getAvailableMaps();
        }
        return new ArrayList<>();
    }

    public int getMapCount(String gamemodeName) {
        Gamemode gamemode = gamemodes.get(gamemodeName);
        if (gamemode != null) {
            return gamemode.getMapCount();
        }
        return 0;
    }
}
