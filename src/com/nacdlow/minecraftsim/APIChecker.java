package com.nacdlow.minecraftsim;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIChecker extends Thread {
    private JavaPlugin plugin;
    private long checkInterval;

    public static JSONObject apiData = new JSONObject();
    public APIChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.checkInterval = this.plugin.getConfig().getLong("refresh_rate_ms");
    }

    @Override
    public void run() {
        while(true) {
            try {
                URL url = new URL(plugin.getConfig().getString("simulator_url") + "/sim/data.json");
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

                Object obj = new JSONParser().parse(jsonResp);

                apiData = (JSONObject) obj;
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
            try {
                sleep(checkInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
