package com.chatotc.ho.chatotc.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.chatotc.ho.chatotc.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView textView;
    public TextView textView_comment;
    public LinearLayout colorLayout;
    public CheckBox checkBox;


    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.itemfriend_imageview);
        textView = (TextView) itemView.findViewById(R.id.itemfriend_textview);
        textView_comment = (TextView) itemView.findViewById(R.id.itemfriend_textview_comment);
        colorLayout = (LinearLayout) itemView.findViewById(R.id.itemfriend_colorlayout);
        checkBox = (CheckBox) itemView.findViewById(R.id.itemfriend_checkbox);

    }
}
