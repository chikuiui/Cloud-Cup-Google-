package com.example.cloudcup.games;


import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import com.example.cloudcup.GameActivity;
import com.example.cloudcup.R;


public class SequenceGameActivity extends GameActivity {
    private static final String LOG_TAG = SequenceGameActivity.class.getSimpleName();
    private Button blueButton;
    private Button redButton;
    private Button greenButton;
    private Button yellowButton;
    private String sequence = "";
    public Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        state = GameState.GAME;
        sequence = "";
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sequence_game);

        blueButton = (Button) findViewById(R.id.blue_button);
        redButton = (Button) findViewById(R.id.red_button);
        greenButton = (Button) findViewById(R.id.green_button);
        yellowButton = (Button) findViewById(R.id.yellow_button);

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick("b");
            }
        });
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick("r");
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick("g");
            }
        });
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick("y");
            }
        });
    }

    public void onButtonClick(String letter) {
        sequence += letter;
        vibrator.vibrate(100);
        gameDataRef.setValue(sequence);
    }
}
