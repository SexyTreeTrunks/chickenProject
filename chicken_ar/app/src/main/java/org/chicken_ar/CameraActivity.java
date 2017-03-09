package org.chicken_ar;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {
    private TextureView mCameraTextureView;
    private Preview mPreview;
    private GpsDirectionInfo gpsDirectionInfo;
    private TmapClient tmapClient;
    private ArrayList<Location> pathPoints;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        dm = getApplicationContext().getResources().getDisplayMetrics();

        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView);

        //new GpsDirectionInfo(this.getApplicationContext(), this);
        gpsDirectionInfo = new GpsDirectionInfo(this.getApplicationContext(), this);
        tmapClient = new TmapClient();

        try {
            /*
            Toast.makeText(getApplicationContext(),"CameraActv info, lon: " + gpsDirectionInfo.lon + ", lat: " + gpsDirectionInfo.lat,Toast.LENGTH_SHORT).show();
            tmapClient.execute(Double.toString(gpsDirectionInfo.lon),Double.toString(gpsDirectionInfo.lat)).get();
            if(tmapClient.getStatus() == AsyncTask.Status.FINISHED) {
                Toast.makeText(getApplicationContext(), "tmapData 겟또!", Toast.LENGTH_SHORT).show();
            }
            */
            tmapClient.execute("126.964823","37.545801");
            //tmapClient.findNearPoiDataByCategory("치킨","127.075201","37.549441");
            //tmapClient.getPoiData();
        } catch (Exception e) {
            Log.e("****CameraActv error","tmapClient execute error");
            e.printStackTrace();
        }
        Log.d("****Camera Actv","onCreate 실행");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
        Log.d("****Camera Actv", "onResume 실행");

        if(tmapClient.getStatus() == AsyncTask.Status.FINISHED)
            pathPoints = tmapClient.getPathPoints();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }
}