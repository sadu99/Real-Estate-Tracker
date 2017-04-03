package real_estate_tracker.realestatetrackerandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements NetworkOperations.Listener {

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private NetworkOperations mNetworkOperations;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();

        mNetworkOperations = new NetworkOperations(this,this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String name = user.getDisplayName().toString();
                    String email = user.getEmail().toString();
                    String id = user.getUid();
                    try {
                        mNetworkOperations.addUser(name,id,email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    // User is signed in
                    Log.d("firebase:signed_in:", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("firebase:signed_out:", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        setContentView(R.layout.activity_login);
        mImage = (ImageView) findViewById(R.id.image);
        mImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_landscape_black_24dp));
        mImage.setColorFilter(getResources().getColor(R.color.blue));
        mLoginButton = (LoginButton)findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("email", "public_profile");
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());

                Log.d("SUCCESS", "SUCCESS");
            }

            @Override
            public void onCancel() {
                Log.d("CANCEL", "CANCEL");
                Toast.makeText(LoginActivity.this,"Login attempt canceled",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("ERROR", e.getMessage());
                Toast.makeText(LoginActivity.this,"Login attempt failed.",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("handleFBAccessToken", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signInWithCredential", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("signInWithCredential", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onGetFavouritesSuccess(ArrayList<PropertyObject> response) throws JSONException {

    }

    @Override
    public void onAddFavouritesSuccess(String response) throws JSONException {

    }

    @Override
    public void onSearchSuccess(ArrayList<PropertyObject> response) throws JSONException {

    }

    @Override
    public void onLoginSuccess(String response) {
        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
    }
}
