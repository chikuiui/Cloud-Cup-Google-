package com.example.cloudcup.games;


import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import com.example.cloudcup.GameActivity;
import com.example.cloudcup.R;


public class SwipeGameActivity extends GameActivity {
    private static final String LOG_TAG = SwipeGameActivity.class.getSimpleName();

    private static final int MIN_THRESOLD =  300;
    private int x1, x2;
    private int y1, y2;
    private int swipeCount;
    public Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        state = GameActivity.GameState.GAME;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_game_activity);
        swipeCount = 0;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    // onTouchEvent () method gets called when User performs any touch event on screen
    // Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                x1 = (int) touchevent.getX();
                y1 = (int) touchevent.getY();
                Log.d(LOG_TAG, "x1 " + x1);
                Log.d(LOG_TAG, "y1 " + y1);
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = (int) touchevent.getX();
                y2 = (int) touchevent.getY();
                Log.d(LOG_TAG, "x2 " + x2);
                Log.d(LOG_TAG, "y2 " + y2);

                //if Down to UP sweep event on screen
                if (y1 > (y2 + MIN_THRESOLD)) {
                    swipeCount++;
                    Log.d(LOG_TAG, "swipe count: " + swipeCount);
                    gameDataRef.setValue(swipeCount);
                    vibrator.vibrate(100);
                }
                break;
            }
        }
        return false;
    }
}
