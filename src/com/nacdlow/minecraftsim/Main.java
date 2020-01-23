package com.nacdlow.minecraftsim;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info("Running Nacdlow Minecraft Simulation plugin");
        config.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }
    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerToggleLightSwitch(PlayerInteractEvent event) {
        if(event.getClickedBlock().getBlockData().getAsString().equals("stone_button")) {
            getLogger().info("A button has been clicked!");
        }
    }
}
