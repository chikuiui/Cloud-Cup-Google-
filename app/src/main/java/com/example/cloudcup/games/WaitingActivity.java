package com.example.cloudcup.games;

import android.os.Bundle;

import com.example.cloudcup.GameActivity;
import com.example.cloudcup.R;

public class WaitingActivity extends GameActivity {
    private static final String LOG_TAG = WaitingActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        // we have created a enum object called state in GameActivity which is protected so that we can access in inherited class
        state = GameState.WAITING;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);
    }
}
