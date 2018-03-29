package com.codeworm.barkapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ChangeMobileNumberActivity extends AppCompatActivity {
    TextView textCurrentMobileNumber;
    EditText editNewMobileNumber;
    Button btnNext, btnCancel;
    String sMobileNum;
    ValidationFlag validationFlag = new ValidationFlag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile_number);

        textCurrentMobileNumber = (TextView) findViewById(R.id.textCurrentMobileNumber);
        editNewMobileNumber = (EditText) findViewById(R.id.editNewMobileNumber);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        textCurrentMobileNumber.setText(SharedPreferencesManager.getInstance(this).getMobilenum());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sMobileNum = editNewMobileNumber.getText().toString();
                if(validate()){
                    sMobileNum = validationFlag.formatMobileNumber(sMobileNum);
                    verifyMobileNumber();
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

        if(sMobileNum.isEmpty() || validationFlag.checkMobileNumber(sMobileNum)){
            if(sMobileNum.isEmpty()){
                editNewMobileNumber.setError("Please enter your mobile number");
            }else{
                editNewMobileNumber.setError("Please enter a valid mobile number");
            }
            valid = false;
        }

        return valid;
    }

    public void verifyMobileNumber(){
        sendCode();
        Intent intent = new Intent(this, ChangeMobileNumberVerificationActivity.class);
        intent.putExtra("newnumber", sMobileNum);
        startActivity(intent);
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

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
