package com.practice.plugin.models;

import java.util.*;

/**
 * Represents a gamemode with its own ELO rankings and maps
 */
public class Gamemode {
    private String name;
    private Map<Integer, GameMap> maps;
    private int nextMapNumber;
    private int roundsNeeded;

    public Gamemode(String name) {
        this.name = name;
        this.maps = new HashMap<>();
        this.nextMapNumber = 1;
        this.roundsNeeded = 1; // Default: first to 1
    }

    public String getName() {
        return name;
    }

    public void addMap(GameMap map) {
        maps.put(map.getMapNumber(), map);
        nextMapNumber = Math.max(nextMapNumber, map.getMapNumber() + 1);
    }

    public GameMap getMap(int mapNumber) {
        return maps.get(mapNumber);
    }

    public Collection<GameMap> getAllMaps() {
        return maps.values();
    }

    public List<GameMap> getAvailableMaps() {
        List<GameMap> available = new ArrayList<>();
        for (GameMap map : maps.values()) {
            if (map.isFullyConfigured()) {
                available.add(map);
            }
        }
        return available;
    }

    public int getMapCount() {
        return maps.size();
    }

    public int getNextMapNumber() {
        return nextMapNumber;
    }

    public int getRoundsNeeded() {
        return roundsNeeded;
    }

    public void setRoundsNeeded(int rounds) {
        this.roundsNeeded = Math.max(1, rounds);
    }
}
