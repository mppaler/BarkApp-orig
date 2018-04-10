package com.codeworm.barkapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

        //CHECK DATABASE IF USER HAS ALREADY SCANNED DATA
        checkUserStatus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //CHECK DATABASE IF USER HAS ALREADY SCANNED DATA
        checkUserStatus();
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

            android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag("Parking Log");

            System.out.println("Size of parkingLogList --> " + (parkingLogFactory.getParkingLogList()).size());
            int numHolder = parkingLogFactory.getParkingLogList().size();
            if(mParkingLog.size() != (parkingLogFactory.getParkingLogList()).size()){
                parkingLogFactory.clearParkingLogList();        //CLEAR DATA FROM PARKING LOG LIST
                System.out.println("Size of parkingLogList --> " + (parkingLogFactory.getParkingLogList()).size());
                parkingLogFactory.setParkingLogList(mParkingLog);       //ADD THE NEW SET OF DATA IN PARKING LOG
                System.out.println("Size of parkingLogList --> " + (parkingLogFactory.getParkingLogList()).size());
                if(numHolder != 0 && parkingLogFactory.getParkingLogList().size() != 0 && fragment != null && fragment.isVisible()){
                    restartApplication();
                }
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, parkingLogFragment, "Parking Log").commit();
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


    public void checkUserStatus(){
//        loadingDialog = new LoadingDialog(LoginActivity.this);
//        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        loadingDialog.setCancelable(false);
//        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHECK_USER_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();

                            if(jsonObject.getString("message").equals("Success") && SharedPreferencesManager.getInstance(MainActivity.this).getSlotId() == null &&
                                    SharedPreferencesManager.getInstance(MainActivity.this).getRackLocation() == null &&
                                    SharedPreferencesManager.getInstance(MainActivity.this).getAddress() == null){  //User was found to be registered in a rack
                                SharedPreferencesManager.getInstance(getApplicationContext()).setCode(jsonObject.getString("qr_data"));
                                setParkingDetails(jsonObject.getString("qr_data"));
                                restartApplication();

                            }else if(jsonObject.getString("message").equals("Not registered") && SharedPreferencesManager.getInstance(MainActivity.this).getSlotId() != null &&
                                    SharedPreferencesManager.getInstance(MainActivity.this).getRackLocation() != null &&
                                    SharedPreferencesManager.getInstance(MainActivity.this).getAddress() != null){
                                SharedPreferencesManager.getInstance(MainActivity.this).removeParkingDetails();
                                restartApplication();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", SharedPreferencesManager.getInstance(getApplicationContext()).getUsername());
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void setParkingDetails(final String qr_data) {
        System.out.println("Inside setParkingDetails");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PARKING_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                            System.out.println("Getting there...");
                            if(!jsonObject.getBoolean("error")){
                                SharedPreferencesManager.getInstance(getApplicationContext()).setParkingDetails(jsonObject.getString("slot_id"), jsonObject.getString("rack_location"), jsonObject.getString("address"));
                                System.out.println("Value of slot ID that will be transfered in shared preference: " + jsonObject.getString("slot_id"));
                                restartApplication();


                            }else{
                                System.out.println("Napunta sa else");
                                Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_LONG).show();
//                                loadingDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("code", qr_data);
                return params;
            }
        };
        System.out.println("The decision of momshie");
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public void restartApplication(){
        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Application Need to Restart");
        builder.setMessage("Your account's record and our system did not match. The application need to restart.");
        builder.setCancelable(false);
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
