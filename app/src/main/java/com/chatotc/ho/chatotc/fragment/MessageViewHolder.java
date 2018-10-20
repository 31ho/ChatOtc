package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chatotc.ho.chatotc.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_message;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_message = (TextView) itemView.findViewById(R.id.itemMessage_textview);
    }
}
