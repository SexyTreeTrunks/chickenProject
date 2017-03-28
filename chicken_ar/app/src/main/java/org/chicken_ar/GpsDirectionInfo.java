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
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class GpsDirectionInfo implements SensorEventListener, LocationListener {

    private final Context mContext;
    private ArrayList<Location> pathPoints;
    private ArrayList<Location> pointList;
    private HashMap<Integer,String> pathDescriptions;
    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat;
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

    BuildingInfo myLocation;

    float viewAngle = 20;

    float width;
    float height;

    float imgWidth;
    float imgHeight;

    protected LocationManager locationManager;

    int count = 0;
    int gpsCount = 0;

    public GpsDirectionInfo(Context context) {
        this.mContext = context;
    }

    public GpsDirectionInfo(Context context, CameraActivity ma) {
        this.mContext = context;
        getLocation();

        cameraActivity = ma;

        myLocation = new BuildingInfo(lon, lat, 0);

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
            //포인트까지의 거리계산
            if(pathPoints != null) {
                // 화살표 이미지
                arrowImage.setVisibility(View.VISIBLE);
                //arrowImage.setX((width - imgWidth) / 2);
                arrowImage.setX(width/6 - 60);
                arrowImage.setY((height - imgHeight)/2 + (-(-90 + 90) / (float) 90) * (height));

                double bearing = bearingP1toP2(myLocation.lat,myLocation.lon, pathPoints.get(count).getLatitude(), pathPoints.get(count).getLongitude());
                //double bearing = bearingP1toP2(myLocation.lat,myLocation.lon, 37.545892, 126.964676);
                writeLog(myLocation.lat+","+myLocation.lon);
                double degreeForArrow = event.values[0] - bearing;
                //double degreeForArrow2 = getDegreeForArrow(count+1);
                //double degreeForArrow3 = getDegreeForArrow(count+2);
                double distanceForPoint = calculateDistance(myLocation.lat, myLocation.lon, pathPoints.get(count).getLatitude(), pathPoints.get(count).getLongitude());//37.545892, 126.964676
                //double distanceForPoint = calculateDistance(myLocation.lat, myLocation.lon, 37.545892, 126.964676);

                if(degreeForArrow>0) {
                    while (degreeForArrow > 180)
                        degreeForArrow -= 360;
                } else {
                    while (degreeForArrow < -180)
                        degreeForArrow += 360;
                }

                textView.setText("myLoc: " + myLocation.lat + "," + myLocation.lon
                        +"\ndestLoc" + pathPoints.get(count).getLatitude() +"," + pathPoints.get(count).getLongitude()
                        +"\ndegree: "+degreeForArrow+"\ndistance: "+distanceForPoint
                        +" gps호출횟수 : " + gpsCount
                        +"\n전체 pathPoint 수 : " + pathPoints.size() + " 현재 pathPoint : " + (count+1));

                if(-30 <= degreeForArrow && degreeForArrow <= 30) {
                    arrowImage.setRotation(0);//don't rotate!
                } else if(degreeForArrow>=-120 &&degreeForArrow<-30) {
                    arrowImage.setRotation(90);
                } else if(degreeForArrow<=120 && degreeForArrow>30) {
                    arrowImage.setRotation(-90);
                } else {
                    arrowImage.setRotation(180);
                }

                //arrowImage.setRotation((float)degreeForArrow * (-1));
                //if(distanceForPoint < 500)
                 //   textView.setText("다음 포인트까지 남은 거리 : " + (int)distanceForPoint + "m");
                //if(pathDescriptions.containsKey(count))
                //    textView.setText(pathDescriptions.get(count));

                if (distanceForPoint <= 8 && count + 1 < pathPoints.size()) {
                    count++;
                    Toast.makeText(mContext.getApplicationContext(),"다음 point값 갱신",Toast.LENGTH_SHORT).show();
                }
                if (distanceForPoint <= 8 && count + 1 == pathPoints.size())
                    Toast.makeText(cameraActivity.getApplicationContext(), "목적지에 도착했습니다", Toast.LENGTH_LONG).show();

                Log.d("*****pointList", pointList.size() + "");
                for(int i = 0; i < pointList.size(); i++)
                    Log.d("*****pointList", pointList.get(i) + "");
            }
                /*
                if (-20 <= degree && degree <= 20 && -135 <= gradient && gradient <= -45) {
                    Log.i("test", "해당 위치에 건물 존재");
                    if (i != 0) { //화살표 이미지를 제외한 이미지들만 화면에 나타나게
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
    private double deg2rad(double deg) {
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad) {
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
        gpsCount++;
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

    public void setPathPoints(ArrayList<Location> pathPoints) {
        this.pathPoints = pathPoints;
    }

    public void setPointList(ArrayList<Location> pointList) {
        this.pointList = pointList;
    }

    public void setPathDescriptions(HashMap<Integer, String> hashmap) {
        this.pathDescriptions = hashmap;
    }

    public void writeLog(String contents) {
        String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
        String filename = "log.txt";
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.newLine();
            writer.flush();
            writer.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double
            P2_longitude) {
        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에
        //라디안 각도로 변환한다.
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);
        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에
        // 라디안 각도로 변환한다.
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);
        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos
                (Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는
        //방향을 설정해야 한다. 라디안값이다.
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos
                (radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));
        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.
        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);   true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }
        return true_bearing;
    }
}
