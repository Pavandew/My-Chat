package com.example.pmessanger;

import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmessanger.utils.AndroidUtil;
import com.example.pmessanger.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.util.Arrays;


import de.hdodenhof.circleimageview.CircleImageView;

public class chatWindow extends AppCompatActivity {
    Users otherUser;
    String chatroomId;
    chatMessageModel chatroomModel;
    messagesAdapter adapter;
    msgModelClass msg_ModelClass;
    EditText messageInput;
    ImageView sendMessageBtn;
    ImageView backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //get UserModel
        // Retrieve the Users object passed through the Intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), otherUser.getUserId());

        // Initialize UI components

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.btn_back);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_view);


        FirebaseUtils.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
            .addOnCompleteListener(t -> {
                if(t.isSuccessful()){
                    Uri uri  = t.getResult();
                    AndroidUtil.setProfilePic(this,uri,imageView);
                }
            });

        // For back Button
        backBtn.setOnClickListener(view -> finish());


        otherUsername.setText(otherUser.getUserName());

        sendMessageBtn.setOnClickListener((v -> {
            String messages = messageInput.getText().toString().trim();
            if (messages.isEmpty())
                return;
            sendMessageToUser(messages);
        }));

        getOrCreatemsgModelClass();

        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatMessageModel> options = new FirestoreRecyclerOptions.Builder<chatMessageModel>()
                .setQuery(query, chatMessageModel.class).build();

        adapter = new messagesAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message) {

        msg_ModelClass.setLastMessageTimestamp(Timestamp.now());
        msg_ModelClass.setLastMessageSenderId(FirebaseUtils.currentUserId());
        msg_ModelClass.setLastMessage(message);
        FirebaseUtils.getChatroomReference(chatroomId).set(msg_ModelClass);

        chatMessageModel chatMessageModel = new chatMessageModel(message, FirebaseUtils.currentUserId(), Timestamp.now());
        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreatemsgModelClass() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                msg_ModelClass = task.getResult().toObject(msgModelClass.class);
                if (msg_ModelClass == null) {
                    //first time chat
                    msg_ModelClass = new msgModelClass(
                            chatroomId,
                            Arrays.asList(FirebaseUtils.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtils.getChatroomReference(chatroomId).set(msg_ModelClass);
                }
            }
        });
    }

    void sendNotification(String message) {

        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Users currentUser = task.getResult().toObject(Users.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", currentUser.getUserName());
                    notificationObj.put("body", message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());

//                    callApi(jsonObject);


                } catch (Exception e) {

                }

            }
        });

    }

//void callApi(JSONObject jsonObject){
//    MediaType JSON = MediaType.get("application/json; charset=utf-8");
//    OkHttpClient client = new OkHttpClient();
//    String url = "https://fcm.googleapis.com/fcm/send";
//    RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
//    Request request = new Request.Builder()
//            .url(url)
//            .post(body)
//            .header("Authorization","Bearer YOUR_API_KEY")
//            .build();
//    client.newCall(request).enqueue(new Callback() {
//        @Override
//        public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//        }
//
//        @Override
//        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//        }
//    });
}



