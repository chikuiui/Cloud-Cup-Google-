package com.example.cloudcup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cloudcup.games.BlankGameActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageView userImage;
    private TextView username;
    private EditText code;

    private static final int RC_SIGN_IN = 0;

    private GoogleSignInClient gsc;

    private String uName ;
    private Uri pUri;


    // register data in firebase realtime database using this.
    private DatabaseReference databaseReference;

    // to get firebase auth
    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        code = findViewById(R.id.code);
        userImage = findViewById(R.id.user_image);
        username = findViewById(R.id.username);


        // IMPLEMENT GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);

        // check if the user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            uName = account.getDisplayName();
            pUri = account.getPhotoUrl();

            username.setText(uName);
            new DownloadImageAsyncTask().execute(pUri);
            code.requestFocus();
        }else{
            // HANDLE SIGN IN CLICK.
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent,RC_SIGN_IN);
        }

        code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                join();
                return true;
            }
        });
        code.requestFocus();
    }


    public void join(){
        Intent intent = new Intent(this, BlankGameActivity.class);
        String codeValue = code.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();

        String playerName;
        String imageUrl;
        if (user != null) {
            Log.d(LOG_TAG, "User is signed in");
            playerName = user.getDisplayName();
            imageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
        } else {
            Log.d(LOG_TAG, "User is signed in not");
            Random rand = new Random();
            playerName = "Anonymous " + rand.nextInt(10);
            imageUrl = "";
        }

        // generate a unique key for the new player registration
        DatabaseReference playerRef = databaseReference.child("room").child(codeValue).child("players");
        String key = databaseReference.push().getKey();
        Player player = new Player(playerName,imageUrl,0);
        assert key != null;
        playerRef.child(key).setValue(player);

        intent.putExtra("playerId",key);
        intent.putExtra("playerName",uName);
        intent.putExtra("code",codeValue);

        startActivity(intent);
    }



    protected void onStart(){
        super.onStart();
    }

    protected void onStop(){
        super.onStop();
        // GOOGLE SIGN OUT.
        gsc.signOut();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // RETRIEVE USER DETAILS
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // *******************************************************************************
            // this code update firebase auth to sign in
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Authentication successful, FirebaseUser is now signed in
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                // Proceed with further actions
                            } else {
                                // Authentication failed
                                Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            }
                        }
                    });
            //*******************************************************************************
            uName = account.getDisplayName();
            pUri = account.getPhotoUrl();

            username.setText(uName);
            new DownloadImageAsyncTask().execute(pUri);
            code.requestFocus();

        }catch (ApiException e){
            Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    class DownloadImageAsyncTask extends AsyncTask<Uri, Void, Bitmap> {
        private Uri uri;

        @Override
        protected Bitmap doInBackground(Uri... params) {
            uri = params[0];
            // URI scheme must be 'https' (for downloaded images), 'content' (for shared images), or
            // 'file' (for camera images).
            if (!(uri.getScheme().equals("https") || uri.getScheme().equals("content")
                    || uri.getScheme().equals("file"))) {
                return null;
            }
            try {
                return BitmapUtils.decodeBitmapBounded(
                        BitmapUtils.getInputStream(MainActivity.this, uri), 90, 90);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error reading bitmap", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                BitmapDrawable bitmap = new BitmapDrawable(result);
                userImage.setImageDrawable(new RoundedAvatarDrawable(result));
            }
        }
    }

}