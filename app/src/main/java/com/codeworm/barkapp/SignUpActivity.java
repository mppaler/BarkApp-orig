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

public class SignUpActivity extends AppCompatActivity {

    EditText etFullname, etUsername, etPassword, etConPassword, etMobileNum;
    Button btnSubmit;
    String sFullname, sUsername, sPassword, sConPassword, sMobileNum;
    ValidationFlag validationFlag = new ValidationFlag();
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFullname = (EditText) findViewById(R.id.editFullname);
        etUsername = (EditText) findViewById(R.id.editUsername);
        etPassword = (EditText) findViewById(R.id.editPassword);
        etConPassword = (EditText) findViewById(R.id.editConPassword);
        etMobileNum = (EditText) findViewById(R.id.editMobileNum);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        System.out.println("SignUp: I was here!");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }



    public void initialize(){
        sFullname = etFullname.getText().toString();
        sUsername = etUsername.getText().toString();
        sPassword = etPassword.getText().toString();
        sConPassword = etConPassword.getText().toString();
        sMobileNum = etMobileNum.getText().toString();
    }

    public void register(){
        initialize();
        if(!validate()){
            //loadingDialog.dismiss();
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate(){
        boolean valid = true;
        validationFlag.setCheckUsernameFlag(false);
        if(sFullname.isEmpty()){
            etFullname.setError("Please enter valid name");
            valid = false;
        }
        if(sUsername.isEmpty() || validateUsername(sUsername)){
            if(sUsername.isEmpty()){
                etUsername.setError("Please enter a username");
            }else{
                etUsername.setError("This username is already been taken");
            }

            valid = false;
        }
        if(!sPassword.equals(sConPassword) || sPassword.isEmpty()){
            System.out.println("Value of sPassword " + sPassword + " Value of sConPassword " + sConPassword);
            if(sPassword.isEmpty()){
                etPassword.setError("Please enter your password");
            }else{
                etPassword.setError("Password does not match");
            }
            valid = false;
        }
        if(!sConPassword.equals(sPassword) || sConPassword.isEmpty()){
            if(sConPassword.isEmpty()){
                etConPassword.setError("Please enter your password");
            }else{
                etConPassword.setError("Password does not match");
            }
            valid = false;
        }
        if(sMobileNum.isEmpty() || validationFlag.checkMobileNumber(sMobileNum)){
            if(sMobileNum.isEmpty()){
                etMobileNum.setError("Please enter your mobile number");
            }else{
                etMobileNum.setError("Please enter a valid mobile number");
            }
            valid = false;
        }
        return valid;
    }

    public boolean validateUsername(String username){
        loadingDialog = new LoadingDialog(SignUpActivity.this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        boolean decision = true;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHECK_USERNAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            System.out.println("Getting there...");

                            if(jsonObject.getString("type").equals("Username already taken")){
                                validationFlag.setCheckUsernameFlag(true);
                            }else{
                                validationFlag.setCheckUsernameFlag(false);
                                continueSignUp();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        decision = validationFlag.isCheckUsernameFlag();
        return decision;
    }

    public void doCancel(View view){

        SignUpActivity.super.finish();
    }

    public void sendCode(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEND_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), "Sending 4-Digit Code to your number", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("mobilenum", sMobileNum);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void continueSignUp(){
        sMobileNum = validationFlag.formatMobileNumber(sMobileNum);
        sendCode(); //Application will send a 4-Digit code
        loadingDialog.dismiss();
        Intent intent = new Intent(this, PhoneNumberVerificationActivity.class);
        intent.putExtra("type", "signup");
        intent.putExtra("sFullname",sFullname);
        intent.putExtra("sUsername",sUsername);
        intent.putExtra("sPassword",sPassword);
        intent.putExtra("sMobileNum",sMobileNum);
        startActivity(intent);
        finish();
    }

}


