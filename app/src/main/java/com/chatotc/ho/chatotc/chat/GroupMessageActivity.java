package com.chatotc.ho.chatotc.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chatotc.ho.chatotc.R;
import com.chatotc.ho.chatotc.fragment.GroupMessageRecyclerViewAdapter;
import com.chatotc.ho.chatotc.model.ChatModel;
import com.chatotc.ho.chatotc.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GroupMessageActivity extends AppCompatActivity {

    Map<String, UserModel> users = new HashMap<>();
    String destinationRooms;
    String uid;

    EditText editText;

    GroupMessageRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        destinationRooms = getIntent().getStringExtra("destinationRoom");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText = (EditText) findViewById(R.id.groupMessageActivity_edittext);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.groupMessageActivity_recyclerview);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    users.put(item.getKey(), item.getValue(UserModel.class));
                }
                System.out.println(users.size());
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupMessageRecyclerViewAdapter(recyclerView, destinationRooms, uid, users);
        recyclerView.setAdapter(adapter);
    }

    void init(){
        Button button = (Button) findViewById(R.id.groupMessageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference()
                        .child("chatrooms")
                        .child(destinationRooms)
                        .child("comments")
                        .push()
                        .setValue(comment)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("chatrooms")
                                        .child(destinationRooms)
                                        .child("users")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                                                for (String item : map.keySet()){
                                                    if(item.equals(uid)){
                                                        continue;
                                                    }
                                                    adapter.sendGcm(editText.getText().toString(), users.get(item).pushToken);
                                                    editText.setText("");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("chatrooms")
                .child(destinationRooms)
                .child("comments");
        if(adapter.getValueEventListener() != null) {
            databaseReference.removeEventListener(adapter.getValueEventListener());
        }
        finish();
    }
}
