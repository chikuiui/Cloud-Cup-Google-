package com.example.cloudcup.games;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.cloudcup.BitmapUtils;
import com.example.cloudcup.Consts;
import com.example.cloudcup.GameActivity;
import com.example.cloudcup.R;
import com.example.cloudcup.RoundedAvatarDrawable;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class EndOfGameActivity extends GameActivity {
    private static final String LOG_TAG = EndOfGameActivity.class.getSimpleName();

    // we need to show certain details when the game ends
    private DatabaseReference playersRef; // reference of players to show
    private ImageView winnerImage;
    private TextView winnerNameView;
    private DatabaseReference stateRef;
    //   private Firebase currentGameRef;


    class DownloadImageAsyncTask extends AsyncTask<Uri,Void, Bitmap>{
        private Uri uri;
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            uri = uris[0];
            // URI  scheme must be 'https' (for download images), 'content'(for shared images) or 'file'(for camera images)
            if(!(uri.getScheme().equals("http") || uri.getScheme().equals("content") || uri.getScheme().equals("file")))return null;

            try{
                return BitmapUtils.decodeBitmapBounded(BitmapUtils.getInputStream(EndOfGameActivity.this,uri),90,90);
            }catch (IOException e){
                Log.e(LOG_TAG,"Error reading bitmap",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            if(result != null){
                BitmapDrawable bitmap = new BitmapDrawable(result);
                winnerImage.setImageDrawable(new RoundedAvatarDrawable(result)); // original -> setImageResource
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        state = GameState.DONE;
        Log.d(LOG_TAG,"End of Game!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_of_game);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        winnerNameView = findViewById(R.id.winner_name);
        winnerImage = findViewById(R.id.winner_image);

//        playersRef = new Firebase(Consts.FIREBASE_URL + "/room/" + code + "/players");
        playersRef = database.getReference("room").child(code).child("players");

        playersRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            int maxScore = 0;
            String winnerName = "";
            String winnerImageUrl = "";
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Log.d(LOG_TAG, snapshot.getValue().toString());
                for (DataSnapshot player : snapshot.getChildren()) {
                    int score = Integer.parseInt(player.child("score").getValue().toString());
                    if(score > maxScore) {
                     maxScore = score;
                     winnerName = player.child("name").toString();
                     winnerImageUrl = player.child("imageUrl").getValue().toString();
                    }
                }
                Log.d(LOG_TAG,"winner is " + winnerName + "with score " + maxScore);
                winnerNameView.setText(winnerName + " won!");
                if(!winnerImageUrl.isEmpty()){
                    new DownloadImageAsyncTask().execute(Uri.parse(winnerImageUrl));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        stateRef  = database.getReference("room").child(code).child("state");

        final ImageButton button = findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateRef.setValue("restarted");
            }
        });
    }


}
