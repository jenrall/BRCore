package com.example.brcore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * API که بقیه پلاگین‌ها (BRPlane, BRLoot, ...) ازش استفاده می‌کنن
 */
public class BRCoreAPI {

    private final BRCore plugin;

    public BRCoreAPI(BRCore plugin) {
        this.plugin = plugin;
    }

    public GameState getState() {
        return plugin.getState();
    }

    public Arena getCurrentArena() {
        return plugin.getCurrentArena();
    }

    public Set<UUID> getPlayers() {
        return plugin.getParticipants();
    }

    public Location getLobbySpawn() {
        Arena a = plugin.getCurrentArena();
        return a != null ? a.getLobbySpawn() : null;
    }

    public Location getGameSpawn() {
        Arena a = plugin.getCurrentArena();
        return a != null ? a.getGameSpawn() : null;
    }

    public void broadcast(String msg) {
        for (UUID id : plugin.getParticipants()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.sendMessage(msg);
        }
    }

    public boolean isRunning() {
        return plugin.getState() == GameState.IN_GAME
                || plugin.getState() == GameState.COUNTDOWN;
    }
}
