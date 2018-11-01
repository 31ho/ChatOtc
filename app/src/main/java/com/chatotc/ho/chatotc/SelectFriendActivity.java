package com.chatotc.ho.chatotc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.chatotc.ho.chatotc.fragment.PeopleFragmentRecyclerViewAdapter;
import com.chatotc.ho.chatotc.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SelectFriendActivity extends AppCompatActivity {

    PeopleFragmentRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selectfriendactivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PeopleFragmentRecyclerViewAdapter(this, 1);
        recyclerView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.selectfriendactivity_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ChatModel chatModel = adapter.getChatModel();
                chatModel.users.put(myUid, true);

                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
            }
        });
    }
}
