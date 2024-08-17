package com.example.pmessanger.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.GenericLifecycleObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.pmessanger.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class AndroidUtil {
    public static  void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, Users model){
        intent.putExtra("userName",model.getUserName());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());

    }

    public static Users getUserModelFromIntent(Intent intent){
        Users userModel = new Users();
        userModel.setUserName(intent.getStringExtra("userName"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

//    public static void setProfilePic(Context context, Uri imageUri, CircleImageView imageView){
//        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
//    }

    public static void setProfilePic(Context context, Uri imageUri, CircleImageView imageView) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return;
            }
        }

        Glide.with(context)
                .load(imageUri)
                .into(imageView);
    }
}
