package org.chicken_ar;

/**
 * Created by Taewoo on 2016-12-20.
 */

public class BuildingInfo {

    public static final double LON0 = 127.075201; //학생회관
    public static final double LON1 = 127.073152; //광개토
    public static final double LON2 = 127.073952; //충무관

    public static final double LAT0 = 37.549441; //학생회관
    public static final double LAT1 = 37.550276; //광개토
    public static final double LAT2 = 37.552261; //충무관

    public double lat, lon, alti;      //위도,경도, 고도

    public BuildingInfo(double latitude, double longitude, double altitude)
    {
        lat = latitude;
        lon = longitude;
        alti = altitude;
    }

}
