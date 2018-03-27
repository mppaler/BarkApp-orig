package com.codeworm.barkapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codeworm.barkapp.m_Model.LocationModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.codeworm.barkapp.R.id.slot_id;


public class ParkingDetailsActivity extends AppCompatActivity {
    TextView tvParkingArea, tvSlotId, tvAddress;
    String scannedData;
    Button claimBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference db = database.getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/");


    DatabaseReference query = FirebaseDatabase.getInstance().getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/").child("locs");

    ArrayList<String> mKeys= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);

        final Activity activity = this;
        tvParkingArea = (TextView) findViewById(R.id.parking_area);
        tvSlotId = (TextView) findViewById(slot_id);
        tvAddress = (TextView) findViewById(R.id.address);
        claimBtn = (Button)findViewById(R.id.scan_claim_btn);


        tvSlotId.setText(SharedPreferencesManager.getInstance(this).getSlotId());
        tvParkingArea.setText(SharedPreferencesManager.getInstance(this).getRackLocation());
        tvAddress.setText(SharedPreferencesManager.getInstance(this).getAddress());

//        initialize(); //Initialize elements in activity
//        setParkingDetails();

        claimBtn.setOnClickListener(new View.OnClickListener() {
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
        System.out.println("Value of slot ID is: " + SharedPreferencesManager.getInstance(this).getSlotId() + "ako ba to??");



//            db.child("locs").child("Location1").addChildEventListener(new ChildEventListener() {
//
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    String status = dataSnapshot.child("1001").child("status").getValue(String.class);
//                    String key = dataSnapshot.getKey();
//                    int index = mKeys.indexOf(key);
//                    System.out.println("HERES WHATS CHANGED " + status);
//
//
//                }
//
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        String key = dataSnapshot.getKey();
//                        int index = mKeys.indexOf(key);
//                        String status = dataSnapshot.child("1001").child("status").getValue(String.class);
//                        String user= dataSnapshot.child("1001").child("user").getValue(String.class);
//                    if(user.equals("onejuan") && status.equals("vacant")){
//                        Toast.makeText(getApplicationContext(), "SEND SMS HERE", Toast.LENGTH_SHORT).show();
//                    }
//                        System.out.println("HERES WHATS CHANGED " + status);
//
//
//
//
//
//                }
//
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            scannedData = result.getContents();
            System.out.println("Value of scannedData is ------> " + scannedData);

            System.out.println("Value of qr in shared preference is ------> " +SharedPreferencesManager.getInstance(this).getCode());
            if(SharedPreferencesManager.getInstance(this).getCode().equals(scannedData)){

                updateGeneralLog(SharedPreferencesManager.getInstance(getApplicationContext()).getSlotId(), SharedPreferencesManager.getInstance(getApplicationContext()).getUsername(), Constants.UPDATE_GENERAL_LOG_REMOVE);

            }
            else{
                Toast.makeText(this, "Code is invalid", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(this, "Code is invalid", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void updateGeneralLog(final String slot_id, final String username, final String status) {
        System.out.println("Inside setParkingStatus" );
        System.out.println("Value of param:slot_id --> " + slot_id);
        System.out.println("Value of param:username --> " + username);
        System.out.println("Value of param:status --> "+ status);
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
                                SharedPreferencesManager.getInstance(getApplicationContext()).removeParkingDetails();
                                unregisterUser(slot_id, username,status);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

    }

    private void unregisterUser(final String slot_id, final String username, final String status) {
        SharedPreferencesManager.getInstance(getApplicationContext()).setCode(scannedData);
        final String user=SharedPreferencesManager.getInstance(getApplicationContext()).getUsername();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        DatabaseReference query = FirebaseDatabase.getInstance().getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/").child("locs");

        query.orderByKey().limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    final String refKey = ds.getKey();
                    System.out.println("KEYS " + refKey);

                    DatabaseReference query2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/").child("locs");
                    query2.child(refKey).orderByKey().limitToFirst(4).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                String User = childSnapshot.child("user").getValue(String.class);
                                String key = childSnapshot.getKey();


                                if (key.equals(slot_id) && User.equals(user)) {
                                    System.out.println("GUMANA NA AMP " + key + slot_id);

                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

                                    HashMap<String, Object> result_user = new HashMap<>();
                                    //result_final.put(childSnapshot.getKey(), user);
                                    result_user.put("user", "");


                                    String childKey = childSnapshot.getKey();
                                    System.out.println("POTAS2 " + childKey);
                                    dbref.child("locs").child(refKey).child(childKey).updateChildren(result_user);

                                } else {
                                    //
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

//    private void checkStatus(){
//        query.orderByKey().limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    String refKey = ds.getKey();
//                    System.out.println("KEYS " + refKey);
//
//                    DatabaseReference query2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/").child("locs");
//                    query2.child(refKey).orderByKey().limitToFirst(4).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
//                                final String user = SharedPreferencesManager.getInstance(getApplicationContext()).getUsername();
//
//
//                                String key = ss.getKey();
//                                int index = mKeys.indexOf(key);
////
//                                String User = dataSnapshot.child(key).child("user").getValue(String.class);
//                                String status = dataSnapshot.child(key).child("status").getValue(String.class);
//                                System.out.println("HERES WHAT CHANGEDs " + index + key + user + status + slot_id);
//
//
//                                if (User.equals(user) && status.equals("vacant")) {
//
//                                    System.out.println("PUNTA DTIO!" + User + key + user + status);
//
//                                    Toast.makeText(getApplicationContext(), "SEND SMS HERE", Toast.LENGTH_SHORT).show();
//                                } else {
//
//                                }
//
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }










}
