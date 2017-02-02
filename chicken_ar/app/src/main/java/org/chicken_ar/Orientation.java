package org.chicken_ar;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

/**`
 * Created by DongHyun on 2016-12-20.
 */


//public class Orientation extends Thread implements SensorEventListener {
public class Orientation implements SensorEventListener {
    //TextView[] tv;
    SensorManager m_sensor_manager;
    Sensor m_ot_sensor;
    CameraActivity cameraActivity;

    ImageView[] img = new ImageView[3];

    Gpsinfo gpsinfo;

    BuildingInfo[] bi;
    BuildingInfo my;

    float viewAngle = 20;


    float width;
    float height;

    float[] imgWidth = new float[3];
    float[] imgHeight = new float[3];


    public Orientation(CameraActivity ma)
    {
        cameraActivity =ma;
        gpsinfo = new Gpsinfo(cameraActivity.getApplicationContext());

        my = new BuildingInfo(gpsinfo.getLongitude(),gpsinfo.getLatitude(),0);

        bi = new BuildingInfo[3];

        bi[2] = new BuildingInfo(127.075201, 37.549441, 0);//학생회관 37.549441, 127.075201 ->충무
        bi[1] = new BuildingInfo(126.963361, 37.545680, 0);//광개토 37.550276, 127.073152 ->
        bi[0] = new BuildingInfo(127.073952, 37.552261,0);//충무관 ->학생

        width = ma.dm.widthPixels;
        height = ma.dm.heightPixels;


        img[2] = (ImageView)ma.findViewById(R.id.duck2); //광개토
        img[1] = (ImageView)ma.findViewById(R.id.duck1); //충무관
        img[0] = (ImageView)ma.findViewById(R.id.duck3); //학생

        for(int i=0;i<3;i++) {
            img[i].getLayoutParams().width = 500;
            img[i].getLayoutParams().height = 500;

            imgWidth[i]=(float) img[i].getLayoutParams().width;
            imgHeight[i]=(float) img[i].getLayoutParams().height;


            //Log.i("값 비교 -----> " + img.getX()+"",img.getY()+" : " + imgHeight + " : " + img.getHeight());
            final int finali = i;

            img[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finali==0)
                        Toast.makeText(cameraActivity.getApplicationContext(),"더 가까이 가서 선택해주세요",Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(cameraActivity.getApplicationContext(), InfoActivity.class);
                        intent.putExtra("index", finali);
                        cameraActivity.startActivity(intent);
                    }
                }
            });

        }

        img[0].getLayoutParams().width = 200;
        img[0].getLayoutParams().height = 200;

        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        m_sensor_manager = (SensorManager)ma.getSystemService(SENSOR_SERVICE);
        // SensorManager 를 이용해서 방향 센서 객체를 얻는다.
        m_ot_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        m_sensor_manager.registerListener(this, m_ot_sensor, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                for (int i = 0; i < 3; i++) {
                    if (isBuildingVisible(my, bi[i], event.values[0], event.values[1])) {
                        double disX = bi[i].lat - my.lat;
                        double disY = bi[i].lon - my.lon;

                        double offset,hAngle;
                        if(disX<0)
                        {
                            disX=-disX;
                            if(disY<0)
                            {
                                disY=-disY;
                                offset = Math.atan(disY/disX)*180/ Math.PI;
                                hAngle= 270 -offset;
                            }
                            else
                            {
                                offset = Math.atan(disY/disX)*180/ Math.PI;
                                hAngle= 270 +offset;
                            }
                        }
                        else
                        {
                            if(disY<0)
                            {
                                disY=-disY;
                                offset = Math.atan(disY/disX)*180/ Math.PI;
                                hAngle= 90 +offset;
                            }
                            else
                            {
                                offset = Math.atan(disY/disX)*180/ Math.PI;
                                hAngle= 90 - offset;
                            }
                        }


                        double degree = event.values[0] - hAngle;

                        if (180 < degree)
                            degree -= 360;
                        else if (degree < -180)
                            degree += 360;

                        img[i].setVisibility(View.VISIBLE);
                        //Log.i("hAngle : " + ((int) event.values[0] - hAngle) + viewAngle, "기울기 : " + (int) event.values[1]);
                        //Log.i("값 : ", " " + width * ((viewAngle - (degree)) / (viewAngle * 2)));
                        //Log.i("event[0] : " + event.values[0], "hAngle : " + hAngle);
                        //Log.i("half : " + (event.values[0] - hAngle), "asdasdas");
                        Log.i(i+"","번째");

                        Log.i(i+"",(width - imgWidth[i]) / 2 + width * (-(degree) / viewAngle)+"");
                        Log.i(i+"",(height - imgHeight[i]) / 2 + (-((int) (event.values[1]) + 90) / (float) 90) * (height)+"");
                        img[i].setX((float) ((width - imgWidth[i]) / 2 + width * (-(degree) / viewAngle)));
                        img[i].setY((height - imgHeight[i]) / 2 + (-((int) (event.values[1]) + 90) / (float) 90) * (height));
//                img.setX(width*((viewAngle - (event.values[0] - hAngle)) /(viewAngle*2)) - imgWidth);
//                img.setY(height*(-(90 + (int)event.values[1])/(viewAngle*2)) - imgHeight);

                    }
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void finalize() throws Throwable {
        m_sensor_manager.unregisterListener(this);
        super.finalize();
    }

    public boolean isBuildingVisible(BuildingInfo myPos, BuildingInfo buildingPos, float way, float grad)
    {
        double disX = buildingPos.lat - my.lat;
        double disY = buildingPos.lon - my.lon;

        double offset,hAngle;
        if(disX<0)
        {
            disX=-disX;
            if(disY<0)
            {
                disY=-disY;
                offset = Math.atan(disY/disX)*180/ Math.PI;
                hAngle= 270 -offset;
            }
            else
            {
                offset = Math.atan(disY/disX)*180/ Math.PI;
                hAngle= 270 +offset;
            }
        }
        else
        {
            if(disY<0)
            {
                disY=-disY;
                offset = Math.atan(disY/disX)*180/ Math.PI;
                hAngle= 90 +offset;
            }
            else
            {
                offset = Math.atan(disY/disX)*180/ Math.PI;
                hAngle= 90 - offset;
            }
        }
        double gradient = grad;
        double degree = way - hAngle;

        if(180 < degree)
            degree -= 360;
        else if ( degree < -180)
            degree += 360;

        if(-20 <= degree && degree <= 20 && -135 <= gradient && gradient <= -45) {
            Log.i("test","해당 위치에 건물 존재");
            return true;
        }
        else
            return false;
    }
}