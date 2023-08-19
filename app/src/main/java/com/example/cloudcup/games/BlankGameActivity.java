package com.example.cloudcup.games;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cloudcup.Consts;
import com.example.cloudcup.GameActivity;
import com.example.cloudcup.ImageAdapter;
import com.example.cloudcup.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*  A default activity that extends GameActivity  which  decided which game should be started .*/
public class BlankGameActivity extends GameActivity {
    private static final String LOG_TAG = BlankGameActivity.class.getSimpleName();

    private DatabaseReference stateReference;
    private DatabaseReference playersReference;

    private ImageAdapter adapter;
    GridView gridView;
    private List<String> playerImageUrls = new ArrayList<String>();
    private List<String> playerNames = new ArrayList<String>();

    /**
     * Fragment that displays a grid of players joining the game.
     */
//    public static class PlayersListFragment extends Fragment {
//        @Override
//        public View onCreateView(
//                LayoutInflater inflater,
//                ViewGroup container,
//                Bundle savedInstanceState) {
//            // Inflate the layout for this fragment
//            return inflater.inflate(R.layout.fragment_players_list, container, false);
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        state = GameState.GAME;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_game_activity);

        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new ImageAdapter(this);
        gridView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        stateReference = database.getReference("room").child(code).child("state");

        final ImageButton button = (ImageButton) findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When clicking on Join, set the state of the room from "not-started" to "waiting".
                stateReference.setValue("waiting");
            }
        });

        playersReference = database.getReference("room").child(code).child("players");

        playersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerImageUrls.clear();
                playerNames.clear();

                for(DataSnapshot child : snapshot.getChildren()){
                    String playerName = child.child("name").getValue(String.class);
                    String playerImageUrl = child.child("imageUrl").getValue(String.class);

                    if (playerImageUrl != null && !playerImageUrls.contains(playerImageUrl)) {
                        playerImageUrls.add(playerImageUrl);
                        playerNames.add(playerName);
                    }
                }

                adapter.setImageUrls(playerImageUrls);
                adapter.setNames(playerNames);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}