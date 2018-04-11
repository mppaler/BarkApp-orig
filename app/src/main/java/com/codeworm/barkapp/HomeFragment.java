package com.codeworm.barkapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.client.android.Intents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Button btnScan, btnParkingDetails, btnLocation;
    private BottomNavigationView navParkingDetails, navScan, navRack;
    LoadingDialog loadingDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);


        navParkingDetails = (BottomNavigationView) view.findViewById(R.id.navigation_parking_details);
        navScan = (BottomNavigationView) view.findViewById(R.id.navigation_scan);
        navRack = (BottomNavigationView) view.findViewById(R.id.navigation_location);

        navParkingDetails.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navScan.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navRack.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


//        btnScan = (Button) view.findViewById(R.id.scan_btn);
//        btnParkingDetails = (Button) view.findViewById(R.id.parking_details_btn);
//        btnLocation = (Button) view.findViewById(R.id.location_btn);



        if(SharedPreferencesManager.getInstance(getActivity()).getSlotId() != null &&
                SharedPreferencesManager.getInstance(getActivity()).getRackLocation() != null &&
                SharedPreferencesManager.getInstance(getActivity()).getAddress() != null){
            navParkingDetails.setVisibility(View.VISIBLE);
            navScan.setVisibility(View.GONE);
        }else{
            navParkingDetails.setVisibility(View.GONE);
            navScan.setVisibility(View.VISIBLE);
        }


//        btnScan.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), ScanActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnParkingDetails.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), ParkingDetailsActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnLocation.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), MapActivity.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //CHECK DATABASE IF USER HAS ALREADY SCANNED DATA
        checkUserStatus();

        if(SharedPreferencesManager.getInstance(getActivity()).getSlotId() != null &&
                SharedPreferencesManager.getInstance(getActivity()).getRackLocation() != null &&
                SharedPreferencesManager.getInstance(getActivity()).getAddress() != null){
            navParkingDetails.setVisibility(View.VISIBLE);
            navScan.setVisibility(View.GONE);
        }else{
            navParkingDetails.setVisibility(View.GONE);
            navScan.setVisibility(View.VISIBLE);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_parking_details:
                    intent = new Intent(getActivity(), ParkingDetailsActivity.class);
                    startActivity(intent);

                    return true;
                case R.id.navigation_scan:
                    System.out.println("Scan was clicked");
                    intent = new Intent(getActivity(), ScanActivity.class);
                    startActivity(intent);

                    return true;

                case R.id.navigation_location:
                    intent = new Intent(getActivity(), MapActivity.class);
                    startActivity(intent);

                    return true;
            }
            return false;
        }

    };

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

                            if(jsonObject.getString("message").equals("Success") && SharedPreferencesManager.getInstance(getActivity()).getSlotId() == null &&
                                    SharedPreferencesManager.getInstance(getActivity()).getRackLocation() == null &&
                                    SharedPreferencesManager.getInstance(getActivity()).getAddress() == null){  //User was found to be registered in a rack
                                SharedPreferencesManager.getInstance(getActivity().getApplicationContext()).setCode(jsonObject.getString("qr_data"));
                                setParkingDetails(jsonObject.getString("qr_data"));
                                restartApplication();

                            }else if(jsonObject.getString("message").equals("Not registered") && SharedPreferencesManager.getInstance(getActivity()).getSlotId() != null &&
                                    SharedPreferencesManager.getInstance(getActivity()).getRackLocation() != null &&
                                    SharedPreferencesManager.getInstance(getActivity()).getAddress() != null){
                                SharedPreferencesManager.getInstance(getActivity()).removeParkingDetails();
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
                params.put("username", SharedPreferencesManager.getInstance(getActivity().getApplicationContext()).getUsername());
                return params;
            }
        };

        RequestHandler.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
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
                                SharedPreferencesManager.getInstance(getActivity().getApplicationContext()).setParkingDetails(jsonObject.getString("slot_id"), jsonObject.getString("rack_location"), jsonObject.getString("address"));
                                System.out.println("Value of slot ID that will be transfered in shared preference: " + jsonObject.getString("slot_id"));
                                restartApplication();


                            }else{
                                System.out.println("Napunta sa else");
                                Toast.makeText(getActivity().getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_LONG).show();
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
        RequestHandler.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public void restartApplication(){
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        builder.setTitle("Application Need to Restart");
        builder.setMessage("Your account's record and our system did not match. The application need to restart.");
        builder.setCancelable(false);
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}