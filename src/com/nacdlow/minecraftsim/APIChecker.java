package com.nacdlow.minecraftsim;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIChecker extends Thread {
    private JavaPlugin plugin;
    private long checkInterval;
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

                while ((inputLine = in.readLine()) != null)
                    System.out.println(inputLine);
                in.close();

            } catch (IOException ex) {
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
