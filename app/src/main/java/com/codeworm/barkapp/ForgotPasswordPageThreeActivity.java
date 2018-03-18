package com.codeworm.barkapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordPageThreeActivity extends AppCompatActivity {
    Button btnCancel, btnSubmit;
    String sFirstDigit, sSecondDigit, sThirdDigit, sFourthDigit, sFullname, sUsername, sPassword, sMobileNum;
    EditText etFirstDigit, etSecondDigit, etThirdDigit, etFouthDigit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page_three);


    }
}
