package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatotc.ho.chatotc.R;
import com.chatotc.ho.chatotc.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    private String uid;
    private List<ChatModel> chatModelList = new ArrayList<>();

    public ChatRecyclerViewAdapter(){
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chatrooms")
                .orderByChild("users/" + uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatModelList.clear();
                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            chatModelList.add(item.getValue(ChatModel.class));
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_chat, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }
}
