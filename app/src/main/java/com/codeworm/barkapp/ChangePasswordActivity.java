package com.codeworm.barkapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePasswordActivity extends AppCompatActivity {
    Button btnSubmit, btnCancel;
    EditText editCurrentPassword, editNewPassword, editConfirmNewPassword;
    String sCurrentPassword, sNewPassword, sConfirmNewPassword, sUsername, sMobileNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        editCurrentPassword = (EditText) findViewById(R.id.editCurrentPassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmNewPassword = (EditText) findViewById(R.id.editConfirmNewPassword);

        sUsername = SharedPreferencesManager.getInstance(this).getUsername();
        sMobileNum = SharedPreferencesManager.getInstance(this).getMobilenum();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCurrentPassword = editCurrentPassword.getText().toString();
                sNewPassword = editNewPassword.getText().toString();
                sConfirmNewPassword = editConfirmNewPassword.getText().toString();

                if(validate()){
                    changePassword(sNewPassword);
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

        if(!sCurrentPassword.equals(SharedPreferencesManager.getInstance(this).getPassword()) || sCurrentPassword.isEmpty()){
            if(sCurrentPassword.isEmpty()){
                editCurrentPassword.setError("Please enter your current password");
            }else{
                editCurrentPassword.setError("Entered value does not match with your current password");
            }
            valid = false;
        }

        if(!sNewPassword.equals(sConfirmNewPassword) || sNewPassword.isEmpty()){
            if(sNewPassword.isEmpty()){
                editNewPassword.setError("Please enter a password");
            }else{
                editNewPassword.setError("Password does not match");
            }
            valid = false;
        }

        if(!sConfirmNewPassword.equals(sNewPassword) || sConfirmNewPassword.isEmpty()){
            if(sConfirmNewPassword.isEmpty()){
                editConfirmNewPassword.setError("Please enter a password");
            }else{
                editConfirmNewPassword.setError("Password does not match");
            }
            valid = false;
        }
        return valid;
    }

    public void changePassword(final String sNewPassword){
        final LoadingDialog loadingDialog = new LoadingDialog(ChangePasswordActivity.this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHANGE_PASSWORD,
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
                                SharedPreferencesManager.getInstance(getApplicationContext()).setPassword(sNewPassword);
                                finish();
                                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                startActivity(intent);

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
                params.put("password", sNewPassword);
                params.put("username", sUsername);
                params.put("mobilenum", sMobileNum);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
