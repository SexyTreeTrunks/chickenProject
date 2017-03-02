package org.chicken_ar;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;

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

        gpsDirectionInfo = new GpsDirectionInfo(this.getApplicationContext(), this);

        try {
            tmapClient.execute(Double.toString(gpsDirectionInfo.lon),Double.toString(gpsDirectionInfo.lat)).get();
            pathPoints = tmapClient.getPathPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }
}