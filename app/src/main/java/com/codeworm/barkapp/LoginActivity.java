package com.codeworm.barkapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private VideoView nVideoView;
    EditText etUsername, etPassword;
    Button btnLogin;
    String sUsername, sPassword;
    ValidationFlag validationFlag = new ValidationFlag();
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


//        if(SharedPreferencesManager.getInstance(this).isLoggedIn()){
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//            return;
//        }
        SharedPreferencesManager.getInstance(this).clearSharePreference();
        etUsername = (EditText) findViewById(R.id.editUsername);
        etPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()) {
                    doLogin();


                }
            }
        });
        /*nVideoView = (VideoView) findViewById(R.id.bgVideo);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bgvideo);

        nVideoView.setVideoURI(uri);
        nVideoView.start();

        nVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });*/



    }


    public void initialize(){
        sUsername = etUsername.getText().toString();
        sPassword = etPassword.getText().toString();
    }
    public boolean validate(){
        initialize();
        boolean valid = true;

        if(sUsername.isEmpty()){
            etUsername.setError("Please enter username");
            valid = false;
        }
        if(sPassword.isEmpty()){
            etPassword.setError("Please enter password");
            valid = false;
        }
        return valid;
//        String method = "login";
//        Log.d("myTag", "HI MOMSHIE");
//        BackgroundTask backgroundTask = new BackgroundTask(this);
//        backgroundTask.execute(method, sUsername, sPassword);
//        //Log.d("myTag", "Pre-exit");
//        finish();
//        //Log.d("myTag", "Tapos na");
//        System.out.println("CHEATEEEER");


    }

    public void  doLogin(){
        boolean decision = false;

        //SHOW LOADING DIALOG
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        System.out.println("Username input: " + sUsername);
        System.out.println("Password input: " + sPassword);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
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
                                SharedPreferencesManager.getInstance(getApplicationContext()).loginUser(jsonObject.getString("fullname"), jsonObject.getString("username"), jsonObject.getString("password").trim(), jsonObject.getString("mobilenum"), jsonObject.getInt("id"));
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                loadingDialog.dismiss();
                                finish();

                            }else{
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            loadingDialog.dismiss();
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
                params.put("username", sUsername);
                params.put("password", sPassword);
                return params;
            }
        };
        System.out.print("The decision of momshie");
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//        decision = validationFlag.isCheckUser();
//        System.out.print("The decision of momshie is " + decision);

    }

    public void doSignUp(View view){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);

    }

    public void doForgotPassword(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);

    }

}