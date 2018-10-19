package com.chatotc.ho.chatotc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chatotc.ho.chatotc.fragment.PeopleFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new PeopleFragment()).commit();
    }
}
