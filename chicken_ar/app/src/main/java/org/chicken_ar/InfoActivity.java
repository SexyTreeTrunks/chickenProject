package org.chicken_ar;

/**
 * Created by DongHyun on 2016-12-21.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements ReviewDataDownload.Listener, AdapterView.OnItemClickListener{
    TextView textViewRestaurantName;
    RatingBar ratingBar;
    RatingBar userRatingBar;
    ViewFlipper viewFlipper;
    Button buttonFindingPath;
    EditText editTextReview;
    ListView listView;
    Button buttonUploadReview;
    ProgressDialog loading;
    private String userID = "왕밤빵";
    private String restaurant_name;
    private double longitude;
    private double latitude;
    private float ratingStars;
    private float userRatingStars;
    ArrayList<ReviewInfoListViewItem> listViewItemList;
    private List<ReviewInfo> reviewInfoList;
    private static final String DAUM_API_KEY = "0374d14587e81f06e41447a8467a1fd6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initialVariable();
        settingVariable();

        new ReviewDataDownload(this).execute(restaurant_name);

        //Review 띄우는 ListView랑 viewFilpper 관련 설정(이미지 띄우기)만 하면됨!
    }

    private void initialVariable() {
        Intent intent = getIntent();
        restaurant_name = intent.getExtras().getString("NAME");
        longitude = intent.getExtras().getDouble("LON");
        latitude = intent.getExtras().getDouble("LAT");
        ratingStars = intent.getExtras().getFloat("RATINGSTARS");

        textViewRestaurantName = (TextView) findViewById(R.id.restaurantName);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarInfo);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        buttonFindingPath = (Button) findViewById(R.id.buttonFindingPath);
        editTextReview = (EditText) findViewById(R.id.editTextReview);
        userRatingBar = (RatingBar) findViewById(R.id.ratingBarForUser);
        userRatingBar.setIsIndicator(false);
        userRatingBar.setStepSize((float)0.5);
        buttonUploadReview = (Button) findViewById(R.id.buttonUploadReview);
        listView = (ListView) findViewById(R.id.listViewReview);

    }

    private void settingVariable() {
        textViewRestaurantName.setText(restaurant_name);
        ratingBar.setRating(ratingStars);
        buttonFindingPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("DEST_LON_KEY", longitude);
                intent.putExtra("DEST_LAT_KEY", latitude);
                startActivity(intent);
            }
        });
        buttonUploadReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = editTextReview.getText().toString();
                userRatingStars = userRatingBar.getRating();
                uploadReview(restaurant_name,userID,Float.toString(userRatingStars),contents);
            }
        });


    }



    public void uploadReview(String name, String userId, String ratingStars, String contents) {
        DataUpload task = new DataUpload() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        };
        task.execute(name, userId, ratingStars, contents);
        // new ReviewDataDownload(this).execute(restaurant_name);
    }

    public void uploadCalculatedRatingStars(String type, String restaurantName, String ratingStars) {
        DataUpload task = new DataUpload() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        };
        task.execute(type, restaurantName, ratingStars);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onLoaded(List<ReviewInfo> reviewInfoList) {
        Log.d("****infoActiv","onloaded 진입");
        this.reviewInfoList = reviewInfoList;
        listViewItemList = new ArrayList<ReviewInfoListViewItem>();
        for(int i = 0; i < reviewInfoList.size(); i++) {
            ReviewInfoListViewItem listViewItem = new ReviewInfoListViewItem();
            listViewItem.setUserId(reviewInfoList.get(i).getUserId());
            listViewItem.setRatingStars((float)reviewInfoList.get(i).getRatingStars()/2);
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
}