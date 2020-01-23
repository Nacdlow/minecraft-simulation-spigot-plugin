package com.nacdlow.minecraftsim;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SimEventHandler implements Listener {
    public SimEventHandler() {
    }
    @org.bukkit.event.EventHandler
    public void onPlayerToggleLightSwitch(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null && event.getClickedBlock().getBlockData().getAsString().equals("stone_button")) {
            Bukkit.getLogger().info("A button has been clicked!");
        }
    }
}
