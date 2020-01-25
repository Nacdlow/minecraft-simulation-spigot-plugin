package com.nacdlow.minecraftsim;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class APIChecker implements Runnable {
    private JavaPlugin plugin;
    private long checkInterval;

    public static JSONObject apiData = new JSONObject();

    public APIChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.checkInterval = this.plugin.getConfig().getLong("refresh_rate_ms");
    }

    @Override
    public void run() {
        try {
            // Get data from API
            String verEnc = URLEncoder.encode(plugin.getServer().getVersion(), StandardCharsets.UTF_8.toString());
            URL url = new URL(plugin.getConfig().getString("simulator_url") + "/sim/data.json?from=minecraft&server=" + verEnc);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(500);
            con.setReadTimeout(500);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            con.getInputStream()));
            String inputLine;
            String jsonResp = "";

            while ((inputLine = in.readLine()) != null)
                jsonResp += inputLine;
            in.close();

            // Convert to JSON
            Object obj = new JSONParser().parse(jsonResp);
            apiData = (JSONObject) obj;

            // Set Minecraft time to match simulation
            float minecraft_time = (long) apiData.get("minecraft_time");
            plugin.getServer().getWorld("world").setTime((long) minecraft_time); // Minecraft world is 72 times faster than real world
            JSONObject home = (JSONObject) apiData.get("home");
            // Update lights
            for (int i = 0; i < 99; i++) {
                if (plugin.getConfig().contains("light_groups." + i)) {
                    Iterator itr = ((JSONArray) home.get("rooms")).iterator();
                    while (itr.hasNext()) {
                        JSONObject room = (JSONObject) itr.next();
                        if (((long) room.get("main_light_device_id")) == plugin.getConfig().getInt("light_groups." + i + ".device_id")) {
                            List<String> coords = plugin.getConfig().getStringList("light_groups." + i + ".activation_coords");
                            boolean status = (boolean) room.get("light_status");
                            String on = plugin.getConfig().getString("light_groups." + i + ".use_active_block");
                            String off = plugin.getConfig().getString("light_groups." + i + ".use_unactive_block");
                            try {
                                coords.forEach(coord -> {
                                    Location loc = Utils.coordsToLocation(coord);
                                    if (status) {
                                        plugin.getServer().getWorld("world").getBlockAt(loc).setType(Material.valueOf(on));
                                    } else {
                                        plugin.getServer().getWorld("world").getBlockAt(loc).setType(Material.valueOf(off));
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            continue;
                        }
                    }
                }
            }

        } catch (ConnectException ex) {
            plugin.getLogger().warning("Failed to connect to API!");
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }
}
