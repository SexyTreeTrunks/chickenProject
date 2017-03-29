package org.chicken_ar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lak Shin Jeong on 2017-03-29.
 */

public class AugmentedReality extends AppCompatActivity implements DiningDataDownload.Listener, LocationListener, SensorEventListener {

    List<DiningInfo> diningInfoList;
    ArrayList<DiningInfo> totalStoreList;

    DisplayMetrics dm;
    private TextureView mCameraTextureView;
    private Preview mPreview;
    TextView textView;
    float screenWidth;
    float screenHeight;
    int userDegree;
    int count = 0;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;
    Location location;
    double lat;
    double lon;
    int viewAngle = 20;

    protected LocationManager locationManager;
    BuildingInfo myLocation;
    SensorManager m_sensor_manager;
    Sensor m_ot_sensor;

    TextView textViewAR[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dm = getApplicationContext().getResources().getDisplayMetrics();
        mCameraTextureView = (TextureView) findViewById(R.id.cameraARTextureView);
        mPreview = new Preview(this, mCameraTextureView);

        initVariables();

        totalStoreList = new ArrayList<DiningInfo>();

        new DiningDataDownload(this).execute(CategoryType.CAFE);
        new DiningDataDownload(this).execute(CategoryType.DINING_KOREA);
        new DiningDataDownload(this).execute(CategoryType.DINING_SNACK);
        new DiningDataDownload(this).execute(CategoryType.DINING_JAPANESE);
        new DiningDataDownload(this).execute(CategoryType.DINING_CHINESE);
        new DiningDataDownload(this).execute(CategoryType.DINING_WESTERN);



        getLocation();
        myLocation = new BuildingInfo(lon, lat, 0);
        m_sensor_manager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        m_ot_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        m_sensor_manager.registerListener(this, m_ot_sensor, SensorManager.SENSOR_DELAY_UI);

        screenWidth = this.dm.widthPixels;
        screenHeight = this.dm.heightPixels;
    }

