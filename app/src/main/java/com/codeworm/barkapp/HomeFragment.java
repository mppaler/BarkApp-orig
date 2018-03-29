package com.codeworm.barkapp;

import android.content.Context;
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


}