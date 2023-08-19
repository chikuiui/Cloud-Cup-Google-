package com.example.cloudcup;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cloudcup.games.EndOfGameActivity;
import com.example.cloudcup.games.MathGameActivity;
import com.example.cloudcup.games.SequenceGameActivity;
import com.example.cloudcup.games.ShakingGameActivity;
import com.example.cloudcup.games.SwipeGameActivity;
import com.example.cloudcup.games.TappingGameActivity;
import com.example.cloudcup.games.TurnGameActivity;
import com.example.cloudcup.games.WaitingActivity;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;



public class GameActivity extends AppCompatActivity {
    // for checking log
    private static final String LOG_TAG = GameActivity.class.getSimpleName();

    //enum class is special class that refers to group of constants(like final variables.)always uppercase letters.
    public enum GameState{
        WAITING,GAME,DONE;
    }



    // Players information's
    protected String code;
    protected String playerID;
    protected Intent currentIntent;
    protected String gameType = "";
    protected String currentGame;
    protected GameState state;

    // Getting database References through firebase
    private DatabaseReference gameTypeRef;
    protected DatabaseReference gameDataRef;

    // called when the activity is first called.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        // get the room code -> where players can enter if they type this room code
        code = getIntent().getStringExtra("code");
        Log.d(LOG_TAG,"Room code "+code);

        // get player id
        playerID = getIntent().getStringExtra("playerId");
        Log.d(LOG_TAG,"player id "+playerID);

        // current game is not null then get "number" otherwise -1 we can also use if else
        currentGame = getIntent().getStringExtra("number") != null ? getIntent().getStringExtra("number") : "-1";

        currentIntent = getIntent();

        gameDataRef = FirebaseDatabase.getInstance().getReference("room").child(code).child("games")
                .child(currentGame).child("data").child(playerID);

        // reference to the current game.
        DatabaseReference currentGameRef = FirebaseDatabase.getInstance().getReference("room" ).child(code);

        // listen to state changes ,if display a progress dialog if "waiting"
        currentGameRef.child("state").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    if(snapshot.getValue().toString().equals("waiting") && !state.equals(GameState.WAITING)){
                        openWaitingRoom();
                    }else if(snapshot.getValue().toString().equals("done") && !state.equals(GameState.DONE)){
                        openEndOfGameRoom();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // listen to currentGame changes to change game activity
          currentGameRef.child("currentGame").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(snapshot.getValue() != null && !snapshot.getValue().toString().equals("-1") &&
                     !currentGame.equals(snapshot.getValue().toString())){
                      currentGame = snapshot.getValue().toString();
                      gameTypeRef = FirebaseDatabase.getInstance().getReference("room").child(code).child("games").child(currentGame);
                      gameTypeRef.child("type").addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              if (snapshot.getValue() != null &&
                                    !snapshot.getValue().toString().isEmpty()) {
                                Log.d(LOG_TAG, "gameType is now " + snapshot.getValue());
                                gameType = (String) snapshot.getValue();
                                startGame();
                            }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {

                          }
                      });
                      startGame();
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
    }

    private void startGame(){
        if(gameType == null || gameType.isEmpty())return;
        if(currentGame.equals("-1"))return;
        if(currentIntent != null && currentIntent.getStringExtra("number") != null &&
                currentIntent.getStringExtra("number").equals(currentGame))return;

        HashMap<String, Class> gameMapping = new HashMap<String, Class>();
        gameMapping.put("math", MathGameActivity.class);
        gameMapping.put("tap", TappingGameActivity.class);
        gameMapping.put("shake", ShakingGameActivity.class);
        gameMapping.put("swipe", SwipeGameActivity.class);
        gameMapping.put("turn", TurnGameActivity.class);
        gameMapping.put("sequence", SequenceGameActivity.class);

        Class<?> cls = gameMapping.get(gameType);

        if(cls != null){
            openRoom(cls,playerID,code,currentGame);
        }else{
            Log.e(LOG_TAG,"Game Type unknown "+gameType);
        }
    }

    //
    private void openWaitingRoom(){
        openRoom(WaitingActivity.class,playerID,code,currentGame);
    }
    // when game is ended it will call EndOfGameActivity class and show the result.
    private void openEndOfGameRoom(){
        openRoom(EndOfGameActivity.class,playerID,code,currentGame);
    }

    // it will send intent to waitingActivity or EndOfGameActivity based on situations.
    private void openRoom(Class<?> cls, String playerID,String code,String currentGame){
        Intent intent = new Intent(this,cls);
        intent.putExtra("playerId",playerID);
        intent.putExtra("code",code);
        intent.putExtra("number",currentGame);
        startActivity(intent);
        finish();
    }

}
