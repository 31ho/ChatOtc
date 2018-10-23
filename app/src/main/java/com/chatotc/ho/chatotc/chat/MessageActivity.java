package com.chatotc.ho.chatotc.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chatotc.ho.chatotc.R;
import com.chatotc.ho.chatotc.fragment.MessageRecyclerViewAdapter;
import com.chatotc.ho.chatotc.fragment.MessageViewHolder;
import com.chatotc.ho.chatotc.model.ChatModel;
import com.chatotc.ho.chatotc.model.NotificationModel;
import com.chatotc.ho.chatotc.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;
    MessageRecyclerViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinationUid = getIntent().getStringExtra("destinationUid");
        button = (Button) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_edittext);

        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if(chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });
                } else {

                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms")
                            .child(chatRoomUid)
                            .child("comments")
                            .push().setValue(comment)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    adapter.sendGcm(editText.getText().toString());
                                    editText.setText("");
                                }
                            });
                }
            }
        });

        checkChatRoom();
    }



    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid)
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() == null){
                            ChatModel newRoom = new ChatModel();
                            newRoom.users.put(uid, true);
                            newRoom.users.put(destinationUid,true);

                            FirebaseDatabase.getInstance().getReference().child("chatrooms")
                                    .push().setValue(newRoom)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checkChatRoom();
                                        }
                                    });
                            return;
                        }

                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            ChatModel chatModel = item.getValue(ChatModel.class);
                            if(chatModel.users.containsKey(destinationUid) && chatModel.users.size() ==2){
                                chatRoomUid = item.getKey();
                                button.setEnabled(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                                adapter = new MessageRecyclerViewAdapter(chatRoomUid, destinationUid, uid, recyclerView);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("chatrooms")
                .child(chatRoomUid)
                .child("comments");
        databaseReference.removeEventListener(adapter.getValueEventListener());
        finish();
    }
}