    public void initVariables() {
        textView = (TextView) findViewById(R.id.textView);
        textViewAR = new TextView[10];
        textViewAR[0] = (TextView) findViewById(R.id.textView1);
        textViewAR[1] = (TextView) findViewById(R.id.textView2);
        textViewAR[2] = (TextView) findViewById(R.id.textView3);
        textViewAR[3] = (TextView) findViewById(R.id.textView4);
        textViewAR[4] = (TextView) findViewById(R.id.textView5);
        textViewAR[5] = (TextView) findViewById(R.id.textView6);
        textViewAR[6] = (TextView) findViewById(R.id.textView7);
        textViewAR[7] = (TextView) findViewById(R.id.textView8);
        textViewAR[8] = (TextView) findViewById(R.id.textView9);
        textViewAR[9] = (TextView) findViewById(R.id.textView10);
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getApplication().getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled)
                return null;
            else {
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLoaded(List<DiningInfo> diningInfoList) {
        this.diningInfoList = diningInfoList;
        totalStoreList.addAll(diningInfoList);
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            userDegree = (int)event.values[0];
            getStoreNearBy();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation.lat = location.getLatitude();
        myLocation.lon = location.getLongitude();
        textView.append(myLocation.lat + "," + myLocation.lon + " ");
    }

    private void getStoreNearBy() {
        double storeLatitude, storeLongitude;
        double latdif, londif;
        ArrayList<DiningInfo> storeNearbyList = new ArrayList<DiningInfo>();

        try {
            if (totalStoreList != null) {
                for (int i = 0; i < totalStoreList.size(); i++) {
                    DiningInfo temp = totalStoreList.get(i);
                    storeLatitude = Double.parseDouble(temp.getLatitude());
                    storeLongitude = Double.parseDouble(temp.getLongitude());
                    latdif = myLocation.lat - storeLatitude;
                    londif = myLocation.lon - storeLongitude;
                    if(latdif < 0)
                        latdif = -latdif;
                    if(londif < 0)
                        londif = -londif;
                    if(latdif <= 0.0002 && londif <= 0.001)
                        storeNearbyList.add(temp);
                }

                if(storeNearbyList != null) {
                    String storeType;
                    int storeTypeInt;

                    for(int j = 0; j < 10; j++) {
                        DiningInfo temp = storeNearbyList.get(j);
                        storeType = temp.getId().substring(0, 1);
                        storeTypeInt = Integer.parseInt(storeType);
                        int bearing = (int) bearingP1toP2(myLocation.lat, myLocation.lon, Double.parseDouble(temp.getLatitude()),
                                Double.parseDouble(temp.getLongitude()));
                        int degree = userDegree - bearing;
                        double distance = calculateDistance(myLocation.lat, myLocation.lon, Double.parseDouble(temp.getLatitude()),
                                Double.parseDouble(temp.getLongitude()));

                        setImage(temp.getName(), storeTypeInt, degree, distance);
                        setClickIistener(temp);
                        count++;
                    }
                }
            }
        } catch(Exception e) {
            Log.d("****Error !!!", e+"");
        }
        count = 0;
    }

    private void setImage(String storeName, int storeTypeInt, int degree, double distance) {
        if(degree >= -20 && degree <= 20) {
            textViewAR[count].setX((float) ((screenWidth - 93) / 2 + screenWidth * (-(degree) / viewAngle)));
            textViewAR[count].setY((screenHeight - 150) / 2 + (-((int) -90 + 90) / (float) 90) * (screenHeight));
            textViewAR[count].setText((int)distance + "m\n" + storeName);
            textViewAR[count].setVisibility(View.VISIBLE);
            switch(storeTypeInt) {
                case 1:
                    textViewAR[count].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cafe, 0, 0);
                    break;
                case 2:
                    textViewAR[count].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dining_korea, 0, 0);
                    break;
                case 3:
                    textViewAR[count].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dining_chinese, 0, 0);
                    break;
                case 4:
                    textViewAR[count].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dining_japanese, 0, 0);
                    break;
                case 5:
                    textViewAR[count].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dining_western, 0, 0);
                    break;
                case 6:
                    textViewAR[count].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dining_snack2, 0, 0);
                    break;
            }
        }
        Log.d("****Activated", "!!!!!!!!!");
    }


    private void setClickIistener(final DiningInfo diningInfo) {
        textViewAR[count].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent infoIntent = new Intent(getApplicationContext(), InfoActivity.class);
                    infoIntent.putExtra("NAME",diningInfo.getName());
                    infoIntent.putExtra("LON",Double.valueOf(diningInfo.getLongitude()));
                    infoIntent.putExtra("LAT",Double.valueOf(diningInfo.getLatitude()));
                    infoIntent.putExtra("RATINGSTARS",diningInfo.getratingStar());
                    startActivity(infoIntent);
                } catch (Exception e) {
                    Log.d("****Exception!", "" + e);
                }
            }
        });
    }

    public double bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double
            P2_longitude) {
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);

        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos
                (Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos
                (radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));

        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else
            true_bearing = radian_bearing * (180 / 3.141592);
        return true_bearing;
    }

    public double calculateDistance(double myLatitude, double myLongitude, double buildingLatitude, double buildingLongitude) {
        double theta, dist;
        theta = myLongitude - buildingLongitude;
        dist = Math.sin(deg2rad(myLatitude)) * Math.sin(deg2rad(buildingLatitude)) + Math.cos(deg2rad(myLatitude))
                * Math.cos(deg2rad(buildingLatitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    private double deg2rad(double deg) {
        return (double)(deg * Math.PI / (double)180d);
    }

    private double rad2deg(double rad) {
        return (double)(rad * (double)180d / Math.PI);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
        Log.d("****Camera Actv", "onResume 실행");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }

}
