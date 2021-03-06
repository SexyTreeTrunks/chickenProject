package org.chicken_ar;

/**
 * Created by DongHyun on 2016-12-21.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements ReviewDataDownload.Listener, AdapterView.OnItemClickListener {
    RatingBar userRatingBar;
    EditText editTextReview;
    ListView listView;
    Bitmap bitmap;
    ProgressDialog loading;
    ReviewDataDownload reviewDataDownload;
    private String userID = "chicken2";
    private String restaurant_name;
    private double longitude;
    private double latitude;
    private float ratingStars;
    private float userRatingStars;
    ArrayList<ReviewInfoListViewItem> listViewItemList;
    private List<ReviewInfo> reviewInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initialVariable();
        settingButtonVariable();
        settingViewFlipper();
        new ReviewDataDownload(this).execute(restaurant_name);

        //Review 띄우는 ListView랑 viewFilpper 관련 설정(이미지 띄우기)만 하면됨!
    }

    private void initialVariable() {
        Intent intent = getIntent();
        restaurant_name = intent.getExtras().getString("NAME");
        longitude = intent.getExtras().getDouble("LON");
        latitude = intent.getExtras().getDouble("LAT");
        ratingStars = intent.getExtras().getFloat("RATINGSTARS");

        TextView textViewRestaurantName = (TextView) findViewById(R.id.restaurantName);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarInfo);
        textViewRestaurantName.setText(restaurant_name);
        ratingBar.setRating(ratingStars);
        editTextReview = (EditText) findViewById(R.id.editTextReview);
        userRatingBar = (RatingBar) findViewById(R.id.ratingBarForUser);
        userRatingBar.setIsIndicator(false);
        userRatingBar.setStepSize((float) 0.5);
        listView = (ListView) findViewById(R.id.listViewReview);

    }

    private void settingButtonVariable() {
        Button buttonFindingPath = (Button) findViewById(R.id.buttonFindingPath);
        buttonFindingPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("DEST_LON_KEY", longitude);
                intent.putExtra("DEST_LAT_KEY", latitude);
                startActivity(intent);
            }
        });
        Button buttonUploadReview = (Button) findViewById(R.id.buttonUploadReview);
        buttonUploadReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = editTextReview.getText().toString();
                userRatingStars = userRatingBar.getRating();
                uploadReview(restaurant_name, userID, Integer.toString((int) userRatingStars * 2), contents);
            }
        });

    }

    private void settingViewFlipper() {
        ViewFlipper viewFlipper;
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageForViewFlipper1);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageForViewFlipper2);
        ImageView imageView3 = (ImageView) findViewById(R.id.imageForViewFlipper3);
        String url1;
        String url2;
        String url3;
        if(restaurant_name.equals("설쌈냉면")) {
            url1 = "http://cythumb.cyworld.com/810x0/c2down.cyworld.co.kr/download?fid=64224cb80c31fe5f423f4cef69cd5fb2&name=20150423_122209.jpg";
            url2 = "http://cfile4.uf.tistory.com/image/256EB54453970EE22B5174";
            url3 = "http://cfile2.uf.tistory.com/image/22053C40585FD94E1AD1A6";
        }else if(restaurant_name.equals("버거인")) {
            url1 = "http://mblogthumb4.phinf.naver.net/20150316_207/ns00023_1426489406146OvdyN_JPEG/P20150316_134348645_9B0CFE98-6EEC-4D12-A17A-7B8B7488D4ED.JPG?type=w2";
            url2 = "https://img.siksinhot.com/place/1486677048715305.jpg";
            url3 = "http://mblogthumb3.phinf.naver.net/20160213_226/gahee1266_1455373840926IedVV_JPEG/P20160213_134626284_567AFAA9-F1D3-4516-9BB9-EF85781A574D.JPG?type=w420";
        }else {
            url1 = "http://img1.itpic.co.kr/download?redirect=/U001001/moa/2016/06/28/44/moa_8031467086861884_811_1_467345.jpg";
            url2 = "http://mblogthumb1.phinf.naver.net/20130731_280/mijung700_1375207536711JGFwb_JPEG/02.jpg?type=w2";
            url3 = "https://mobiletax.kr/static/images/landing/juicy.jpg";
        }

        imageFromURL(url1, imageView1);
        imageFromURL(url2, imageView2);
        imageFromURL(url3, imageView3);
        viewFlipper.removeAllViews();
        viewFlipper.addView(imageView1);
        viewFlipper.addView(imageView2);
        viewFlipper.addView(imageView3);
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(3000);
    }

    public void uploadReview(String name, String userId, String ratingStars, String contents) {
        DataUpload task = new DataUpload();
        task.execute(name, userId, ratingStars, contents);
        // new ReviewDataDownload(this).execute(restaurant_name);
        editTextReview.setText("");
        userRatingBar.setRating(0);
        new ReviewDataDownload(this).execute(restaurant_name);
    }

    public void uploadCalculatedRatingStars(String type, String restaurantName, String ratingStars) {
        DataUpload task = new DataUpload();
        task.execute(type, restaurantName, ratingStars);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onLoaded(List<ReviewInfo> reviewInfoList) {
        Log.d("****infoActiv", "onloaded 진입");
        this.reviewInfoList = reviewInfoList;
        listViewItemList = new ArrayList<ReviewInfoListViewItem>();
        for (int i = 0; i < reviewInfoList.size(); i++) {
            ReviewInfoListViewItem listViewItem = new ReviewInfoListViewItem();
            listViewItem.setUserId(reviewInfoList.get(i).getUserId());
            listViewItem.setRatingStars((float) reviewInfoList.get(i).getRatingStars() / 2);
            listViewItem.setContents(reviewInfoList.get(i).getContents());
            Log.d("****reviewInfoList", "userId:" + reviewInfoList.get(i).getUserId());
            Log.d("****reviewInfoList", "ratingStars:" + reviewInfoList.get(i).getRatingStars());
            Log.d("****reviewInfoList", "contents:" + reviewInfoList.get(i).getContents());
            listViewItemList.add(listViewItem);
        }
        loadListView();
    }

    private void loadListView() {
        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listViewAdapter.initAdapterToReviewInfo(listViewItemList);
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }

    private void imageFromURL(final String stringUrl, ImageView imageview) {
        Thread mThread = new Thread() {

            @Override
            public void run() {
                try {
                    URL url = new URL(stringUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (Exception ex) {

                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            imageview.setImageBitmap(bitmap);
        } catch (InterruptedException e) {

        }

    }

}