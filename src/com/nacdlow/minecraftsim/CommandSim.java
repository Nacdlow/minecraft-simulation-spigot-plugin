package com.nacdlow.minecraftsim;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSim implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "== " + ChatColor.AQUA + "Nacdlow Simulation Status" + ChatColor.RED + " ==");
        sender.sendMessage("Time: " + String.valueOf((Long) APIChecker.apiData.get("current_time")));
        return true;
    }
}
