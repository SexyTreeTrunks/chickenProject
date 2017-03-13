package org.chicken_ar;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class GpsDirectionInfo implements SensorEventListener, LocationListener {

    private final Context mContext;
    private ArrayList<Location> pathPoints;
    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon;

    // 최소 GPS 정보 업데이트 거리 1미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;

    //=====================================================

    SensorManager m_sensor_manager;
    Sensor m_ot_sensor;
    CameraActivity cameraActivity;

    ImageView[] img = new ImageView[3];

    BuildingInfo[] buildingLocation;
    BuildingInfo myLocation;

    float viewAngle = 20;

    float width;
    float height;

    float[] imgWidth = new float[3];
    float[] imgHeight = new float[3];

    protected LocationManager locationManager;

    public GpsDirectionInfo(Context context, CameraActivity ma) {
        this.mContext = context;
        getLocation();

        cameraActivity = ma;

        myLocation = new BuildingInfo(lon, lat, 0);
        buildingLocation = new BuildingInfo[3];

        buildingLocation[2] = new BuildingInfo(126.963975, 37.545700, 0);//학생회관 37.549441, 127.075201 ->충무    명신관앞
        buildingLocation[1] = new BuildingInfo(126.965632, 37.545147, 0);//광개토 37.550276, 127.073152 ->   달볶이
        buildingLocation[0] = new BuildingInfo(126.965576, 37.545156, 0);//충무관 ->학생  앤티앤스

        width = ma.dm.widthPixels;
        height = ma.dm.heightPixels;

        img[2] = (ImageView)ma.findViewById(R.id.duck2); //광개토
        img[1] = (ImageView)ma.findViewById(R.id.duck1); //충무관
        img[0] = (ImageView)ma.findViewById(R.id.duck3); //학생

        for(int i=0;i<3;i++) {
            img[i].getLayoutParams().width = 200;
            img[i].getLayoutParams().height = 200;

            imgWidth[i]=(float) img[i].getLayoutParams().width;
            imgHeight[i]=(float) img[i].getLayoutParams().height;


            //Log.i("값 비교 -----> " + img.getX()+"",img.getY()+" : " + imgHeight + " : " + img.getHeight());
            final int finali = i;

            img[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(finali==0)
                    //    Toast.makeText(cameraActivity.getApplicationContext(),"더 가까이 가서 선택해주세요",Toast.LENGTH_SHORT).show();
                    //else{
                    Intent intent = new Intent(cameraActivity.getApplicationContext(), InfoActivity.class);
                    intent.putExtra("index", finali);
                    cameraActivity.startActivity(intent);
                    //}
                }
            });
        }
        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        m_sensor_manager = (SensorManager)ma.getSystemService(SENSOR_SERVICE);
        // SensorManager 를 이용해서 방향 센서 객체를 얻는다.
        m_ot_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //SensorManager.getOrientation()
        m_sensor_manager.registerListener(this, m_ot_sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
                return null;
            }
            else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
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
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            for (int i = 0; i < 3; i++) {
                //if (isBuildingVisible(myLocation, buildingLocation[i], event.values[0], event.values[1])) {
                double disX = buildingLocation[i].lat - myLocation.lat;
                double disY = buildingLocation[i].lon - myLocation.lon;
                double offset,hAngle;
                if(disX<0) {
                    disX=-disX;
                    if(disY<0) {
                        disY=-disY;
                        offset = Math.atan(disY/disX)*180/ Math.PI;
                        hAngle= 270 -offset;
                    }
                    else {
                        offset = Math.atan(disY/disX)*180/ Math.PI;
                        hAngle= 270 +offset;
                    }
                }
                else {
                    if(disY<0) {
                        disY=-disY;
                        offset = Math.atan(disY/disX)*180/ Math.PI;
                        hAngle= 90 +offset;
                    }
                    else {
                        offset = Math.atan(disY/disX)*180/ Math.PI;
                        hAngle= 90 - offset;
                    }
                }
                double degree = event.values[0] - hAngle;
                double gradient = event.values[1];
                if (180 < degree)
                    degree -= 360;
                else if (degree < -180)
                    degree += 360;
                //Log.i("hAngle : " + ((int) event.values[0] - hAngle) + viewAngle, "기울기 : " + (int) event.values[1]);
                //Log.i("값 : ", " " + width * ((viewAngle - (degree)) / (viewAngle * 2)));
                //Log.i("event[0] : " + event.values[0], "hAngle : " + hAngle);
                //Log.i("half : " + (event.values[0] - hAngle), "asdasdas");
                Log.i(i+"","번째");

                if(-20 <= degree && degree <= 20 && -135 <= gradient && gradient <= -45) {
                    Log.i("test","해당 위치에 건물 존재");
                    for(int k = 0; k < 3; k++) {
                        img[k].setVisibility(View.VISIBLE);
                        double distance = calculateDistance(myLocation.lat, myLocation.lon, buildingLocation[k].lat, buildingLocation[k].lon);
                        int imageSize = 500 - (int) distance * 10;
                        img[k].getLayoutParams().width = imageSize;
                        img[k].getLayoutParams().height = imageSize;
                    }

                    Log.i(i+"",(width - imgWidth[i]) / 2 + width * (-(degree) / viewAngle)+"");
                    Log.i(i+"",(height - imgHeight[i]) / 2 + (-((int) (event.values[1]) + 90) / (float) 90) * (height)+"");
                    img[i].setX((float) ((width - imgWidth[i]) / 2 + width * (-(degree) / viewAngle)));
                    img[i].setY((height - imgHeight[i]) / 2 + (-((int) (event.values[1]) + 90) / (float) 90) * (height));
                }

//                img.setX(width*((viewAngle - (event.values[0] - hAngle)) /(viewAngle*2)) - imgWidth);
//                img.setY(height*(-(90 + (int)event.values[1])/(viewAngle*2)) - imgHeight);
                //}
            }

          /*  String str;
            // 첫번째 데이터인 방위값으로 문자열을 구성하여 텍스트뷰에 출력한다.
            str = "azimuth(z) : " + (int) event.values[0];
            tv[0].setText(str);

            // 두번째 데이터인 경사도로 문자열을 구성하여 텍스트뷰에 출력한다.
            str = "pitch(x) : " + (int) event.values[1];
            tv[1].setText(str);

            // 세번째 데이터인 좌우 회전값으로 문자열을 구성하여 텍스트뷰에 출력한다.
            str = "roll(y) : " + (int) event.values[2];
            tv[2].setText(str);
*/
            /*
            // 함수의 출력횟수를 텍스트뷰에 출력한다.
            m_check_count++;
            str = "호출 횟수 : " + m_check_count + " 회";
            m_check_view.setText(str);
            */
        }
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

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation.lat = location.getLatitude();
        myLocation.lon = location.getLongitude();
        //Log.i("****GpsDirct info", "onLocationChange----lon: " + lon + ",lat: " + lat);
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void getPathPoints(ArrayList<Location> pathPoints) {
        this.pathPoints = pathPoints;
    }

}
