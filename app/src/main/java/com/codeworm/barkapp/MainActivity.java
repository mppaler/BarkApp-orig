package com.codeworm.barkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    HomeFragment homeFragment = new HomeFragment();
    AccountFragment accountFragment = new AccountFragment();
    UserManualFragment usermanualFragment = new UserManualFragment();
    ParkingLogFragment parkingLogFragment = new ParkingLogFragment();
    ParkingLogFactory parkingLogFactory = new ParkingLogFactory();
    TextView tvFullname, tvUsername;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment, homeFragment.getTag()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        tvFullname = (TextView) headerView.findViewById(R.id.nav_fullname);
        tvUsername = (TextView) headerView.findViewById(R.id.nav_username);
        tvFullname.setText(SharedPreferencesManager.getInstance(this).getFullname());
        tvUsername.setText(SharedPreferencesManager.getInstance(this).getUsername());

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvFullname.setText(SharedPreferencesManager.getInstance(this).getFullname());
        tvUsername.setText(SharedPreferencesManager.getInstance(this).getUsername());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment, "homefragment").commit();
        } else if (id == R.id.nav_account) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment, "accountfragment").commit();
        } else if (id == R.id.nav_parking_logs) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
            ParkingLogAsyncTask parkingLogAsyncTask = new ParkingLogAsyncTask();
            parkingLogAsyncTask.execute(SharedPreferencesManager.getInstance(this).getUsername());
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.fragment_container, parkingLogFragment, parkingLogFragment.getTag()).commit();
        } else if (id == R.id.nav_user_manual) {
//            Intent intent= new Intent(this, UserManualActivity.class);
//            startActivity(intent);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, usermanualFragment, "usermanualFragment").commit();
            Toast.makeText(this, "User Manual", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class ParkingLogAsyncTask extends AsyncTask<String,Void,Void>{

        List<ParkingLog> mParkingLog = new ArrayList<ParkingLog>();
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String response = postData(params);
            System.out.println("Response is --> " + response);

            try {
                JSONArray jsonArray = new JSONArray(response);

                System.out.println("Inside Response");
                System.out.println("Value of jsonArray " + jsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {
                    // Get current json object
                    JSONObject record = jsonArray.getJSONObject(i);

                    // Get the current student (json object) data
                    String timestamp = record.getString("timestamp");
                    String event = record.getString("event");
                    String parking_area = record.getString("parking_area");
                    String slot_id = record.getString("slot_id");

                    System.out.println("Printing values of each log: ");
                    System.out.println("timestamp : " + timestamp);
                    System.out.println("event : " + event);
                    System.out.println("parking area : " + parking_area);
                    System.out.println("slot id : " + slot_id);

                    //                                ParkingLog parkingLog = new ParkingLog();
                    //                                parkingLog.setTimestamp(timestamp);
                    //                                parkingLog.setEvent(event);
                    //                                parkingLog.setParking_area(parking_area);
                    //                                parkingLog.setSlot_id(slot_id);
                    System.out.println("Add data to ArrayList");
                    //                                System.out.println("Value of parkingLog is " + parkingLog);
                    mParkingLog.add(new ParkingLog(timestamp, event, parking_area, slot_id));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingDialog.dismiss();

            System.out.println("Size of parkingLogList --> " + (parkingLogFactory.getParkingLogList()).size());

            if(mParkingLog.size() != (parkingLogFactory.getParkingLogList()).size()){
                parkingLogFactory.clearParkingLogList();        //CLEAR DATA FROM PARKING LOG LIST
                System.out.println("Size of parkingLogList --> " + (parkingLogFactory.getParkingLogList()).size());
                parkingLogFactory.setParkingLogList(mParkingLog);       //ADD THE NEW SET OF DATA IN PARKING LOG
                System.out.println("Size of parkingLogList --> " + (parkingLogFactory.getParkingLogList()).size());
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, parkingLogFragment, parkingLogFragment.getTag()).commit();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public String postData(String input[]) {
            String response = "";
            try {
                URL url = new URL(Constants.URL_GET_PARKING_LOGS);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", input[0]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();

                int statusCode = httpURLConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    System.out.println("Checkpoint: After Buffered Input Stream");
                    response = convertInputStreamToString(inputStream);

                } else {

                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

    }

    public String convertInputStreamToString(InputStream entityResponse) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = entityResponse.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString();
    }


}
