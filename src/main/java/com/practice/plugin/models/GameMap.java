package com.practice.plugin.models;

import org.bukkit.Location;
import java.util.*;

/**
 * Represents a map within a gamemode
 */
public class GameMap {
    private String gamemodeName;
    private int mapNumber;
    private Location firstSpawn;
    private Location secondSpawn;
    private Location minCorner;
    private Location maxCorner;

    public GameMap(String gamemodeName, int mapNumber) {
        this.gamemodeName = gamemodeName;
        this.mapNumber = mapNumber;
    }

    public String getGamemodeName() {
        return gamemodeName;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public Location getFirstSpawn() {
        return firstSpawn;
    }

    public void setFirstSpawn(Location location) {
        this.firstSpawn = location;
    }

    public Location getSecondSpawn() {
        return secondSpawn;
    }

    public void setSecondSpawn(Location location) {
        this.secondSpawn = location;
    }

    public Location getMinCorner() {
        return minCorner;
    }

    public void setMinCorner(Location location) {
        this.minCorner = location;
    }

    public Location getMaxCorner() {
        return maxCorner;
    }

    public void setMaxCorner(Location location) {
        this.maxCorner = location;
    }

    public boolean isFullyConfigured() {
        return firstSpawn != null && secondSpawn != null && minCorner != null && maxCorner != null;
    }
}
