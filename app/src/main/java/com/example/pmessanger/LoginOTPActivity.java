package com.example.pmessanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmessanger.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTPActivity extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;

    EditText login_OTP;
    Button login_next;
    ProgressBar progressBar;
    TextView resend_opt_textView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        login_OTP = findViewById(R.id.login_OTP);
        login_next = findViewById(R.id.login_next);
        resend_opt_textView = findViewById(R.id.resend_opt_textView);
        progressBar = findViewById(R.id.progressBarLogin);

        phoneNumber = getIntent().getExtras().getString("phone");
        Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_SHORT).show();
        Log.d("OTP_Verify", "Verification code: " + verificationCode);

        sendOtp(phoneNumber,false);

        login_next.setOnClickListener(v -> {
            String enteredOtp  = login_OTP.getText().toString();
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
        });

        resend_opt_textView.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });
    }


    void sendOtp(String phoneNumber,boolean isResend){
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(),"OTP sent successfully");
                                Log.d("OTP_Verify", "OTP send to: " + phoneNumber);
                                Log.d("OTP_Verify", "Verification code: " + verificationCode);
                                setInProgress(false);
                            }
                        });
        if (isResend && resendingToken != null) {
            PhoneAuthProvider.verifyPhoneNumber(
                    builder.setForceResendingToken(resendingToken).build()
            );
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            login_next.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            login_next.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginOTPActivity.this, LoginUserNameActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                }
            }
        });


    }

    void startResendTimer(){
        resend_opt_textView.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resend_opt_textView.setText("Resend OTP is "+timeoutSeconds+ " seconds");
                if(timeoutSeconds<=0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resend_opt_textView.setEnabled(true);
                        }
                    });
                }
            }
        }, 0, 1000);
    }


}
