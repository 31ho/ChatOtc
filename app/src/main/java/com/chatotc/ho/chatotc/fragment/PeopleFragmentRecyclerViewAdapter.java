package com.chatotc.ho.chatotc.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chatotc.ho.chatotc.R;
import com.chatotc.ho.chatotc.chat.MessageActivity;
import com.chatotc.ho.chatotc.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    List<UserModel> userModels;
    Activity mActivity;

    public PeopleFragmentRecyclerViewAdapter(Activity activity) {
        mActivity = activity;
        userModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.getValue(UserModel.class).uid)){
                        userModels.add(0,snapshot.getValue(UserModel.class));
                    } else {
                        userModels.add(snapshot.getValue(UserModel.class));
                    }
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        Glide.with(viewHolder.itemView.getContext())
                .load(userModels.get(i).profileImageUrl)
                .apply(new RequestOptions().circleCrop())
                .into(((CustomViewHolder)viewHolder).imageView);

        ((CustomViewHolder)viewHolder).textView.setText(userModels.get(i).userName);
        if(i == 0){
            ((CustomViewHolder)viewHolder).colorLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.myColor));
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("destinationUid", userModels.get(i).uid);
                ActivityOptions activityOptions = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromright,R.anim.toleft);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent, activityOptions.toBundle());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }


}
