package com.codeworm.barkapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class ForgotPasswordPageTwoActivity extends AppCompatActivity {
    String sUsername, sMobileNum;
    EditText etMobileNum;
    Button btnSubmit, btnCancel;
    ValidationFlag validationFlag = new ValidationFlag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page_two);

        etMobileNum = (EditText) findViewById(R.id.editMobileNum);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        sUsername = getIntent().getStringExtra("username");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sMobileNum = etMobileNum.getText().toString();

                if(validate()){
                    sMobileNum = validationFlag.formatMobileNumber(sMobileNum);
                    checkPreReqForgotPassword(sUsername, sMobileNum);
                }


            }
        });

    }

    public boolean validate(){
        boolean valid = true;

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

    public void checkPreReqForgotPassword(final String sUsername, final String sMobileNum){
        final LoadingDialog loadingDialog = new LoadingDialog(ForgotPasswordPageTwoActivity.this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHECK_FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("valid").equals("true")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                sendCode();
                                loadingDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(),PhoneNumberVerificationActivity.class);
                                intent.putExtra("type", "forgotpassword");
                                intent.putExtra("sUsername", sUsername);
                                intent.putExtra("sMobileNum", sMobileNum);
                                startActivity(intent);

                            }else{
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("mobilenum", sMobileNum);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void sendCode(){
        System.out.println("Value of sMobileNum is --> " + sMobileNum);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEND_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
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
                        //progressDialog.hide();
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
}
