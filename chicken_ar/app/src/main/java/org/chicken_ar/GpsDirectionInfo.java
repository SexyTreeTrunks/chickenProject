package org.chicken_ar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
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
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
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

    ImageView arrowImage;
    TextView textView;

    BuildingInfo[] buildingLocation;
    BuildingInfo myLocation;

    float viewAngle = 20;

    float width;
    float height;

    float imgWidth;
    float imgHeight;

    protected LocationManager locationManager;

    int count = 0;

    public GpsDirectionInfo(Context context) {
        this.mContext = context;
    }

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

        arrowImage = (ImageView)ma.findViewById(R.id.duck3);
        textView = (TextView)ma.findViewById(R.id.textView);

        imgWidth = (float) arrowImage.getLayoutParams().width;
        imgHeight = (float) arrowImage.getLayoutParams().height;

        /*for(int i=0;i<3;i++) {
            img[i].getLayoutParams().width = 200;
            img[i].getLayoutParams().height = 200;

            imgWidth[i]=(float) img[i].getLayoutParams().width;
            imgHeight[i]=(float) img[i].getLayoutParams().height;

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
        }*/

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
            //for (int i = 0; i < 3; i++) {
                //if (isBuildingVisible(myLocation, buildingLocation[i], event.values[0], event.values[1])) {

            // 화살표 이미지
            arrowImage.setVisibility(View.VISIBLE);
            //arrowImage.setX((width - imgWidth) / 2);
            arrowImage.setX(width/6 - 60);
            arrowImage.setY((height - imgHeight)/2 + (-(-90 + 90) / (float) 90) * (height));

            //포인트까지의 거리계산
            if(pathPoints != null) {
                double degreeForArrow = getDegreeForArrow(count, event.values[0]);
                //double degreeForArrow2 = getDegreeForArrow(count+1);
                //double degreeForArrow3 = getDegreeForArrow(count+2);

                Log.i("*****point좌표", "나의좌표 : " + myLocation.lat + ", 나의 위도 : " + myLocation.lon + "\n" +
                            "건물좌표 : " + pathPoints.get(count).getLatitude() + ", 건물위도 : " + pathPoints.get(count).getLongitude());
                double distance = calculateDistance(myLocation.lat, myLocation.lon, pathPoints.get(count).getLatitude(), pathPoints.get(count).getLongitude());

                //화살표 이미지 회전. 쓰려면 activity_camera.xml에서 해당 imageview에 대한 backgraound 사항 지워야함
                //arrowImage.setImageBitmap(rotateImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.duck3), (float) degreeForArrow * (-1)));
                if(-30 <= degreeForArrow && degreeForArrow <= 30) {
                    arrowImage.setRotation(0);//don't rotate!
                } else if(degreeForArrow>=-120 &&degreeForArrow<-30) {
                    arrowImage.setRotation(-90);
                } else if(degreeForArrow<=120 && degreeForArrow>30) {
                    arrowImage.setRotation(90);
                } else {
                    arrowImage.setRotation(180);
                }
                //arrowImage.setRotation((float)degreeForArrow * (-1));
                if(distance < 500)
                    textView.setText("다음 포인트까지 남은 거리 : " + (int)distance + "m");

                double distanceForPoint = calculateDistance(myLocation.lat, myLocation.lon, pathPoints.get(count).getLatitude(), pathPoints.get(count).getLongitude());
                if (distanceForPoint <= 1 && count + 1 < pathPoints.size())
                    count++;
                if (distanceForPoint <= 1 && count + 1 == pathPoints.size())
                    Toast.makeText(cameraActivity.getApplicationContext(), "목적지에 도착했습니다", Toast.LENGTH_LONG).show();
            }
                /*
                if (-20 <= degree && degree <= 20 && -135 <= gradient && gradient <= -45) {
                    Log.i("test", "해당 위치에 건물 존재");
                    if (i != 0) { //화살표 이미지를 제외한 이미지들만 화면에 나타나게
                        img[i].setVisibility(View.VISIBLE);
                        double distance = calculateDistance(myLocation.lat, myLocation.lon, buildingLocation[i].lat, buildingLocation[i].lon);
                        int imageSize = 500 - (int) distance * 10;
                        img[i].getLayoutParams().width = imageSize;
                        img[i].getLayoutParams().height = imageSize;

                        img[i].setX((float) ((width - imgWidth[i]) / 2 + width * (-(degree) / viewAngle)));
                        img[i].setY((height - imgHeight[i]) / 2 + (-((int) (event.values[1]) + 90) / (float) 90) * (height));
                    }
                }
//                img.setX(width*((viewAngle - (event.values[0] - hAngle)) /(viewAngle*2)) - imgWidth);
//                img.setY(height*(-(90 + (int)event.values[1])/(viewAngle*2)) - imgHeight);
                //}
            }*/

        }
    }

    private double getDegreeForArrow(int count_num, float event_value) {
        double disXforArrow = pathPoints.get(count_num).getLatitude() - myLocation.lat;
        double disYforArrow = pathPoints.get(count_num).getLongitude() - myLocation.lon;
        double hAngleForArrow = calcHAngle(disXforArrow, disYforArrow);
        double degreeForArrow = event_value - hAngleForArrow;
        if (180 < degreeForArrow)
            degreeForArrow -= 360;
        else if (degreeForArrow < -180)
            degreeForArrow += 360;
        return degreeForArrow;
    }


    public double calcHAngle(double disX, double disY) {
        double offset, hAngle;

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
        return hAngle;
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
        Log.i("****GPS Accuracy", Float.toString(location.getAccuracy()));
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
