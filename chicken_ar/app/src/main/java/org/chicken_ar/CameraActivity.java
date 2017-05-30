package org.chicken_ar;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {
    private TextureView mCameraTextureView;
    private Preview mPreview;
    private GpsDirectionInfo gpsDirectionInfo;
    private TmapClient tmapClient;
    private ArrayList<Location> pathPoints;
    private double dest_lon;
    private double dest_lat;
    DisplayMetrics dm;
    private ArrayList<Double> distancePerPoint;
    double totalDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dm = getApplicationContext().getResources().getDisplayMetrics();
        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView);
        
        Log.d("****Camera Actv","onCreate 실행");
    }

    public void getDistancePerPoint() {
        distancePerPoint = new ArrayList<Double>();
        for (int i = 0; i < tmapClient.getPathPoints().size() - 1; i++) {
            Location currentPoint = tmapClient.getPathPoints().get(i);
            Location nextPoint = tmapClient.getPathPoints().get(i + 1);
            double distance = gpsDirectionInfo.calculateDistance(currentPoint.getLatitude(), currentPoint.getLongitude(),
                    nextPoint.getLatitude(), nextPoint.getLongitude());
            totalDistance += distance;
            distancePerPoint.add(distance);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();

        Intent intent = getIntent();
        dest_lon = intent.getExtras().getDouble("DEST_LON_KEY");
        dest_lat = intent.getExtras().getDouble("DEST_LAT_KEY");
        gpsDirectionInfo = new GpsDirectionInfo(this.getApplicationContext(), this);
        tmapClient = new TmapClient() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                gpsDirectionInfo.setPathPoints(getPathPoints());
                gpsDirectionInfo.setPathDescriptions(getPathDescriptions());
                //gpsDirectionInfo.setPointList(getPointList());

                getDistancePerPoint();
                gpsDirectionInfo.setDistancePerPoint(distancePerPoint);
                gpsDirectionInfo.setTotalDistanceAndRemainDistance(totalDistance);
                Log.i("****CameraAtiv", "onPostExecute");
            }
        };

        try {
            tmapClient.execute(Double.toString(gpsDirectionInfo.lon),Double.toString(gpsDirectionInfo.lat),Double.toString(dest_lon),Double.toString(dest_lat)).get();

        } catch (Exception e) {
            Log.e("****CameraActv error","tmapClient execute error");
            e.printStackTrace();
        }
        Log.d("****Camera Actv", "onResume 실행");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}