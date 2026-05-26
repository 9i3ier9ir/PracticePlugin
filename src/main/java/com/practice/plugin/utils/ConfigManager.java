package com.practice.plugin.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

/**
 * Manages all configuration and data files
 */
public class ConfigManager {
    private JavaPlugin plugin;
    private File dataFolder;
    private FileConfiguration eloConfig;
    private FileConfiguration gamemodesConfig;
    private FileConfiguration kitsConfig;
    private FileConfiguration mapsConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        initializeConfigs();
    }

    private void initializeConfigs() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        createConfigIfNotExists("elo.yml");
        createConfigIfNotExists("gamemodes.yml");
        createConfigIfNotExists("kits.yml");
        createConfigIfNotExists("maps.yml");

        loadAllConfigs();
    }

    private void createConfigIfNotExists(String filename) {
        File file = new File(dataFolder, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create " + filename);
                e.printStackTrace();
            }
        }
    }

    public void loadAllConfigs() {
        eloConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "elo.yml"));
        gamemodesConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "gamemodes.yml"));
        kitsConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "kits.yml"));
        mapsConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "maps.yml"));
    }

    public void saveEloConfig() {
        try {
            eloConfig.save(new File(dataFolder, "elo.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save elo.yml");
            e.printStackTrace();
        }
    }

    public void saveGamemodesConfig() {
        try {
            gamemodesConfig.save(new File(dataFolder, "gamemodes.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save gamemodes.yml");
            e.printStackTrace();
        }
    }

    public void saveKitsConfig() {
        try {
            kitsConfig.save(new File(dataFolder, "kits.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save kits.yml");
            e.printStackTrace();
        }
    }

    public void saveMapsConfig() {
        try {
            mapsConfig.save(new File(dataFolder, "maps.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save maps.yml");
            e.printStackTrace();
        }
    }

    public FileConfiguration getEloConfig() {
        return eloConfig;
    }

    public FileConfiguration getGamemodesConfig() {
        return gamemodesConfig;
    }

    public FileConfiguration getKitsConfig() {
        return kitsConfig;
    }

    public FileConfiguration getMapsConfig() {
        return mapsConfig;
    }
}
