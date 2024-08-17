package com.example.pmessanger;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pmessanger.utils.AndroidUtil;
import com.example.pmessanger.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.logging.Logger;

public class splash extends AppCompatActivity {
    ImageView logo;
    TextView name, own1, own2;
    // for Animation
    Animation topAnim, bottomAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        logo = findViewById(R.id.logoImg);
        name = findViewById(R.id.logoNameImg);
        own1 = findViewById(R.id.ownOne);
        own2 = findViewById(R.id.ownTwo);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animations);

        logo.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        own1.setAnimation(bottomAnim);
        own2.setAnimation(bottomAnim);

        if(FirebaseUtils.isLoggedIn() && getIntent().getExtras()!= null) {
            // From notification
            String userId = getIntent().getExtras().getString("userId");
            assert userId != null;
            FirebaseUtils.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                Users users =task.getResult().toObject(Users.class);

                                Intent mainIntent = new Intent(splash.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainIntent);

                                Intent intent = new Intent(splash.this, chatWindow.class);
                                AndroidUtil.passUserModelAsIntent(intent,users);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }else {
            // splash screen after open which screen and how many times show
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtils.isLoggedIn()) {
                        Intent intent = new Intent(splash.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(splash.this, LoginPhoneNumber.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }, 3000);
        }
    }
}