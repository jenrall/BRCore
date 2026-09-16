package com.example.brcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class BRCore extends JavaPlugin {

    private static BRCore instance;
    private static BRCoreAPI api;

    private ArenaManager arenaManager;
    private GameState state = GameState.IDLE;
    private Arena currentArena;
    private final Set<UUID> participants = new HashSet<>();
    private BukkitTask countdownTask;

    @Override
    public void onEnable() {
        instance = this;
        api = new BRCoreAPI(this);

        saveDefaultConfig();
        this.arenaManager = new ArenaManager(this);

        getCommand("br").setExecutor(new BRCommand(this));
        getServer().getPluginManager().registerEvents(new BRListener(this), this);

        getLogger().info("BRCore enabled! (state=" + state + ")");
    }

    @Override
    public void onDisable() {
        arenaManager.save();
        if (countdownTask != null) countdownTask.cancel();
        getLogger().info("BRCore disabled!");
    }

    // ============ API access ============
    public static BRCore getInstance() { return instance; }
    public static BRCoreAPI getAPI() { return api; }

    // ============ Getters ============
    public ArenaManager getArenaManager() { return arenaManager; }
    public GameState getState() { return state; }
    public Arena getCurrentArena() { return currentArena; }
    public Set<UUID> getParticipants() { return participants; }

    // ============ State ============
    public void setState(GameState s) {
        this.state = s;
        getLogger().info("State changed to " + s);
    }

    // ============ Lobby ============
    public boolean joinLobby(Player p, String arenaName) {
        Arena a = arenaManager.getArena(arenaName);
        if (a == null) {
            p.sendMessage(Component.text("آرنا پیدا نشد.", NamedTextColor.RED));
            return false;
        }
        if (a.getLobbySpawn() == null) {
            p.sendMessage(Component.text("لابی تنظیم نشده.", NamedTextColor.RED));
            return false;
        }
        if (state != GameState.IDLE && state != GameState.LOBBY) {
            p.sendMessage(Component.text("بازی در حال اجراست.", NamedTextColor.RED));
            return false;
        }
        if (participants.contains(p.getUniqueId())) {
            p.sendMessage(Component.text("شما قبلاً وارد شدید.", NamedTextColor.YELLOW));
            return false;
        }

        // اگه آرنا عوض شده، پاک کن
        if (currentArena != null && !currentArena.equals(a)) {
            participants.clear();
        }

        currentArena = a;
        state = GameState.LOBBY;
        participants.add(p.getUniqueId());
        p.teleport(a.getLobbySpawn());

        Bukkit.broadcast(Component.text("🎮 " + p.getName() + " وارد لابی شد! (" + participants.size() + ")", NamedTextColor.GREEN));
        return true;
    }

    public void leave(Player p) {
        participants.remove(p.getUniqueId());
        p.sendMessage(Component.text("از لابی خارج شدی.", NamedTextColor.YELLOW));

        if (participants.isEmpty()) {
            state = GameState.IDLE;
            currentArena = null;
        }
    }

    // ============ Start ============
    public boolean startGame() {
        if (currentArena == null) return false;
        if (state != GameState.LOBBY) return false;

        int min = getConfig().getInt("min-players", 2);
        if (participants.size() < min) {
            Bukkit.broadcast(Component.text("حداقل " + min + " بازیکن لازمه!", NamedTextColor.RED));
            return false;
        }

        state = GameState.COUNTDOWN;
        int countdown = getConfig().getInt("countdown-seconds", 10);

        countdownTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            int remaining = countdown;

            @Override
            public void run() {
                if (remaining <= 0) {
                    countdownTask.cancel();
                    actuallyStart();
                    return;
                }
                if (remaining <= 5 || remaining % 5 == 0) {
                    Bukkit.broadcast(Component.text("شروع بازی در " + remaining + " ثانیه...", NamedTextColor.GOLD));
                }
                remaining--;
            }
        }, 0L, 20L);

        return true;
    }

    private void actuallyStart() {
        state = GameState.IN_GAME;

        // همه بازیکنا به game spawn
        Location spawn = currentArena.getGameSpawn();
        if (spawn != null) {
            for (UUID id : participants) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) p.teleport(spawn);
            }
        }

        Bukkit.broadcast(Component.text("⚔ بازی شروع شد!", NamedTextColor.GOLD));
    }

    public void stopGame() {
        state = GameState.IDLE;
        if (countdownTask != null) { countdownTask.cancel(); countdownTask = null; }

        for (UUID id : new HashSet<>(participants)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.sendMessage(Component.text("بازی متوقف شد.", NamedTextColor.YELLOW));
            }
        }
        participants.clear();
        currentArena = null;

        Bukkit.broadcast(Component.text("🛑 بازی پایان یافت.", NamedTextColor.RED));
    }
}
