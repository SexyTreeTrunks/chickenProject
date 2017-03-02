package org.chicken_ar;

/**
 * Created by Taewoo on 2016-12-20.
 */

public class BuildingInfo {

    public static final double LON0 = 126.965576; //앤티앤스
    public static final double LON1 = 126.965632; //달볶이
    public static final double LON2 = 126.963975; //명신관앞

    public static final double LAT0 = 37.545156; //앤티앤스
    public static final double LAT1 = 37.545147; //달볶이
    public static final double LAT2 = 37.545700; //명신관앞

    //TODO Orientation.java에서도 바꿔야 반영됨

    public double lat, lon, alti;      //위도,경도, 고도

    public BuildingInfo(double latitude, double longitude, double altitude)
    {
        lat = latitude;
        lon = longitude;
        alti = altitude;
    }

}
