package com.example.brcore;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private final BRCore plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(BRCore plugin) {
        this.plugin = plugin;
        load();
    }

    public Arena getArena(String name) { return arenas.get(name.toLowerCase()); }
    public Map<String, Arena> getArenas() { return arenas; }

    public boolean create(String name) {
        if (arenas.containsKey(name.toLowerCase())) return false;
        arenas.put(name.toLowerCase(), new Arena(name));
        return true;
    }

    public boolean delete(String name) {
        if (!arenas.containsKey(name.toLowerCase())) return false;
        arenas.remove(name.toLowerCase());
        plugin.getConfig().set("arenas." + name.toLowerCase(), null);
        plugin.saveConfig();
        return true;
    }

    public void save() {
        FileConfiguration cfg = plugin.getConfig();
        for (Arena a : arenas.values()) {
            String path = "arenas." + a.getName().toLowerCase();
            cfg.set(path + ".lobby-spawn", a.getLobbySpawn());
            cfg.set(path + ".game-spawn", a.getGameSpawn());
        }
        plugin.saveConfig();
    }

    private void load() {
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection sec = cfg.getConfigurationSection("arenas");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            String path = "arenas." + key;
            Arena a = new Arena(key);
            Location lobby = cfg.getLocation(path + ".lobby-spawn");
            Location game = cfg.getLocation(path + ".game-spawn");
            a.setLobbySpawn(lobby);
            a.setGameSpawn(game);
            arenas.put(key, a);
        }
    }
}
