package com.nacdlow.minecraftsim;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info("Running Nacdlow Minecraft Simulation plugin");
        config.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new SimEventHandler(this), this);
        getLogger().info("Starting API Checker...");
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new APIChecker(this), 0L, 2L);
        this.getCommand("sim").setExecutor(new CommandSim());
    }

    @Override
    public void onDisable() {
    }
}
