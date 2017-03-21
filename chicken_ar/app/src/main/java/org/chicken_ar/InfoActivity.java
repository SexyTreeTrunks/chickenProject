package org.chicken_ar;

/**
 * Created by DongHyun on 2016-12-21.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class InfoActivity extends AppCompatActivity {
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
    private static final String DAUM_API_KEY = "0374d14587e81f06e41447a8467a1fd6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initialVariable();
        settingVariable();

        //Review 띄우는 ListView랑 viewFilpper 관련 설정(이미지 띄우기)만 하면됨!
    }

    private void initialVariable() {
        Intent intent = getIntent();
        restaurant_name = intent.getExtras().getString("NAME");
        longitude = intent.getExtras().getDouble("LON");
        latitude = intent.getExtras().getDouble("LAT");
        ratingStars = intent.getExtras().getFloat("RATINGSTARS");
        Toast.makeText(getApplicationContext(),Float.toString(ratingStars),Toast.LENGTH_SHORT).show();

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
        ReviewUpload task = new ReviewUpload() {
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
        task.execute(name, userId, ratingStars, contents);
    }
}