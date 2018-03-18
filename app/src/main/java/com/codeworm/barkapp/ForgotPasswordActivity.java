package com.codeworm.barkapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etUsername;
    Button btnNext, btnCancel;
    String sUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etUsername = (EditText) findViewById(R.id.editUsername);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sUsername = etUsername.getText().toString();

                if(validate()){
                    Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordPageTwoActivity.class);
                    intent.putExtra("username", sUsername);
                    startActivity(intent);

                }
            }
        });



    }

    public boolean validate(){
        boolean valid = true;

        if (sUsername.isEmpty()) {
            etUsername.setError("Please enter a Username");
            valid = false;
        }

        return valid;
    }
}
