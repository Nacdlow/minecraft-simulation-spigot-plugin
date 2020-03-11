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

    public static JSONObject apiData = null;

    public APIChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.checkInterval = this.plugin.getConfig().getLong("refresh_rate_ms");
    }

    public void updateWorld() throws IOException, ParseException {
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
    }

    public void updateLightStatus() throws IOException, ParseException {
        // Get data from API
        String verEnc = URLEncoder.encode(plugin.getServer().getVersion(), StandardCharsets.UTF_8.toString());
        URL url = new URL(plugin.getConfig().getString("bridge_plugin_url") + "/get_device_states");

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
        JSONArray root = (JSONArray) obj;
        // Update lights
        for (int i = 0; i < 99; i++) {
            if (plugin.getConfig().contains("light_groups." + i)) {
                Iterator itr = root.iterator();
                while (itr.hasNext()) {
                    JSONObject room = (JSONObject) itr.next();
                    String id = (String)room.get("Id");
                    if (plugin.getConfig().contains("light_groups."+id)){
                        List<String> coords = plugin.getConfig().getStringList("light_groups." + id + ".activation_coords");
                        boolean status = (boolean) room.get("Status");
                        String on = plugin.getConfig().getString("light_groups." + id + ".use_active_block");
                        String off = plugin.getConfig().getString("light_groups." + id + ".use_unactive_block");
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
                    }
                }
            }
        }
    }

    public void registerDevices() throws IOException {
        for (int i = 0; i < 99; i++) {
            if (plugin.getConfig().contains("light_groups." + i)) {
                URL url = new URL(plugin.getConfig().getString("bridge_plugin_url") + "/register_light_group/" + i);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(500);
                con.setReadTimeout(500);
                con.getInputStream();
            }
        }
    }

    @Override
    public void run() {
        try {
            registerDevices();
            updateWorld();
            updateLightStatus();

        } catch (ConnectException ex) {
            apiData = null;
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }
}
