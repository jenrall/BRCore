package com.example.brcore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class BRListener implements Listener {

    private final BRCore plugin;

    public BRListener(BRCore plugin) { this.plugin = plugin; }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.leave(e.getPlayer());
    }
}
