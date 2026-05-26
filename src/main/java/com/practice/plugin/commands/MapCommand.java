package com.practice.plugin.commands;

import com.practice.plugin.managers.MapManager;
import com.practice.plugin.managers.GamemodeManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles /setregion, /setfirstspawn, /setsecondspawn commands
 * Supports WorldEdit if available, but works without it
 */
public class MapCommand implements CommandExecutor {
    private MapManager mapManager;
    private GamemodeManager gamemodeManager;

    public MapCommand(MapManager mapManager, GamemodeManager gamemodeManager) {
        this.mapManager = mapManager;
        this.gamemodeManager = gamemodeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (!sender.hasPermission("practiceplugin.map.set")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("setregion")) {
            return handleSetRegion(player, args);
        } else if (label.equalsIgnoreCase("setfirstspawn")) {
            return handleSetFirstSpawn(player, args);
        } else if (label.equalsIgnoreCase("setsecondspawn")) {
            return handleSetSecondSpawn(player, args);
        }

        return true;
    }

    private boolean handleSetRegion(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /setregion <gamemode> <map_number>");
            player.sendMessage("§eFirst, make a selection with WorldEdit (//wand or //sel)");
            return true;
        }

        String gamemodeName = args[0].toLowerCase();
        String mapNumStr = args[1];

        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            player.sendMessage("§cGamemode '" + gamemodeName + "' does not exist!");
            return true;
        }

        try {
            int mapNumber = Integer.parseInt(mapNumStr);
            
            // Try to get WorldEdit selection
            Location[] selection = getWorldEditSelection(player);
            if (selection != null && selection.length == 2) {
                mapManager.saveMapRegion(gamemodeName, mapNumber, selection[0], selection[1]);
                player.sendMessage("§a✓ Map region set for " + gamemodeName + " map " + mapNumber);
                player.sendMessage("§eCorner 1: (" + selection[0].getBlockX() + ", " + selection[0].getBlockY() + ", " + selection[0].getBlockZ() + ")");
                player.sendMessage("§eCorner 2: (" + selection[1].getBlockX() + ", " + selection[1].getBlockY() + ", " + selection[1].getBlockZ() + ")");
                return true;
            }
            
            // WorldEdit not available or no selection
            player.sendMessage("§eWorldEdit selection not found. Options:");
            player.sendMessage("§e1. Install WorldEdit plugin");
            player.sendMessage("§e2. Use §f//wand§e to get the WorldEdit wand");
            player.sendMessage("§e3. Left-click a corner (§fshift + left-click§e)");
            player.sendMessage("§e4. Right-click the opposite corner (§fshift + right-click§e)");
            player.sendMessage("§e5. Run §f/setregion " + gamemodeName + " " + mapNumber);
            
        } catch (NumberFormatException e) {
            player.sendMessage("§cMap number must be a number!");
        }

        return true;
    }

    /**
     * Safely get WorldEdit selection using reflection
     * Returns null if WorldEdit is not available or player has no selection
     */
    private Location[] getWorldEditSelection(Player player) {
        try {
            // Reflect to get the WorldEdit instance
            Class<?> worldEditClazz = Class.forName("com.sk89q.worldedit.WorldEdit");
            Object we = worldEditClazz.getMethod("getInstance").invoke(null);

            // Get the session manager
            Object sessionManager = worldEditClazz.getMethod("getSessionManager").invoke(we);

            // Get the player's session
            Object session = sessionManager.getClass()
                    .getMethod("getIfPresent", Object.class)
                    .invoke(sessionManager, player.getUniqueId());

            if (session == null) {
                return null;
            }

            // Get the selection
            Object selection = session.getClass()
                    .getMethod("getSelection")
                    .invoke(session);

            if (selection == null) {
                return null;
            }

            // Get min and max points
            Object minPoint = selection.getClass().getMethod("getMinimumPoint").invoke(selection);
            Object maxPoint = selection.getClass().getMethod("getMaximumPoint").invoke(selection);

            // Extract coordinates using reflection
            int minX = (Integer) minPoint.getClass().getMethod("getX").invoke(minPoint);
            int minY = (Integer) minPoint.getClass().getMethod("getY").invoke(minPoint);
            int minZ = (Integer) minPoint.getClass().getMethod("getZ").invoke(minPoint);

            int maxX = (Integer) maxPoint.getClass().getMethod("getX").invoke(maxPoint);
            int maxY = (Integer) maxPoint.getClass().getMethod("getY").invoke(maxPoint);
            int maxZ = (Integer) maxPoint.getClass().getMethod("getZ").invoke(maxPoint);

            Location loc1 = new Location(player.getWorld(), minX, minY, minZ);
            Location loc2 = new Location(player.getWorld(), maxX, maxY, maxZ);

            return new Location[]{loc1, loc2};

        } catch (ClassNotFoundException e) {
            // WorldEdit not installed
            return null;
        } catch (Exception e) {
            // Error accessing WorldEdit or no selection
            return null;
        }
    }

    private boolean handleSetFirstSpawn(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§cUsage: /setfirstspawn <gamemode> [map_number]");
            return true;
        }

        String gamemodeName = args[0].toLowerCase();

        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            player.sendMessage("§cGamemode '" + gamemodeName + "' does not exist!");
            return true;
        }

        int mapNumber;
        if (args.length >= 2) {
            try {
                mapNumber = Integer.parseInt(args[1]);
                if (mapNumber <= 0) {
                    player.sendMessage("§cMap number must be a positive integer!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cMap number must be a number!");
                return true;
            }
        } else {
            mapNumber = gamemodeManager.getGamemode(gamemodeName).getNextMapNumber();
        }

        mapManager.saveFirstSpawn(gamemodeName, mapNumber, player.getLocation());
        player.sendMessage("§a✓ First spawn set for " + gamemodeName + " map " + mapNumber);
        player.sendMessage("§eLocation: (" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ")");

        return true;
    }

    private boolean handleSetSecondSpawn(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§cUsage: /setsecondspawn <gamemode> [map_number]");
            return true;
        }

        String gamemodeName = args[0].toLowerCase();

        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            player.sendMessage("§cGamemode '" + gamemodeName + "' does not exist!");
            return true;
        }

        int mapNumber;
        if (args.length >= 2) {
            try {
                mapNumber = Integer.parseInt(args[1]);
                if (mapNumber <= 0) {
                    player.sendMessage("§cMap number must be a positive integer!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cMap number must be a number!");
                return true;
            }
        } else {
            mapNumber = gamemodeManager.getGamemode(gamemodeName).getNextMapNumber();
        }

        mapManager.saveSecondSpawn(gamemodeName, mapNumber, player.getLocation());
        player.sendMessage("§a✓ Second spawn set for " + gamemodeName + " map " + mapNumber);
        player.sendMessage("§eLocation: (" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ")");

        return true;
    }
}
