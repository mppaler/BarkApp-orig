package com.codeworm.barkapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChangeMobileNumberVerificationActivity extends AppCompatActivity {
    Button btnCancel, btnSubmit, btnResend;
    String sFirstDigit, sSecondDigit, sThirdDigit, sFourthDigit, sUsername, sMobileNum;
    EditText etFirstDigit, etSecondDigit, etThirdDigit, etFouthDigit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile_number_verification);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnResend = (Button) findViewById(R.id.btnResend);
        etFirstDigit = (EditText) findViewById(R.id.tvFirstDigit);
        etSecondDigit = (EditText) findViewById(R.id.tvSecondDigit);
        etThirdDigit = (EditText) findViewById(R.id.tvThirdDigit);
        etFouthDigit = (EditText) findViewById(R.id.tvFourthDigit);

        sMobileNum = getIntent().getStringExtra("newnumber");
        sUsername = SharedPreferencesManager.getInstance(this).getUsername();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sFirstDigit = etFirstDigit.getText().toString();
                sSecondDigit = etSecondDigit.getText().toString();
                sThirdDigit = etThirdDigit.getText().toString();
                sFourthDigit = etFouthDigit.getText().toString();

                String sCode = sFirstDigit + sSecondDigit + sThirdDigit + sFourthDigit;
                CheckCodeAsyncTask checkCodeAsyncTask = new CheckCodeAsyncTask();
                checkCodeAsyncTask.execute(sMobileNum, sCode);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnResend.setEnabled(false);
                sendCode();
            }
        });

        etFirstDigit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Integer textlength1 = etFirstDigit.getText().length();

                if (textlength1 >= 1) {
                    etSecondDigit.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etSecondDigit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Integer textlength1 = etSecondDigit.getText().length();

                if (textlength1 >= 1) {
                    etThirdDigit.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etThirdDigit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Integer textlength1 = etThirdDigit.getText().length();

                if (textlength1 >= 1) {
                    etFouthDigit.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public class CheckCodeAsyncTask extends AsyncTask<String,Void,Void> {
        String checkCodeResponse;

        final LoadingDialog loadingDialog = new LoadingDialog(ChangeMobileNumberVerificationActivity.this);

        @Override
        protected void onPreExecute() {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
            System.out.println("Just entered the AsyncTask and I'm here at onPreExecute method");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            checkCodeResponse = checkCode(params);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            System.out.println("Value of checkCodeResponse -> " + checkCodeResponse);
            System.out.println("Entered onPostExecute..");
            if(checkCodeResponse.equals("true")){
                System.out.println("Entered in checkCodeResponse condition");
                Toast.makeText(getApplicationContext(), "Code matched.", Toast.LENGTH_SHORT).show();
                changeMobileNumber();
                loadingDialog.dismiss();

            }else{
                loadingDialog.dismiss();
                System.out.println("Failed to enter in checkCodeResponse condition");
                Toast.makeText(getApplicationContext(), "Code did not match.", Toast.LENGTH_SHORT).show();
            }
        }

        public String checkCode(String input[]){
            String response = "";
            try {
                System.out.println("HttpRequest starting...");
                URL url = new URL(Constants.URL_CHECK_CODE);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("mobilenum", input[0])
                        .appendQueryParameter("code", input[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();

                int statusCode = httpURLConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    System.out.println("Checkpoint: After Buffered Input Stream");
                    response = convertInputStreamToString(inputStream);
                    System.out.println("Value of Response was -> " + response);

                } else {

                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        public String convertInputStreamToString(InputStream entityResponse) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = entityResponse.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString();
        }

    }

    public void changeMobileNumber(){
        System.out.println("Entered registerUser method...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHANGE_MOBILE_NUMBER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println("Entered onResponse method...");
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("error").equals("false")){
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                SharedPreferencesManager.getInstance(getApplicationContext()).setMobilenum(sMobileNum);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }else{

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

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
