package org.chicken_ar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawerMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LoadJSONTask.Listener, AdapterView.OnItemClickListener {
    ArrayList<DiningInfoListViewItem> listViewItemList;
    List<DiningInfo> diningInfoList;
    private ListView mListView;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        new LoadJSONTask(this).execute(CategoryType.CAFE);

        //맨 마지막에 지워야 함.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
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
                new LoadJSONTask(this).execute(CategoryType.CAFE);
                break;
            case R.id.nav_dining_korea:
                new LoadJSONTask(this).execute(CategoryType.DINING_KOREA);
                break;
            case R.id.nav_dining_snack:
                new LoadJSONTask(this).execute(CategoryType.DINING_SNACK);
                break;
            case R.id.nav_dining_japanese:
                new LoadJSONTask(this).execute(CategoryType.DINING_JAPANESE);
                break;
            case R.id.nav_dining_chinese:
                new LoadJSONTask(this).execute(CategoryType.DINING_CHINESE);
                break;
            case R.id.nav_dining_western:
                new LoadJSONTask(this).execute(CategoryType.DINING_WESTERN);
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
            listViewItem.setDistance(/*calculate distance*/0);
            listViewItemList.add(listViewItem);
        }
        loadListView();
    }

    private void loadListView() {

        ListViewAdapter listViewAdapter = new ListViewAdapter(listViewItemList);
        mListView.setAdapter(listViewAdapter);
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }
}
