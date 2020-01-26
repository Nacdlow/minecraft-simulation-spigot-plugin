package com.nacdlow.minecraftsim;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class SimEventHandler implements Listener {
    private JavaPlugin plugin;

    public SimEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @org.bukkit.event.EventHandler
    public void onPlayerToggleLightSwitch(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (b.getType() == Material.STONE_BUTTON || b.getType() == Material.OAK_BUTTON) {
                for (int i = 0; i < 99; i++) {
                    if (plugin.getConfig().contains("light_groups." + i)) {
                        Location loc = Utils.coordsToLocation(plugin.getConfig().getString("light_groups." + i + ".activator_button"));
                        if (Utils.locationsEqual(loc, b.getLocation())) {
                            event.getPlayer().sendMessage(ChatColor.GREEN + "[Nacdlow Debug] Toggle light (device ID: " + plugin.getConfig().getInt("light_group." + i + ".device_id") + ")");
                            Utils.doAPICall(plugin, "/toggle/" + plugin.getConfig().getInt("light_groups." + i + ".activator_button"));
                        }
                    }
                }
            } else if (b.getType().toString().contains("BED") || b.getType() == Material.FURNACE || b.getType() == Material.CRAFTING_TABLE) {
                event.getPlayer().sendMessage(ChatColor.RED + "Nacdlow Simulation: That action is not available.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void violenceEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && ((Player) event.getDamager()).getGameMode() == GameMode.ADVENTURE) {
            ((Player) event.getDamager()).sendMessage(ChatColor.RED + "Nacdlow Simulation: Nacdlow is against all types of violence.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void breakPaintingEvent(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && ((Player) event.getRemover()).getGameMode() == GameMode.ADVENTURE) {
            ((Player) event.getRemover()).sendMessage(ChatColor.RED + "Nacdlow Simulation: NOPE.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getPlayer().isOp()) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH) {
                Location loc = event.getBlock().getLocation();
                int x = loc.getBlockX();
                int y = loc.getBlockY();
                int z = loc.getBlockZ();
                String coord = "" + x + "," + y + "," + z;
                event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "[Nacdlow] Coords: " + coord + " (" + event.getBlock().getType().toString() + ") (copied)");
                try {
                    StringSelection stringSelection = new StringSelection(coord);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                } catch (Exception e) {
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.AQUA + "Welcome to the Nacdlow Minecraft Simulation server!");
        event.setJoinMessage(ChatColor.GOLD + event.getPlayer().getDisplayName() + " joined Nacdlow");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().isOp()) {
            event.setFormat(ChatColor.AQUA + "[Nacdlow] " + ChatColor.DARK_RED + "%s" + ChatColor.WHITE + ": %s");
        } else {
            event.setFormat(ChatColor.AQUA + "[Nacdlow] " + ChatColor.DARK_GRAY + "%s" + ChatColor.WHITE + ": %s");
        }
    }
}
