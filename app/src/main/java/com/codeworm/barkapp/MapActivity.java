package com.codeworm.barkapp;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codeworm.barkapp.m_UI.ParkingListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.codeworm.barkapp.activity.PlaceInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.codeworm.barkapp.R.id.address;
import static com.codeworm.barkapp.R.id.map;
import static java.lang.System.in;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    LocationManager locationManager;
    private Button mShowList;
    private Button mShowListSearch;
    private ListView mTotalList; //LV
    private ListView mLocNameList; //LV

    private ArrayList<String>mKeys= new ArrayList<>();

    private ArrayList<Integer> mTotals = new ArrayList<>();
    private ArrayList<String> mLocName = new ArrayList<>();

    //    private ArrayList<String, Integer> mLocationName = new ArrayList<String, Integer>();
    private TextView mTotalView;
    private TextView mSample;
    private TextView mLocView;
    private TextView mStatView;
    private DatabaseReference myRef;
    // private FirebaseListAdapter<LocationModel> mAdapter;
    private MapActivity mapActivity;
    public Criteria critera;
    public String bestProvider;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference2 = database.getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/");
//   DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://maps--places-d166.firebaseio.com/users");





    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

//        mTotalView = (TextView) findViewById(R.id.slot);
        mSample = (TextView) findViewById(R.id.text1);
//        mTotalList  = (ListView) findViewById(R.id.list_slot);
        mLocNameList  = (ListView) findViewById(R.id.list_locname);
        // mStatView = (TextView) findViewById(R.id.slot_no);

//LV
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mLocName);
//
////
//        mLocNameList.setAdapter(arrayAdapter);
//        mLocNameList.setVisibility(View.GONE);
//        arrayAdapter.notifyDataSetChanged();
//
//        final ArrayAdapter<Integer> arrayAdapter2 = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, mTotals);
//
////
//        mTotalList.setAdapter(arrayAdapter2);
//        mTotalList.setVisibility(View.GONE);
//        arrayAdapter2.notifyDataSetChanged();
//LV END






        if (!isLocationEnabled()) {
            showAlert(1);
            System.out.println("ANG LOCATION AY" + isLocationEnabled());
            if (isLocationEnabled()) {
                if (mLocationPermissionsGranted) {
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);

                    init();
                }
            }
        } else if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }

//             mAdapter = new FirebaseListAdapter<LocationModel>(this, LocationModel.class, R.layout.view_item, databaseReference ) {
//            @Override
//            protected void populateView(View v, LocationModel data, int position) {
//                // Bind the Chat to the view
//                // ...
//
//                String name = data.getName();
//                int ratio = data.getRatio();
//
//                TextView text1 = (TextView) v.findViewById(R.id.text_name);
//                TextView text2  = (TextView)  v.findViewById(R.id.text_ratio);
//                text1.setText(data.getName());
//                text2.setText(String.valueOf(data.getRatio()));
//                System.out.println("PLS "+data.getName());
//                System.out.println("PLS2 "+data.getRatio());
//                System.out.println("PLS "+name);
//                System.out.println("PLS2 "+ratio);
//            }
//        };
//
//
//        mTotalList.setAdapter(mAdapter);
//        FirebaseListOptions<LocationModel> options = new FirebaseListOptions.Builder<LocationModel>()
//                .setQuery(query, LocationModel.class)
//                .build();

//        FirebaseListAdapter<LocationModel>firebaseListAdapter = new FirebaseListAdapter<LocationModel>(
//                this,
//                LocationModel.class,
//                android.R.layout.simple_list_item_2,
//                databaseReference
//        ){
//
//            @Override
//            protected void populateView(View v, LocationModel model,  int position) {
//
//                TextView textView = (TextView) v.findViewById(android.R.id.text1);
//                TextView textView2 = (TextView) v.findViewById(android.R.id.text2);
//                System.out.println("GANA NA PLS" + model.getName());
//
//                textView.setText(model.getName());
//                textView.setText(model.getAge());
//
//
////                ((TextView)v.findViewById(android.R.id.text1)).setText((model.getLocName()));
//
//
//
//            }
//        };
//
//
//        mTotalList.setAdapter(firebaseListAdapter);






//        mAdapter = new FirebaseListAdapter<LocationModel>(this, LocationModel.class, android.R.layout.simple_list_item_1, databaseReference ) {
//            @Override
//            protected void populateView(View v, LocationModel data, int position) {
//                // Bind the Chat to the view
//                // ...
//                ((TextView)v.findViewById(android.R.id.text1)).setText((data.getName()));
//            }
//        };
//
//
//        mTotalList.setAdapter(mAdapter);
//        FirebaseListOptions<LocationModel> options = new FirebaseListOptions.Builder<LocationModel>()
//                .setQuery(databaseReference, LocationModel.class).setLayout(android.R.layout.simple_list_item_2).build();
//
//        FirebaseListAdapter<LocationModel> adapter = new FirebaseListAdapter<LocationModel>(options) {
//            @Override
//            protected void populateView(View v, LocationModel model, int position) {
//                // Get references to the views of message.xml
//                TextView messageText = v.findViewById(R.id.text1);
//                TextView messageUser = v.findViewById(R.id.text2);
//
//
//                // Set their text
//                messageText.setText(model.getName());
//                messageUser.setText(model.getAge());
//
//                // Format the date before showing it
//
//            }
//        };
//        mTotalList.setAdapter(adapter);

        databaseReference2.child("locs").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                LatLng newLocation = new LatLng(
                        dataSnapshot.child("lat").getValue(Double.class),
                        dataSnapshot.child("lng").getValue(Double.class)
                );

