package com.practice.plugin.managers;

import com.practice.plugin.models.GameMap;
import com.practice.plugin.utils.ConfigManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

/**
 * Manages map configurations and storage
 */
public class MapManager {
    private ConfigManager configManager;
    private GamemodeManager gamemodeManager;

    public MapManager(ConfigManager configManager, GamemodeManager gamemodeManager) {
        this.configManager = configManager;
        this.gamemodeManager = gamemodeManager;
        loadAllMaps();
    }

    private void loadAllMaps() {
        FileConfiguration config = configManager.getMapsConfig();
        if (config.contains("maps")) {
            for (String gamemodeName : config.getConfigurationSection("maps").getKeys(false)) {
                for (String mapNumStr : config.getConfigurationSection("maps." + gamemodeName).getKeys(false)) {
                    try {
                        int mapNumber = Integer.parseInt(mapNumStr);
                        GameMap map = new GameMap(gamemodeName, mapNumber);
                        
                        // Load spawns if they exist
                        String firstPath = "maps." + gamemodeName + "." + mapNumber + ".firstSpawn";
                        if (config.contains(firstPath)) {
                            map.setFirstSpawn(config.getLocation(firstPath));
                        }
                        
                        String secondPath = "maps." + gamemodeName + "." + mapNumber + ".secondSpawn";
                        if (config.contains(secondPath)) {
                            map.setSecondSpawn(config.getLocation(secondPath));
                        }
                        
                        String minPath = "maps." + gamemodeName + "." + mapNumber + ".minCorner";
                        if (config.contains(minPath)) {
                            map.setMinCorner(config.getLocation(minPath));
                        }
                        
                        String maxPath = "maps." + gamemodeName + "." + mapNumber + ".maxCorner";
                        if (config.contains(maxPath)) {
                            map.setMaxCorner(config.getLocation(maxPath));
                        }
                        
                        gamemodeManager.addMapToGamemode(gamemodeName, map);
                    } catch (NumberFormatException e) {
                        // Skip invalid map numbers
                    }
                }
            }
        }
    }

    public void saveMapRegion(String gamemodeName, int mapNumber, Location min, Location max) {
        GameMap map = gamemodeManager.getMap(gamemodeName, mapNumber);
        if (map == null) {
            map = new GameMap(gamemodeName, mapNumber);
            gamemodeManager.addMapToGamemode(gamemodeName, map);
        }
        
        map.setMinCorner(min);
        map.setMaxCorner(max);
        
        FileConfiguration config = configManager.getMapsConfig();
        String path = "maps." + gamemodeName + "." + mapNumber;
        config.set(path + ".minCorner", min);
        config.set(path + ".maxCorner", max);
        configManager.saveMapsConfig();
    }

    public void saveFirstSpawn(String gamemodeName, int mapNumber, Location location) {
        GameMap map = gamemodeManager.getMap(gamemodeName, mapNumber);
        if (map == null) {
            map = new GameMap(gamemodeName, mapNumber);
            gamemodeManager.addMapToGamemode(gamemodeName, map);
        }
        
        map.setFirstSpawn(location);
        
        FileConfiguration config = configManager.getMapsConfig();
        config.set("maps." + gamemodeName + "." + mapNumber + ".firstSpawn", location);
        configManager.saveMapsConfig();
    }

    public void saveSecondSpawn(String gamemodeName, int mapNumber, Location location) {
        GameMap map = gamemodeManager.getMap(gamemodeName, mapNumber);
        if (map == null) {
            map = new GameMap(gamemodeName, mapNumber);
            gamemodeManager.addMapToGamemode(gamemodeName, map);
        }
        
        map.setSecondSpawn(location);
        
        FileConfiguration config = configManager.getMapsConfig();
        config.set("maps." + gamemodeName + "." + mapNumber + ".secondSpawn", location);
        configManager.saveMapsConfig();
    }
}
