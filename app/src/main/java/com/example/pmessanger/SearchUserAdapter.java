package com.example.pmessanger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmessanger.utils.AndroidUtil;
import com.example.pmessanger.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<Users, SearchUserAdapter.UserModelViewHolder> {
    Context context;
    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<Users> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull SearchUserAdapter.UserModelViewHolder holder, int position, @NonNull Users model) {
        holder.usernameText.setText(model.getUserName());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(FirebaseUtils.currentUserId())){
            holder.usernameText.setText(model.getUserName()+" (Me)");
        }

        FirebaseUtils.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, chatWindow.class);
            AndroidUtil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public SearchUserAdapter.UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_user_row, parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText, phoneText;
        CircleImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_view);
        }
    }
}
