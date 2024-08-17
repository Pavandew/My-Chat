package com.example.pmessanger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmessanger.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class messagesAdapter extends FirestoreRecyclerAdapter<chatMessageModel, messagesAdapter.receiverViewHolder> {

    Context context;
    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    public messagesAdapter(@NonNull FirestoreRecyclerOptions<chatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull receiverViewHolder holder, int position, @NonNull chatMessageModel model) {
        Log.i("haushd","asjd");
        if(model.getSenderId().equals(FirebaseUtils.currentUserId())){
            holder.msg_txt_receiver.setVisibility(View.GONE);
            holder.msg_txt_sender.setVisibility(View.VISIBLE);
            holder.msg_txt_sender.setText(model.getMessage());
        }else{
            holder.msg_txt_sender.setVisibility(View.GONE);
            holder.msg_txt_receiver.setVisibility(View.VISIBLE);
            holder.msg_txt_receiver.setText(model.getMessage());
        }

    }

    @NonNull
    @Override
    public receiverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout,parent,false);
        return new receiverViewHolder(view);
    }

    // ViewHolder for Receiver
    class receiverViewHolder extends RecyclerView.ViewHolder {
        TextView msg_txt_sender;
        TextView msg_txt_receiver;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            msg_txt_receiver = itemView.findViewById(R.id.receiverTextView);
            msg_txt_sender = itemView.findViewById(R.id.senderTextView);
        }
    }
}