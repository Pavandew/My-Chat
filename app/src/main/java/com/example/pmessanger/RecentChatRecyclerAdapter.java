package com.example.pmessanger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmessanger.chatWindow;
import com.example.pmessanger.R;
import com.example.pmessanger.chatMessageModel;
import com.example.pmessanger.Users;
import com.example.pmessanger.utils.AndroidUtil;
import com.example.pmessanger.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<msgModelClass, RecentChatRecyclerAdapter.msgModelClassViewHolder> {
    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<msgModelClass> options, Context context) {
        super(options);
        this.context = context;
    }



    @Override
    protected void onBindViewHolder(@NonNull RecentChatRecyclerAdapter.msgModelClassViewHolder holder, int position, @NonNull msgModelClass model) {
        FirebaseUtils.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            boolean lastMessageSendByMe = model.getLastMessageSenderId().equals(FirebaseUtils.currentUserId());

                            Users otherUser = task.getResult().toObject(Users.class);

                            FirebaseUtils.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()) {
                                        Uri uri = task.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                    }
                                }
                            });

                            holder.usernameText.setText(otherUser.getUserName());
                            if(lastMessageSendByMe){
                                holder.lastMessageText.setText("you: "+model.getLastMessage());
                            } else {
                                holder.lastMessageText.setText(model.getLastMessage());
                            }
                            holder.lastMessageTime.setText(FirebaseUtils.timestampToString(model.getLastMessageTimestamp()));

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //navigate to chat activity
                                    Intent intent = new Intent(context, chatWindow.class);
                                        AndroidUtil.passUserModelAsIntent(intent, otherUser);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }


    @NonNull
    @Override
    public RecentChatRecyclerAdapter.msgModelClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new msgModelClassViewHolder(view);
    }
    class msgModelClassViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        CircleImageView profilePic;

        public msgModelClassViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.user_image);
        }
    }
}