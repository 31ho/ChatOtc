package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatotc.ho.chatotc.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView textView;
    public LinearLayout colorLayout;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.itemfriend_imageview);
        textView = (TextView) itemView.findViewById(R.id.itemfriend_textview);
        colorLayout = (LinearLayout) itemView.findViewById(R.id.itemfriend_colorlayout);
    }
}
