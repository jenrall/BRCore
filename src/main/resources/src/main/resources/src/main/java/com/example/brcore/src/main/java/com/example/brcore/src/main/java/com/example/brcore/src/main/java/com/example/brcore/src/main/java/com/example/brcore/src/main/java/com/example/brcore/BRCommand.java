package com.example.brcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BRCommand implements CommandExecutor {

    private final BRCore plugin;

    public BRCommand(BRCore plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players.");
            return true;
        }

        if (args.length == 0) { help(p); return true; }

        switch (args[0].toLowerCase()) {
            case "help" -> help(p);
            case "create" -> create(p, args);
            case "delete" -> delete(p, args);
            case "list" -> list(p);
            case "setlobby" -> setLobby(p, args);
            case "setspawn" -> setSpawn(p, args);
            case "join" -> {
                if (args.length < 2) { p.sendMessage(Component.text("Usage: /br join <arena>", NamedTextColor.RED)); return true; }
                plugin.joinLobby(p, args[1]);
            }
            case "leave" -> plugin.leave(p);
            case "start" -> {
                if (!plugin.startGame()) {
                    p.sendMessage(Component.text("شروع نشد.", NamedTextColor.RED));
                }
            }
            case "stop" -> plugin.stopGame();
            default -> p.sendMessage(Component.text("Unknown. /br help", NamedTextColor.RED));
        }
        return true;
    }

    private void help(Player p) {
        p.sendMessage(Component.text("=== BRCore ===", NamedTextColor.GOLD));
        p.sendMessage(Component.text("/br create <name>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br delete <name>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br list", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br setlobby <name>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br setspawn <name>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br join <name>", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br leave", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br start", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/br stop", NamedTextColor.YELLOW));
    }

    private void create(Player p, String[] args) {
        if (!p.hasPermission("br.admin")) { noPerm(p); return; }
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /br create <name>", NamedTextColor.RED)); return; }
        if (plugin.getArenaManager().create(args[1])) {
            p.sendMessage(Component.text("آرنا ساخته شد. حالا setlobby و setspawn", NamedTextColor.GREEN));
        } else {
            p.sendMessage(Component.text("آرنا وجود داره.", NamedTextColor.RED));
        }
    }

    private void delete(Player p, String[] args) {
        if (!p.hasPermission("br.admin")) { noPerm(p); return; }
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /br delete <name>", NamedTextColor.RED)); return; }
        if (plugin.getArenaManager().delete(args[1])) {
            p.sendMessage(Component.text("حذف شد.", NamedTextColor.GREEN));
        } else {
            p.sendMessage(Component.text("پیدا نشد.", NamedTextColor.RED));
        }
    }

    private void list(Player p) {
        if (plugin.getArenaManager().getArenas().isEmpty()) {
            p.sendMessage(Component.text("هیچ آرنایی نیست.", NamedTextColor.RED));
            return;
        }
        p.sendMessage(Component.text("=== Arenas ===", NamedTextColor.GOLD));
        plugin.getArenaManager().getArenas().forEach((name, a) -> {
            String lobby = a.getLobbySpawn() != null ? "✅" : "❌";
            String spawn = a.getGameSpawn() != null ? "✅" : "❌";
            p.sendMessage(Component.text("- " + name + " | lobby: " + lobby + " | spawn: " + spawn, NamedTextColor.YELLOW));
        });
    }

    private void setLobby(Player p, String[] args) {
        if (!p.hasPermission("br.admin")) { noPerm(p); return; }
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /br setlobby <name>", NamedTextColor.RED)); return; }
        Arena a = plugin.getArenaManager().getArena(args[1]);
        if (a == null) { p.sendMessage(Component.text("پیدا نشد.", NamedTextColor.RED)); return; }
        a.setLobbySpawn(p.getLocation());
        plugin.getArenaManager().save();
        p.sendMessage(Component.text("لابی تنظیم شد.", NamedTextColor.GREEN));
    }

    private void setSpawn(Player p, String[] args) {
        if (!p.hasPermission("br.admin")) { noPerm(p); return; }
        if (args.length < 2) { p.sendMessage(Component.text("Usage: /br setspawn <name>", NamedTextColor.RED)); return; }
        Arena a = plugin.getArenaManager().getArena(args[1]);
        if (a == null) { p.sendMessage(Component.text("پیدا نشد.", NamedTextColor.RED)); return; }
        a.setGameSpawn(p.getLocation());
        plugin.getArenaManager().save();
        p.sendMessage(Component.text("نقطه شروع تنظیم شد.", NamedTextColor.GREEN));
    }

    private void noPerm(Player p) {
        p.sendMessage(Component.text("دسترسی نداری.", NamedTextColor.RED));
    }
}
