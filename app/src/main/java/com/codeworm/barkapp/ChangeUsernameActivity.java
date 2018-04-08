package com.codeworm.barkapp;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUsernameActivity extends AppCompatActivity {
    TextView txtUsername;
    EditText editNewUsername;
    Button btnSubmit, btnCancel;
    String sUsername, sOldUsername;
    LoadingDialog loadingDialog;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        toolbar = (Toolbar) findViewById(R.id.appToolbar);
        txtUsername = (TextView) findViewById(R.id.textUsername);
        editNewUsername = (EditText) findViewById(R.id.editNewUsername);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        loadingDialog = new LoadingDialog(ChangeUsernameActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.background_light), PorterDuff.Mode.SRC_ATOP);
        txtUsername.setText(SharedPreferencesManager.getInstance(this).getUsername());
        sOldUsername = SharedPreferencesManager.getInstance(this).getUsername();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sUsername = editNewUsername.getText().toString();
                if(validate() && checkUsername(sUsername)){
                    changeUsername();

                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT);

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public boolean validate(){
        boolean valid = true;

        if(sUsername.isEmpty()){
            valid = false;
            editNewUsername.setError("Please enter a username");
        }
        else{
            valid = true;
        }

        return valid;
    }

    public boolean checkUsername(final String sUsername){
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        final boolean[] valid = {true};
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHECK_USERNAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                            System.out.println("Getting there...");

                            if(jsonObject.getString("type").equals("Username already taken")){
                                loadingDialog.dismiss();
                                System.out.println("Getting there...YAS");
                                valid[0] = false;
                            }else{
                                System.out.println("Getting there... LOL");
                                valid[0] = true;
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", sUsername);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        return valid[0];
    }

    public void changeUsername(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHANGE_USERNAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("error").equals("false")){
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                                SharedPreferencesManager.getInstance(getApplicationContext()).setUsername(sUsername);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            }else{
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();

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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", sUsername);
                params.put("oldusername", sOldUsername);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
