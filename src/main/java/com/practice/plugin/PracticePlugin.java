package com.practice.plugin;

import com.practice.plugin.commands.*;
import com.practice.plugin.listeners.*;
import com.practice.plugin.managers.*;
import com.practice.plugin.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for PracticePlugin
 */
public class PracticePlugin extends JavaPlugin {
    
    private ConfigManager configManager;
    private GamemodeManager gamemodeManager;
    private MapManager mapManager;
    private EloManager eloManager;
    private QueueManager queueManager;

    @Override
    public void onEnable() {
        getLogger().info("§a[PracticePlugin] Loading...");

        // Create config file if it doesn't exist
        saveDefaultConfig();

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.gamemodeManager = new GamemodeManager(configManager);
        this.mapManager = new MapManager(configManager, gamemodeManager);
        this.eloManager = new EloManager(configManager);
        this.queueManager = new QueueManager(this, gamemodeManager, eloManager);

        // Register commands
        registerCommands();

        // Register event listeners
        registerListeners();

        getLogger().info("§a[PracticePlugin] Successfully loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("§c[PracticePlugin] Disabling...");

        // Stop matchmaking task
        if (queueManager != null) {
            queueManager.stopMatchmakingTask();
        }

        getLogger().info("§c[PracticePlugin] Successfully disabled!");
    }

    private void registerCommands() {
        // Queue commands
        getCommand("queue").setExecutor(new QueueCommand(queueManager));

        // Gamemode commands
        getCommand("gamemode").setExecutor(new GamemodeCommand(gamemodeManager));

        // Rounds command
        getCommand("setrounds").setExecutor(new SetRoundsCommand(gamemodeManager, configManager));

        // Map commands
        MapCommand mapCommand = new MapCommand(mapManager, gamemodeManager);
        getCommand("setregion").setExecutor(mapCommand);
        getCommand("setfirstspawn").setExecutor(mapCommand);
        getCommand("setsecondspawn").setExecutor(mapCommand);

        // Kit command
        getCommand("invset").setExecutor(new InvsetCommand(configManager, gamemodeManager));

        // ELO view command
        getCommand("eloview").setExecutor(new EloviewCommand(eloManager));

        // Duel command
        getCommand("duel").setExecutor(new DuelCommand(queueManager, gamemodeManager));

        getLogger().info("§aCommands registered!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(queueManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(queueManager), this);

        getLogger().info("§aEvent listeners registered!");
    }
}
