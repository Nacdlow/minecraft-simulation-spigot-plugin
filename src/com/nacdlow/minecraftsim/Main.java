package com.nacdlow.minecraftsim;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info("Running Nacdlow Minecraft Simulation plugin");
        config.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new SimEventHandler(), this);
        getLogger().info("Starting API Checker...");
        (new APIChecker(this)).start();
    }
    @Override
    public void onDisable() {
    }

}