//                LocationModel name = dataSnapshot.child("name").getValue(LocationModel.class);
//                LocationModel ratio = dataSnapshot.child("ratio").getValue(LocationModel.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                Integer ratio = dataSnapshot.child("ratio").getValue(Integer.class);
//                System.out.println("OGAG"+newLocation);

                String key = dataSnapshot.getKey();

                mLocName.add(name);
                mTotals.add(ratio);
                mKeys.add(key);
                System.out.println("PUTA PLS " + ratio);

                mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .title(name));
                System.out.println("OY" + ratio +key);



            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                LatLng newLocation2 = new LatLng(
                        dataSnapshot.child("lat").getValue(Double.class),
                        dataSnapshot.child("lng").getValue(Double.class)
                );
                System.out.println("DSDS " +dataSnapshot.child("ratio").getValue());

                String key = dataSnapshot.getKey();
                String name = dataSnapshot.child("name").getValue(String.class);
                Integer ratio = dataSnapshot.child("ratio").getValue(Integer.class);
                Integer total = dataSnapshot.child("total").getValue(Integer.class);

                int index = mKeys.indexOf(key);
                System.out.println("ratio" +ratio);








                mMap.addMarker(new MarkerOptions()
                        .position(newLocation2)
                        .title(name));






                System.out.println("TANGINA MO MARIAH " +  dataSnapshot.child("ratio").getValue());

            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {



            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }






    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo;


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Double> coordinates = new ArrayList<Double>();
    private ArrayList<LatLng> coor = new ArrayList<LatLng>();



    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.v("Example", "onCreate");
        getIntent().setAction("Already created");
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //       .findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mInfo = (ImageView) findViewById(R.id.place_info);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //ListView mTotalList = (ListView) findViewById(R.id.total_slot);

        getLocationPermission();

//  LV
//        databaseReference2.child("locs").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    Double lat = ds.child("lat").getValue(Double.class);
//                    Double lng = ds.child("lng").getValue(Double.class);
//
//                    LatLng newLocation2 = new LatLng(lat, lng);
//                    String key = dataSnapshot.getKey();
//
//
//
//                    MarkerOptions a = new MarkerOptions()
//                            .position(newLocation2)
//                            .title(key);
//
//
//
//                    Marker myMarker = mMap.addMarker(a);
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
    }



    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.
                    getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()&& task.getResult() != null){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{

                            Log.d(TAG, "onComplete: current location is null");
                            getDeviceLocation();


                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

//    private LocationListener listener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    };


    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();




//        latlngs.add(new LatLng(14.610278, 120.98916699999995)); //some latitude and logitude value
//        latlngs.add(new LatLng(14.610727,120.9901791));
//        latlngs.add(new LatLng(14.656891,121.020967));
//        for (LatLng point : latlngs) {
//            options.position(point);
//            options.title(placeInfo.getName());
//            options.snippet("blach");
//            mMap.addMarker(options);
//        }
//        coordinates.add(new LatLng(14.610278, 120.98916699999995)); //some latitude and logitude value
//        coordinates.add(new LatLng(14.610727,120.9901791));
//        coordinates.add(new LatLng(14.656891,121.020967));
////        for (LatLng point : coordinates) {
////            options.position(point);
////            options.title("TITLE");
////            options.snippet("blach");
////            mMap.addMarker(options);
////        }
//
//
////        options.position(coordinates);
////        mMap.addMarker(options);
//        for(LatLng point : coordinates){
//            options.position(point);
//            mMap.addMarker(options);
//        }

        if(placeInfo != null){

            try{
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                /** MarkerOptions options = new MarkerOptions()
                 .position(latLng)
                 .title(placeInfo.getName())
                 .snippet(snippet);
                 mMarker = mMap.addMarker(options); **/

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);


        }

        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);

        mapFragment.getMapAsync(MapActivity.this);
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
        --------------------------- google places API autocomplete suggestions -----------------
     */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();
        }
    };
    private void showAlert(final int status) {

        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);


                        } else {
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }
    @Override
    protected void onResume() {

        Log.v("Example", "onResume");

        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);
        super.onResume();




        System.out.println("ONRESUME");
    }

    public void fabBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), ParkingListActivity.class);
                startActivity(intent);
        //try lang commit to github
    }
}
