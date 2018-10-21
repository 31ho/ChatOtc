package com.chatotc.ho.chatotc;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.chatotc.ho.chatotc.fragment.ChatFragment;
import com.chatotc.ho.chatotc.fragment.PeopleFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainActivity_bottom_navigationview);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_people :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new PeopleFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new ChatFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
