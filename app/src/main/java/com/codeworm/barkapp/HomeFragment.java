package com.codeworm.barkapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.client.android.Intents;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Button btnScan, btnParkingDetails, btnLocation;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnScan = (Button) view.findViewById(R.id.scan_btn);
        btnParkingDetails = (Button) view.findViewById(R.id.parking_details_btn);
        btnLocation = (Button) view.findViewById(R.id.location_btn);

        if(SharedPreferencesManager.getInstance(getActivity()).getSlotId() != null &&
                SharedPreferencesManager.getInstance(getActivity()).getRackLocation() != null &&
                SharedPreferencesManager.getInstance(getActivity()).getAddress() != null){
            btnParkingDetails.setVisibility(View.VISIBLE);
            btnScan.setVisibility(View.GONE);


        }


        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
            }
        });

        btnParkingDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ParkingDetailsActivity.class);
                startActivity(intent);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
