package com.example.pmessanger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;


public class LoginPhoneNumber extends AppCompatActivity {
Button send_otp_btn;
EditText login_mobile_number;
FirebaseAuth auth;
ProgressBar progressBar;
CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();

        login_mobile_number = findViewById(R.id.login_mobile_number);
        send_otp_btn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.progressBarLogin);
        countryCodePicker = findViewById(R.id.login_country_code);
        hideProgressBar();

        countryCodePicker.registerCarrierNumberEditText(login_mobile_number);

        send_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!countryCodePicker.isValidFullNumber()) {
                    login_mobile_number.setError("Phone number is not Valid");
                    return;
                }
                Intent intent = new Intent(LoginPhoneNumber.this, LoginOTP.class);
                intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
                startActivity(intent);
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
