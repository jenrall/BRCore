package com.example.brcore;

import org.bukkit.Location;

public class Arena {

    private final String name;
    private Location lobbySpawn;
    private Location gameSpawn;

    public Arena(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Location getLobbySpawn() { return lobbySpawn; }
    public Location getGameSpawn() { return gameSpawn; }

    public void setLobbySpawn(Location l) { this.lobbySpawn = l; }
    public void setGameSpawn(Location l) { this.gameSpawn = l; }
}
