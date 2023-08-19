package com.example.cloudcup.games;



import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cloudcup.GameActivity;
import com.example.cloudcup.R;


public class MathGameActivity extends GameActivity {
    private static final String LOG_TAG = MathGameActivity.class.getSimpleName();

    private EditText resultInput;

    @Override
    public void onCreate(Bundle savedInstanceState){
        state = GameState.GAME; // game start

        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_game_activity);

        resultInput = findViewById(R.id.result);

        // when any action is performed on resultInput.
        resultInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(resultInput.getWindowToken(),0);

                resultInput.setEnabled(false);
                resultInput.setFocusable(false);
                resultInput.setClickable(false);

                String codeValue = resultInput.getText().toString();
                gameDataRef.setValue(codeValue);
                return true; // true-> if you have consumed the action.
            }
        });
        resultInput.requestFocus();
    }
}
