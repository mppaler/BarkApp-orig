package com.codeworm.barkapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new BackgroundSplashTask().execute();

    }

    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(SharedPreferencesManager.getInstance(getApplicationContext()).isLoggedIn()){
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }else{
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(4800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
