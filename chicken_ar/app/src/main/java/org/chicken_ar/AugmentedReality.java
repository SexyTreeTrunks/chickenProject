package org.chicken_ar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lak Shin Jeong on 2017-03-29.
 */

public class AugmentedReality extends AppCompatActivity implements DiningDataDownload.Listener {

    ArrayList<DiningInfoListViewItem> totalStoreList;
    List<DiningInfo> diningInfoList;
    ArrayList<DiningInfoListViewItem> listViewItemList;

    DisplayMetrics dm;
    private TextureView mCameraTextureView;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dm = getApplicationContext().getResources().getDisplayMetrics();
        mCameraTextureView = (TextureView) findViewById(R.id.cameraARTextureView);
        mPreview = new Preview(this, mCameraTextureView);

        totalStoreList = new ArrayList<DiningInfoListViewItem>();

        new DiningDataDownload(this).execute(CategoryType.CAFE);
        new DiningDataDownload(this).execute(CategoryType.DINING_KOREA);
        new DiningDataDownload(this).execute(CategoryType.DINING_SNACK);
        new DiningDataDownload(this).execute(CategoryType.DINING_JAPANESE);
        new DiningDataDownload(this).execute(CategoryType.DINING_CHINESE);
        new DiningDataDownload(this).execute(CategoryType.DINING_WESTERN);
    }

    @Override
    public void onLoaded(List<DiningInfo> diningInfoList) {
        this.diningInfoList = diningInfoList;
        listViewItemList = new ArrayList<DiningInfoListViewItem>();
        for(int i = 0; i < diningInfoList.size(); i++) {
            DiningInfoListViewItem listViewItem = new DiningInfoListViewItem();
            listViewItem.setName(diningInfoList.get(i).getName());
            listViewItem.setRatingStar((float)diningInfoList.get(i).getratingStar()/2);
            //Location myLocation = getLocation();
            int calculatedDistance = 0;
            listViewItem.setDistance(calculatedDistance);
            listViewItemList.add(listViewItem);
        }
        totalStoreList.addAll(listViewItemList);
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
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
