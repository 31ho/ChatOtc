package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chatotc.ho.chatotc.R;
import com.chatotc.ho.chatotc.model.ChatModel;
import com.chatotc.ho.chatotc.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    private String uid;
    private List<ChatModel> chatModelList = new ArrayList<>();
    private ChatRoomViewHolder chatRoomViewHolder;

    public ChatRecyclerViewAdapter(){
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chatrooms")
                .orderByChild("users/"+uid)
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatModelList.clear();
                for (DataSnapshot item :dataSnapshot.getChildren()){
                    chatModelList.add(item.getValue(ChatModel.class));
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_chat, viewGroup, false);

        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        chatRoomViewHolder = (ChatRoomViewHolder) viewHolder;
        String destinationUid = null;

        for (String user : chatModelList.get(i).users.keySet()) {
            if (!user.equals(uid)) {
                destinationUid = user;
            }
        }
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(destinationUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        Glide.with(chatRoomViewHolder.itemView.getContext())
                                .load(userModel.profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(chatRoomViewHolder.chatRoom_imageView);
                        chatRoomViewHolder.chatRoom_title.setText(userModel.userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
        commentMap.putAll(chatModelList.get(i).comments);
        if(commentMap.keySet().toArray().length > 0) {


            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            chatRoomViewHolder.chatRoom_last_message.setText(chatModelList.get(i).comments.get(lastMessageKey).message);
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }
}
