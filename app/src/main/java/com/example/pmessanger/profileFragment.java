package com.example.pmessanger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;

import com.example.pmessanger.utils.AndroidUtil;
import com.example.pmessanger.utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class profileFragment extends Fragment {
    CircleImageView profilePic;
    EditText usernameInput, phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    Users currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public profileFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == Activity.RESULT_OK) {
                    Intent data = o.getData();
                    if(data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBtnClick();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // if logout token will be deleted
                            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        FirebaseUtils.logout();
                                        Intent intent = new Intent(getContext(), splash.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(profileFragment.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });
        return view;

    }

    void updateBtnClick() {
        String newUserName = usernameInput.getText().toString();
        if(newUserName.isEmpty() || newUserName.length()<3) {
            usernameInput.setError("Username length should be at least 3 characters");
            return;
        }

        currentUserModel.setUserName(newUserName);
        setInProgress(true);

        if(selectedImageUri != null) {
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            updateToFirestore();
                        }
                    });
        } else {
            updateToFirestore();
        }
    }

    public void updateToFirestore() {
        FirebaseUtils.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            AndroidUtil.showToast(getContext(), "Updated Successfully");
                        } else {
                            AndroidUtil.showToast(getContext(), "Updated failed");
                        }
                    }
                });
    }
        public void getUserData() {
            setInProgress(true);

            FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getContext(), uri, profilePic);
                            }
                        }
                    });

            FirebaseUtils.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    setInProgress(false);
                    currentUserModel = task.getResult().toObject(Users.class);
                    usernameInput.setText(currentUserModel.getUserName());
                    phoneInput.setText(currentUserModel.getPhone());
                }
            });
        }

        public void setInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}