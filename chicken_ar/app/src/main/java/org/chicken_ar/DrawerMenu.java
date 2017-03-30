package org.chicken_ar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrawerMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DiningDataDownload.Listener, AdapterView.OnItemClickListener, LocationListener {
    ArrayList<DiningInfoListViewItem> listViewItemList;
    List<DiningInfo> diningInfoList;
    private ListView mListView;

    LocationManager locationManager;
    boolean isGPSEnabled = false;
    Location location;
    double lat;
    double lon;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;
    Button buttonAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        getLocation();
        checkNetwork();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        new DiningDataDownload(this).execute(CategoryType.CAFE);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent arIntent = new Intent(getApplicationContext(), AugmentedReality.class);
                    arIntent.putExtra("AR", true);
                    startActivity(arIntent);
                } catch(Exception e) {
                    Log.d("****Exception!", ""+e);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_cafe:
                new DiningDataDownload(this).execute(CategoryType.CAFE);
                break;
            case R.id.nav_dining_korea:
                new DiningDataDownload(this).execute(CategoryType.DINING_KOREA);
                break;
            case R.id.nav_dining_snack:
                new DiningDataDownload(this).execute(CategoryType.DINING_SNACK);
                break;
            case R.id.nav_dining_japanese:
                new DiningDataDownload(this).execute(CategoryType.DINING_JAPANESE);
                break;
            case R.id.nav_dining_chinese:
                new DiningDataDownload(this).execute(CategoryType.DINING_CHINESE);
                break;
            case R.id.nav_dining_western:
                new DiningDataDownload(this).execute(CategoryType.DINING_WESTERN);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent infoActivityIntent = new Intent(getApplicationContext(), InfoActivity.class);
        infoActivityIntent.putExtra("NAME",diningInfoList.get(position).getName());
        infoActivityIntent.putExtra("LON",Double.valueOf(diningInfoList.get(position).getLongitude()));
        infoActivityIntent.putExtra("LAT",Double.valueOf(diningInfoList.get(position).getLatitude()));
        infoActivityIntent.putExtra("RATINGSTARS",listViewItemList.get(position).getRatingStar());
        startActivity(infoActivityIntent);
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
            int calculatedDistance = calculateDistance(37.545201, 126.964885/*myLocation.getLatitude(),myLocation.getLongitude()*/,
                    Double.valueOf(diningInfoList.get(i).getLatitude()),Double.valueOf(diningInfoList.get(i).getLongitude()));
            listViewItem.setDistance(calculatedDistance);
            listViewItemList.add(listViewItem);
        }
        loadListView();
    }


    private int calculateDistance(double myLatitude, double myLongitude, double buildingLatitude, double buildingLongitude) {
        double theta, dist;
        theta = myLongitude - buildingLongitude;
        dist = Math.sin(deg2rad(myLatitude)) * Math.sin(deg2rad(buildingLatitude)) + Math.cos(deg2rad(myLatitude))
                * Math.cos(deg2rad(buildingLatitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return (int)dist;
    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (rad * 180d / Math.PI);
    }

    private void loadListView() {

        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listViewAdapter.initAdapterToDiningInfo(listViewItemList);
        mListView.setAdapter(listViewAdapter);
    }

    public void checkNetwork() {
        ConnectivityManager manager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(wifi.isConnected() || mobile.isConnected())
            ;
        else
            alertIfNetworkOff();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(!isGPSEnabled)
                alertIfGpsOff();
            else
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        alertIfGpsOff();
    }

    public void alertIfGpsOff() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("GPS가 꺼져있습니다.\n" +
                "'위치서비스'에서 'Google 위치 서비스'를 체크해주세요").setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("취소", null).show();
    }

    public void alertIfNetworkOff() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("인터넷이 꺼져있습니다.\n" +
                "Wifi나 LTE를 켜주세요.").setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("취소", null).show();
    }
}
