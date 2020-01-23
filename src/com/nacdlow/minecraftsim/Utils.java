package com.nacdlow.minecraftsim;

import org.bukkit.Location;

public class Utils {

    public static Location coordsToLocation(String str) {
        String[] coordsArr = str.split(",");
        float x = Integer.parseInt(coordsArr[0]);
        float y = Integer.parseInt(coordsArr[1]);
        float z = Integer.parseInt(coordsArr[2]);

        return new Location(null,x,y,z);
    }
}
