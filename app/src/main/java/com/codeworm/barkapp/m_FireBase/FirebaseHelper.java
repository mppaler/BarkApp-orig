package com.codeworm.barkapp.m_FireBase;

import android.location.Location;
import android.util.Log;

import com.codeworm.barkapp.m_Model.LocationModel;
import com.codeworm.barkapp.m_Model.Spacecraft;
import com.codeworm.barkapp.m_UI.ParkingListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;



/**
 * Created by Mariah on 11/03/2018.
 */

public class FirebaseHelper {

    ParkingListAdapter adapter;
    DatabaseReference db;
    ArrayList<LocationModel> locationModels=new ArrayList<>();
    ArrayList<String>mKeys= new ArrayList<>();

    /*
 PASS DATABASE REFRENCE
  */
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }

    //IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot) {


            LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);

            System.out.println("POPO"+locationModel);
            String key = dataSnapshot.getKey();
            mKeys.add(key);



    }

    //RETRIEVE
    public ArrayList<LocationModel> retrieve()
    {
        db.child("locs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
                fetchData(dataSnapshot);

                locationModels.add(locationModel);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
                String key = dataSnapshot.getKey();
                int index=mKeys.indexOf(key);
                locationModels.set(index, locationModel);


//
//                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
//                locationModel.setKey(dataSnapshot.getKey());
//
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    if(ds.getKey().equals("name")){
//                        locationModel.setName(ds.getValue(String.class));
//                        System.out.println("ANDYS NAME "+ ds.getKey()+ds.getValue(String.class));
//                    } else if (ds.getKey().equals("ratio")){
//                        locationModel.setRatio(ds.getValue(Long.class));
//                        System.out.println("ANDYS RATIO "+ ds.getKey()+ds.getValue(Long.class));
//                    } else if(ds.getKey().equals("total_slot")){
//                        locationModel.setTotal_slot(ds.getValue(Long.class));
//                        System.out.println("ANDYS TOTAL SLOT "+ ds.getKey()+ds.getValue(Long.class));
//                    } else if(ds.getKey().equals("lat")){
//                        locationModel.setLat(ds.getValue(Long.class));
//                        System.out.println("ANDYS LAT "+ ds.getKey()+ds.getValue(Long.class));
//                    } else if(ds.getKey().equals("lng")){
//                        locationModel.setLng(ds.getValue(Long.class));
//                        System.out.println("ANDYS LNG "+ ds.getKey()+ds.getValue(Long.class));
//                    }
//                    adapter.update(locationModel, locationModel.getName());
//
//
//                }




            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index=mKeys.indexOf(key);
                locationModels.remove(index);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return locationModels;
    }


}
