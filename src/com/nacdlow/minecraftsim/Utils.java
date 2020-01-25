package com.nacdlow.minecraftsim;

import org.bukkit.Location;

public class Utils {

    public static Location coordsToLocation(String str) {
        String[] coordsArr = str.split(",");
        float x = Integer.parseInt(coordsArr[0]);
        float y = Integer.parseInt(coordsArr[1]);
        float z = Integer.parseInt(coordsArr[2]);

        return new Location(null, x, y, z);
    }

    public static boolean locationsEqual(Location loc1, Location loc2){
        return (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ());
    }
}
