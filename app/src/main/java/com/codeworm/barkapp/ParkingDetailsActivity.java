package com.codeworm.barkapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;


public class ParkingDetailsActivity extends AppCompatActivity {
    TextView tvParkingArea, tvSlotId, tvAddress;
    String scannedData;
    Button claimBtn;
    LoadingDialog loadingDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);

        final Activity activity = this;
        tvParkingArea = (TextView) findViewById(R.id.parking_area);
        tvSlotId = (TextView) findViewById(R.id.slot_id);
        tvAddress = (TextView) findViewById(R.id.address);
        claimBtn = (Button)findViewById(R.id.scan_claim_btn);
        toolbar = (Toolbar) findViewById(R.id.appToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.background_light), PorterDuff.Mode.SRC_ATOP);
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
                integrator.setPrompt("");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        System.out.println("Value of slot ID is: " + SharedPreferencesManager.getInstance(this).getSlotId() + "ako ba to??");
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
        loadingDialog = new LoadingDialog(ParkingDetailsActivity.this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
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
                                loadingDialog.dismiss();
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
                                    dbref.child("locs").child(refKey).child(childKey).updateChildren(result_user);

                                    System.out.println("KEYS2 " + refKey + childKey);


//                        dbref.child("Racks").child("001").child(refKey).child("User").setValue(user);

                                    Toast.makeText(ParkingDetailsActivity.this, "FIREBASE ACCEPTED", Toast.LENGTH_SHORT).show();

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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
