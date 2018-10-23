package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chatotc.ho.chatotc.R;
import com.chatotc.ho.chatotc.model.ChatModel;
import com.chatotc.ho.chatotc.model.NotificationModel;
import com.chatotc.ho.chatotc.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


public class MessageRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ChatModel.Comment> comments;
    private UserModel destinationUserModel;
    private String chatRoomUid;
    private String uid;
    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public MessageRecyclerViewAdapter(String chatRoomUid, String des, String uid, RecyclerView rView){
        comments = new ArrayList<>();
        this.chatRoomUid = chatRoomUid;
        this.uid = uid;
        recyclerView = rView;

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(des)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        destinationUserModel = dataSnapshot.getValue(UserModel.class);
                        getMessageList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MessageViewHolder messageViewHolder = ((MessageViewHolder)viewHolder);

        if(comments.get(i).uid.equals(uid)){
            messageViewHolder.textView_message.setText(comments.get(i).message);
            messageViewHolder.textView_message.setBackgroundResource(R.drawable.right_bubble);
            messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
            ((LinearLayout)messageViewHolder.linearLayout_message).setGravity(Gravity.RIGHT);
        } else {
            Glide.with(viewHolder.itemView.getContext())
                    .load(destinationUserModel.profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(messageViewHolder.imageView_profile);
            messageViewHolder.textView_name.setText(destinationUserModel.userName);
            messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
            messageViewHolder.textView_message.setBackgroundResource(R.drawable.left_bubble);
            messageViewHolder.textView_message.setText(comments.get(i).message);

            ((LinearLayout)messageViewHolder.linearLayout_message).setGravity(Gravity.LEFT);
        }

        long unixTime = (long) comments.get(i).timestamp;
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = simpleDateFormat.format(date);
        messageViewHolder.textView_timestamp.setText(time);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    void getMessageList(){
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("chatrooms")
                .child(chatRoomUid)
                .child("comments");
        valueEventListener = databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        comments.clear();
                        Map<String,Object> readUsersMap = new HashMap<>();

                        for(DataSnapshot item : dataSnapshot.getChildren()){

                            String key = item.getKey();
                            ChatModel.Comment comment = item.getValue(ChatModel.Comment.class);

                            comment.readUsers.put(uid, true);
                            readUsersMap.put(key, comment);

                            comments.add(item.getValue(ChatModel.Comment.class));

                        }

                        FirebaseDatabase.getInstance().getReference()
                                .child("chatrooms")
                                .child(chatRoomUid)
                                .child("comments")
                                .updateChildren(readUsersMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notifyDataSetChanged();
                                        recyclerView.scrollToPosition(getItemCount() - 1);
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void sendGcm(String text){

        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = text;
        notificationModel.data.title = userName;
        notificationModel.data.text = text;

        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utp8"), gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyDw4Q607aCPSz1XkaVwqW-r-1Ql3T7Tkis")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    public ValueEventListener getValueEventListener(){
        ValueEventListener valueEventListener = this.valueEventListener;
        return valueEventListener;
    }

}
