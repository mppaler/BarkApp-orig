package com.codeworm.barkapp;

import android.content.Intent;
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

public class ChangeFullnameActivity extends AppCompatActivity {
    TextView txtFullname;
    EditText editNewFullname;
    Button btnSubmit, btnCancel;
    String sFullname, sOldFullname;
    LoadingDialog loadingDialog;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_fullname);

        toolbar = (Toolbar) findViewById(R.id.appToolbar);
        txtFullname = (TextView) findViewById(R.id.txt_full_name);
        editNewFullname = (EditText) findViewById(R.id.editNewFullname);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        loadingDialog = new LoadingDialog(ChangeFullnameActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.background_light), PorterDuff.Mode.SRC_ATOP);
        txtFullname.setText(SharedPreferencesManager.getInstance(this).getFullname());
        sOldFullname = SharedPreferencesManager.getInstance(this).getFullname();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sFullname = editNewFullname.getText().toString();
                if(validate()){
                    changeFullname();
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

        if(sFullname.isEmpty()){
            valid = false;
            editNewFullname.setError("Please enter your full name");
        }
        else{
            valid = true;
        }

        return valid;
    }

    public void changeFullname(){
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHANGE_FULLNAME,
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
                                SharedPreferencesManager.getInstance(getApplicationContext()).setFullname(sFullname);
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
                params.put("fullname", sFullname);
                params.put("oldfullname", sOldFullname);
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
