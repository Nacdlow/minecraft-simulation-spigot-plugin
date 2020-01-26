package com.nacdlow.minecraftsim;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandSim implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "== " + ChatColor.AQUA + "Nacdlow Simulation Status" + ChatColor.RED + " ==");
        Date date = new java.util.Date((Long) APIChecker.apiData.get("current_time")*1000L);
        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sender.sendMessage("Simulation Time: " + dateFormat.format(date));
        JSONObject weather = (JSONObject) APIChecker.apiData.get("weather");
        JSONObject home = (JSONObject) APIChecker.apiData.get("home");

        sender.sendMessage("Outdoor Temperature: "+home.get("power_gen_rate") +" kWh of " + home.get("solar_max_power") +" max");
        sender.sendMessage("Outdoor Temperature: " + weather.get("outdoor_temp"));
        sender.sendMessage("Outdoor Humidity: " +  weather.get("humidity"));
        sender.sendMessage("Cloud Cover: " +  weather.get("cloud_cover"));
        return true;
    }
}
