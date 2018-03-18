package com.codeworm.barkapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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

public class PhoneNumberVerificationActivity extends AppCompatActivity {
    Button btnCancel, btnSubmit, btnResend;
    String sFirstDigit, sSecondDigit, sThirdDigit, sFourthDigit, sFullname, sUsername, sPassword, sMobileNum;
    EditText etFirstDigit, etSecondDigit, etThirdDigit, etFouthDigit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnResend = (Button) findViewById(R.id.btnResend);
        etFirstDigit = (EditText) findViewById(R.id.tvFirstDigit);
        etSecondDigit = (EditText) findViewById(R.id.tvSecondDigit);
        etThirdDigit = (EditText) findViewById(R.id.tvThirdDigit);
        etFouthDigit = (EditText) findViewById(R.id.tvFourthDigit);

        if(getIntent().getStringExtra("type").equals("signup")){
            sFullname = getIntent().getStringExtra("sFullname");
            sUsername = getIntent().getStringExtra("sUsername");
            sPassword = getIntent().getStringExtra("sPassword");
            sMobileNum = getIntent().getStringExtra("sMobileNum");
        }else{
            sUsername = getIntent().getStringExtra("sUsername");
            sMobileNum = getIntent().getStringExtra("sMobileNum");
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sFirstDigit = etFirstDigit.getText().toString();
                sSecondDigit = etSecondDigit.getText().toString();
                sThirdDigit = etThirdDigit.getText().toString();
                sFourthDigit = etFouthDigit.getText().toString();

                String sCode = sFirstDigit + sSecondDigit + sThirdDigit + sFourthDigit;
                System.out.println("Value of sMobileNum is -> " + sMobileNum);
                System.out.println("Value of sCode is -> " + sCode);
                System.out.println("Entering CheckCodeAsyncTask...");
                CheckCodeAsyncTask checkCodeAsyncTask = new CheckCodeAsyncTask();
                checkCodeAsyncTask.execute(sMobileNum, sCode);

            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnResend.setEnabled(false);
                sendCode();
            }
        });

    }

    public class CheckCodeAsyncTask extends AsyncTask<String,Void,Void> {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbCodeVerification);
        String checkCodeResponse;
        private Context mContext;
        final LoadingDialog loadingDialog = new LoadingDialog(PhoneNumberVerificationActivity.this);

//        public CheckCodeAsyncTask(Context context){
//            this.mContext = context;
//        }

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
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println("Value of checkCodeResponse -> " + checkCodeResponse);
            System.out.println("Entered onPostExecute..");
            if(checkCodeResponse.equals("true")){
                System.out.println("Entered in checkCodeResponse condition");
                Toast.makeText(getApplicationContext(), "Code matched.", Toast.LENGTH_SHORT).show();
                if(getIntent().getStringExtra("type").equals("signup")){
                    registerUser();
                    loadingDialog.dismiss();

                }else{
                    proceedToChangePassword();
                    loadingDialog.dismiss();
                }

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

        public void registerUser(){
            System.out.println("Entered registerUser method...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //progressDialog.dismiss();
                            try {
                                System.out.println("Entered onResponse method...");
                                System.out.println(response);
                                JSONObject jsonObject = new JSONObject(response);
                                Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                                //sUsername = sUsername + "@barkapp.com";
                                System.out.println("Value of sUsername in signUp is --->" + sUsername);
                                System.out.println("Value of sPassword in signUp is --->" + sPassword);

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);


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
                    params.put("username", sUsername);
                    params.put("password", sPassword);
                    params.put("mobilenum", sMobileNum);
                    return params;
                }
            };

            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }

        public void proceedToChangePassword(){
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            intent.putExtra("sUsername", sUsername);
            intent.putExtra("sMobileNum", sMobileNum);
            startActivity(intent);

        }
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
