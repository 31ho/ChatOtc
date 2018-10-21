package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatotc.ho.chatotc.R;

public class ChatRoomViewHolder extends RecyclerView.ViewHolder {


    public ImageView chatRoom_imageView;
    public TextView chatRoom_title;
    public TextView chatRoom_last_message;

    public ChatRoomViewHolder(@NonNull View itemView) {
        super(itemView);


        chatRoom_imageView = (ImageView) itemView.findViewById(R.id.itemchat_imageview);
        chatRoom_title = (TextView) itemView.findViewById(R.id.itemchat_textview_title);
        chatRoom_last_message = (TextView) itemView.findViewById(R.id.itemchat_textview_lastmessage);
    }
}
