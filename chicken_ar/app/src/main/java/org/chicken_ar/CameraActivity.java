package org.chicken_ar;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
    private double dest_lon;
    private double dest_lat;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dm = getApplicationContext().getResources().getDisplayMetrics();
        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView);

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
                Log.i("****CameraAtiv","onPostExecute");
            }
        };

        try {
            tmapClient.execute(Double.toString(gpsDirectionInfo.lon),Double.toString(gpsDirectionInfo.lat),Double.toString(dest_lon),Double.toString(dest_lat)).get();
            if(tmapClient.getStatus() == AsyncTask.Status.FINISHED) {
                Toast.makeText(getApplicationContext(), "tmapData 겟또!", Toast.LENGTH_SHORT).show();
            }

            tmapClient.execute("126.963737","37.545390",Double.toString(dest_lon), Double.toString(dest_lat)); //대충 명신관 연구실 위치
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