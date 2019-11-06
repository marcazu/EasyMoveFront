package  com.example.easymovefront.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.network.UpdateUsersTask;
import com.example.easymovefront.ui.maps.MapsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    public static LoggedUser mUserAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mUserAccount = LoggedUser.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        final Button SignOut = findViewById(R.id.signOut_button);
        SignOut.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mUserAccount.setmUserAccount(GoogleSignIn.getLastSignedInAccount(this));
        if (mUserAccount.getmUserAccount() != null) {
            updateUI();
            launchMaps();
        }
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn(mGoogleSignInClient);
                        break;
                    // ...
                }
            }
        });

        findViewById(R.id.signOut_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.signOut_button:
                        signOut(mGoogleSignInClient);
                        break;
                    // ...
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void updateUI() {
        if (mUserAccount.getmUserAccount() != null){
            final SignInButton googleButton = findViewById(R.id.sign_in_button);
            googleButton.setVisibility(View.GONE);
            final Button signOutButton = findViewById(R.id.signOut_button);
            signOutButton.setVisibility(View.VISIBLE);
            setResult(Activity.RESULT_OK);
            CharSequence text = "Welcome";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
            //finish();
        } else {
            findViewById(R.id.signOut_button).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

            setResult(Activity.RESULT_OK);
            CharSequence text = "Signed Out";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }

    private void signIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        revokeAccess(mGoogleSignInClient);
        mUserAccount.setmUserAccount(null);
        updateUI();
    }
    private void revokeAccess(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mUserAccount.setmUserAccount(completedTask.getResult(ApiException.class));

            // Signed in successfully, show authenticated UI.
            updateUI();
            updateBackend(mUserAccount);
            launchMaps();
        } catch (Exception e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            CharSequence text = e.toString();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
            //e.printStackTrace();
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void updateBackend(LoggedUser acc) {
        GoogleSignInAccount aux = acc.getmUserAccount();
        UpdateUsersTask myTask = new UpdateUsersTask(this);
        myTask.execute(aux.getId(), aux.getEmail(), aux.getDisplayName(), aux.getPhotoUrl().toString());

    }

    public void launchMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }



}
