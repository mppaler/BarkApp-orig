package com.codeworm.barkapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ScanActivity extends AppCompatActivity {

    String scannedData;
    Button scanBtn;
    boolean flagMatch, flagSuccess;
    LoadingDialog loadingDialog;
    Toolbar toolbar;

    private ArrayList<String> list_qr = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Activity activity =this;
        scanBtn = (Button)findViewById(R.id.scan_btn2);
        toolbar = (Toolbar) findViewById(R.id.appToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.background_light), PorterDuff.Mode.SRC_ATOP);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result!=null) {
            scannedData = result.getContents();

            if(scannedData != null){
                validateQR(scannedData);
            }

        }

    }

    public void validateQR(final String inputData) {
        loadingDialog = new LoadingDialog(ScanActivity.this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SCANNED_QR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("type").equals("Success")){
                                flagMatch = true;
                                validateStatus(jsonObject.getString("slot_id").toString());

                            }else{
                                Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_LONG).show();
                                flagMatch = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("code", inputData);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    public void validateStatus(final String slot_id ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHECK_PARKING_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("type").equals("Success")){

                                if(jsonObject.getString("slot_status").equals("vacant") && jsonObject.getString("user_type").equals("unregistered")){
                                    //ONLY OCCUPIED IS ALLOWED
                                    loadingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "No bicycle detected. Please park your bicycle first.", Toast.LENGTH_LONG).show();

                                }
                                else if(jsonObject.getString("slot_status").equals("vacant") && jsonObject.getString("user_type").equals("registered")){
                                    //IMPOSSIBLE RESULT
                                    loadingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "IMPOSSIBLE RESULT", Toast.LENGTH_LONG).show();
                                }
                                else if(jsonObject.getString("slot_status").equals("occupied") && jsonObject.getString("user_type").equals("unregistered")){
                                    //AVAILABLE FOR QR SCANNING
                                    registerUser(slot_id);
                                    setParkingDetails();    //SET PARKING DETAILS IN SHARED PREFERENCE
                                    updateGeneralLog(slot_id, SharedPreferencesManager.getInstance(getApplicationContext()).getUsername(), Constants.UPDATE_GENERAL_LOG_UPDATE);  //UPDATE USER_TYPE AND SET USERNAME IN GENERAL_LOG
                                    loadingDialog.dismiss();
                                    openParkingDetails();

                                }
                                else if(jsonObject.getString("slot_status").equals("occupied") && jsonObject.getString("user_type").equals("registered")){
                                    //SLOT IS ALREADY TAKEN
                                    loadingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "THIS SLOT IS ALREADY TAKEN", Toast.LENGTH_LONG).show();
                                }
                            }
                            else if(jsonObject.getString("type").equals("Failed")){
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                flagMatch = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("slot_id", slot_id);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void registerUser(final String slot_id){
        SharedPreferencesManager.getInstance(getApplicationContext()).setCode(scannedData);
        final String user=SharedPreferencesManager.getInstance(getApplicationContext()).getUsername();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        DatabaseReference query = FirebaseDatabase.getInstance().getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/").child("locs");

        query.orderByKey().limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    final String refKey = ds.getKey();

                    DatabaseReference query2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/").child("locs");
                    query2.child(refKey).orderByKey().limitToFirst(4).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                String User = childSnapshot.child("user").getValue(String.class);
                                String key = childSnapshot.getKey();


                                if (key.equals(slot_id) && User.isEmpty()) {
                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

                                    HashMap<String, Object> result_user = new HashMap<>();
                                    result_user.put("user", user);


                                    String childKey = childSnapshot.getKey();
                                    dbref.child("locs").child(refKey).child(childKey).updateChildren(result_user);

                                } else if (key.equals(slot_id) && User.equals("unregistered")) {
                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

                                    HashMap<String, Object> result_final = new HashMap<>();

                                    result_final.put("user", user);
                                    String childKey = childSnapshot.getKey();
                                    dbref.child(childKey).updateChildren(result_final);


                                } else {

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError){

                }
        });
    }



    private void setParkingDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PARKING_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                SharedPreferencesManager.getInstance(getApplicationContext()).setParkingDetails(jsonObject.getString("slot_id"), jsonObject.getString("rack_location"), jsonObject.getString("address"));
                                System.out.println("Value of slot ID that will be transfered in shared preference: " + jsonObject.getString("slot_id"));


                            }else{
                                Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("code", scannedData);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void updateGeneralLog(final String slot_id, final String username, final String status) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_GENERAL_LOG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){
                                Toast.makeText(getApplicationContext(), "Updated USER_TYPE and USERNAME", Toast.LENGTH_LONG).show();

                            }else{

                                Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("slot_id", slot_id);
                params.put("username", username);
                params.put("status", status);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    public void openParkingDetails(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("You have been registered. Would you like to proceed to Parking Details?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(ScanActivity.this, ParkingDetailsActivity.class));
            }
        });
        alertDialog.setNegativeButton("Go to Homepage", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(ScanActivity.this, MainActivity.class));
            }
        });

        alertDialog.create().show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
