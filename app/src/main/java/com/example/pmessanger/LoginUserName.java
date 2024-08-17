package com.example.pmessanger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.TintableCheckedTextView;

import com.example.pmessanger.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUserName extends AppCompatActivity {

    EditText login_username;
    Button login_ok;
    ProgressBar progressBar;
    String phoneNumber;
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_user_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        login_username = findViewById(R.id.login_username);
        login_ok = findViewById(R.id.login_ok);
        progressBar = findViewById(R.id.progressBarLogin);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUserName();

        login_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserName();
            }
        });

    }
    void setUserName() {
        String userName = login_username.getText().toString();
        if(userName.isEmpty() || userName.length()<3) {
            login_username.setError("Username length should be at least 3 characters");
            return;
        }
        setInProgress(true);
        if(users!= null) {
            users.setUserName(userName);
        } else {
            users = new Users(phoneNumber, userName,FirebaseUtils.currentUserId(), Timestamp.now());
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        FirebaseUtils.currentUserDetails().set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    void getUserName() {
        setInProgress(true);
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                     users =  task.getResult().toObject(Users.class);
                     if(users != null) {
                         login_username.setText(users.getUserName());
                     }
                }
            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            login_ok.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            login_ok.setVisibility(View.VISIBLE);
        }
    }
}