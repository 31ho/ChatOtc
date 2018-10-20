package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chatotc.ho.chatotc.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_message;
    public TextView textView_name;
    public ImageView imageView_profile;
    public LinearLayout linearLayout_destination;
    public LinearLayout linearLayout_message;


    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_message = (TextView) itemView.findViewById(R.id.itemMessage_textview);
        textView_name = (TextView) itemView.findViewById(R.id.itemMessage_textView_name);
        imageView_profile = (ImageView) itemView.findViewById(R.id.itemMessage_imageView_profile);
        linearLayout_destination = (LinearLayout) itemView.findViewById(R.id.itemMessage_linearlayout_destination);
        linearLayout_message = (LinearLayout) itemView.findViewById(R.id.itemMessage_linearlayout);
    }
}
