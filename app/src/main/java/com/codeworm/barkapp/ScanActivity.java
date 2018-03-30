package com.codeworm.barkapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ScanActivity extends AppCompatActivity {

    String scannedData;
    Button scanBtn;
    boolean flagMatch, flagSuccess;
    LoadingDialog loadingDialog;

    private ArrayList<String> list_qr = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Activity activity =this;
        scanBtn = (Button)findViewById(R.id.scan_btn2);





        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
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
            System.out.println("Value of scannedData is ----->" + scannedData);

            if(scannedData != null){
                validateQR(scannedData);
                validateQR_FB(scannedData);
            }

        }

    }

    private void validateQR_FB(final String input) {
        SharedPreferencesManager.getInstance(getApplicationContext()).setCode(scannedData);

//        final String user=SharedPreferencesManager.getInstance(getApplicationContext()).getUsername();
//        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        DatabaseReference ref = mDatabase.child("Racks").child("001").child("001");
//        DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Racks");
//
//        query.child("001").orderByChild("User").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                    System.out.println("PLEASE " + childSnapshot.getKey() + childSnapshot.child("qrCode").getValue());
//
//                        String qr = childSnapshot.child("qrCode").getValue(String.class);
//                        String User = childSnapshot.child("User").getValue(String.class);
//                        String key = childSnapshot.getKey();
//
//
////                     list_qr.add(qr);
////                    Iterator<String> itr = list_qr.iterator();
////                     while(itr.hasNext()){
////                         String element = itr.next();
////                         System.out.println("POTAS "+ element);
////                     }
//                    System.out.println("KINGINA MO! "+ input);
//                    if (qr.equals(input) && User.isEmpty()) {
//                        String code = input;
//
//                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
//
//                        HashMap<String, Object> result_final = new HashMap<>();
//                        result_final.put(childSnapshot.getKey(), user);
//                        String refKey = childSnapshot.getKey();
//                        System.out.println("POTAS2 " + refKey);
//                        System.out.println("POKE " + childSnapshot.getKey() + childSnapshot.child("User").getValue() + childSnapshot.child("qrCode").getValue() + input);
//                        dbref.child("Racks").child("001").child(refKey).child("User").setValue(user);
//                        validateQR(input);
//                        System.out.println("ETO NA BOI" + result_final);
//                        Toast.makeText(ScanActivity.this, "FIREBASE ACCEPTED", Toast.LENGTH_SHORT).show();
//
//
//                    }
//
//                    else {
//                        //
//                    }
//
//
//
//                }
//
//                System.out.println("PLS" + dataSnapshot.getValue());
////                 System.out.println("POTA KA" + scannedData + qr);
////                     if(qr.equals(scannedData) && User.isEmpty()) {
////                         mDatabase.child("Racks").child("001").child("001").child("User").setValue(user);
////                         Toast.makeText(ScanActivity.this, "FIREBASE ACCEPTED", Toast.LENGTH_SHORT).show();
////                         System.out.println("POTA KA" + scannedData + qr);
////                     } else {
////                         Toast.makeText(ScanActivity.this, "FIREBASE DENIED", Toast.LENGTH_SHORT).show();
////                     }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();

                            if(jsonObject.getString("type").equals("Success")){


                                System.out.println("NAKAPASOK AKO");
                                flagMatch = true;
                                validateStatus(jsonObject.getString("slot_id").toString());
//                                setParkingDetails();
//                                finish();
//                                openParkingDetails();
                                //SharedPreferencesManager.getInstance(getApplicationContext()).loginUser(jsonObject.getString("fullname"), jsonObject.getString("username"), jsonObject.getString("mobilenum"), jsonObject.getInt("id"));

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
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();

                            if(jsonObject.getString("type").equals("Success")){

                                System.out.println("NAKAPASOK AKO");


                                if(jsonObject.getString("slot_status").equals("vacant") && jsonObject.getString("user_type").equals("unregistered")){
                                    //ONLY OCCUPIED IS ALLOWED
                                    loadingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "NO BICYCLE IS PARKED", Toast.LENGTH_LONG).show();

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
//                                setParkingDetails();
//                                finish();
//                                openParkingDetails();
                                //SharedPreferencesManager.getInstance(getApplicationContext()).loginUser(jsonObject.getString("fullname"), jsonObject.getString("username"), jsonObject.getString("mobilenum"), jsonObject.getInt("id"));

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
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                System.out.println("Value of slotID is ---> " + slot_id);
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

        query.child("Location").orderByChild("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("IM HERE ITS OKAY");
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("PLEASE " + childSnapshot.getKey() + childSnapshot.child("qrCode").getValue());

                    String qr = childSnapshot.child("qrCode").getValue(String.class);
                    String User = childSnapshot.child("User").getValue(String.class);
                    String key = childSnapshot.getKey();


                    if (key.equals(slot_id) && User.isEmpty()) {
                        System.out.println("GUMANA NA AMP "+ key + slot_id);

                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> result_final = new HashMap<>();
                        //result_final.put(childSnapshot.getKey(), user);
                        result_final.put("User", user);

                        String refKey = childSnapshot.getKey();
                        System.out.println("POTAS2 " + refKey);
                        dbref.child("locs").child("Location").child(refKey).updateChildren(result_final);
//                        dbref.child("Racks").child("001").child(refKey).child("User").setValue(user);
                        System.out.println("ETO NA BOI" + result_final);
                        Toast.makeText(ScanActivity.this, "FIREBASE ACCEPTED", Toast.LENGTH_SHORT).show();

                    }else if(key.equals(slot_id) && User.equals("unregistered")){
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> result_final = new HashMap<>();
//                        result_final.put(childSnapshot.getKey(), user);
                        result_final.put("User", user);
                        String refKey = childSnapshot.getKey();
                        System.out.println("POTAS2 " + refKey);
                        dbref.child("locs").child("Location").child(refKey).updateChildren(result_final);
//                        dbref.child("Racks").child("001").child(refKey).child("User").setValue(user);
                        System.out.println("ETO NA BOI" + result_final);
                        Toast.makeText(ScanActivity.this, "FIREBASE ACCEPTED", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        //
                    }



                }

                System.out.println("PLS" + dataSnapshot.getValue());
//                 System.out.println("POTA KA" + scannedData + qr);
//                     if(qr.equals(scannedData) && User.isEmpty()) {
//                         mDatabase.child("Racks").child("001").child("001").child("User").setValue(user);
//                         Toast.makeText(ScanActivity.this, "FIREBASE ACCEPTED", Toast.LENGTH_SHORT).show();
//                         System.out.println("POTA KA" + scannedData + qr);
//                     } else {
//                         Toast.makeText(ScanActivity.this, "FIREBASE DENIED", Toast.LENGTH_SHORT).show();
//                     }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setParkingDetails() {
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


                            }else{
                                System.out.println("Napunta sa else");
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
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("code", scannedData);
                return params;
            }
        };
        System.out.println("The decision of momshie");
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//        decision = validationFlag.isCheckUser();
//        System.out.print("The decision of momshie is " + decision);

    }

    private void updateGeneralLog(final String slot_id, final String username, final String status) {
        System.out.println("Inside setParkingStatus");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_GENERAL_LOG,
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
                                Toast.makeText(getApplicationContext(), "Updated USER_TYPE and USERNAME", Toast.LENGTH_LONG).show();

                            }else{
                                System.out.println("Napunta sa else");
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
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
        System.out.println("The decision of momshie");
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//        decision = validationFlag.isCheckUser();
//        System.out.print("The decision of momshie is " + decision);

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

}
