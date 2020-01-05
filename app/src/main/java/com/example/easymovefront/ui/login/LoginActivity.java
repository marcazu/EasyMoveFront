package  com.example.easymovefront.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This activity contains all of Login screen.
 * It loads the xml. It has a login button, which is to login with google.
 */
public class LoginActivity extends AppCompatActivity {

    public static LoggedUser mUserAccount;
    private SharedPreferences pref;
    private ProgressBar loadingProgressBar;

    /**
     * Is executed when the login screen is opened.
     * It initializes the view and load the xml
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mUserAccount = LoggedUser.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         loadingProgressBar = findViewById(R.id.loading);

        final Button SignOut = findViewById(R.id.signOut_button);
        SignOut.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mUserAccount.setmGoogleSignInClient(GoogleSignIn.getClient(this, gso));

        mUserAccount.setmUserAccount(GoogleSignIn.getLastSignedInAccount(this));
        if (mUserAccount.getmUserAccount() != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
            LoggedUser.getInstance().setId(pref.getString("id", "n/a"));
            LoggedUser.getInstance().setToken(pref.getString("token", "n/a"));
            updateUI();
            try {
                updateBackend(mUserAccount);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            launchMaps();
        }
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            /**
             * If is clicked the sign in button it executes signIn
             * @param v View which is clicked
             * @see LoginActivity#signIn(GoogleSignInClient)
             */
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn(mUserAccount.getmGoogleSignInClient());
                        break;
                    // ...
                }
            }
        });

        findViewById(R.id.signOut_button).setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            switch (v.getId()) {
                case R.id.signOut_button:
                    signOut(mUserAccount.getmGoogleSignInClient());
                    break;
            }
        });

    }

    /**
     * Handles the google sign in task data.
     * It delegates the task to handleSignInResult
     * @param requestCode
     * @param resultCode
     * @param data
     * @see #handleSignInResult(Task)
     */
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

    /**
     * Updates the user interface depending if the user is logged or not.
     * It will display the login button or not
     */
    private void updateUI() {
        if (mUserAccount.getmUserAccount() != null){
            final SignInButton googleButton = findViewById(R.id.sign_in_button);
            googleButton.setVisibility(View.GONE);
            final Button signOutButton = findViewById(R.id.signOut_button);
            signOutButton.setVisibility(View.GONE);
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

    /**
     * Executes the google sign in task.
     * @param mGoogleSignInClient client which logs in
     */
    private void signIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {});
        revokeAccess(mGoogleSignInClient);
        mUserAccount.setmUserAccount(null);
        loadingProgressBar.setVisibility(View.GONE);
        updateUI();
    }
    private void revokeAccess(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {});
    }

    /**
     * Handles the data provided from google sign in task.
     * Saves the user in the database with updateBackend
     * When finished calls launchMaps
     * @param completedTask
     * @see #updateBackend(LoggedUser)
     * @see #launchMaps()
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            loadingProgressBar.setVisibility(View.GONE);
            mUserAccount.setmUserAccount(completedTask.getResult(ApiException.class));

            // Signed in successfully, show authenticated UI.
            updateUI();
            updateBackend(mUserAccount);
            pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("id", LoggedUser.getInstance().getId());
            edit.putString("token", LoggedUser.getInstance().getToken());
            edit.apply();
            launchMaps();
        } catch (Exception e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            CharSequence text = e.toString();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }

    /**
     * Starts a task to save the user to the database.
     * @param acc logged google account
     */
    private void updateBackend(LoggedUser acc) throws ExecutionException, InterruptedException, TimeoutException {
        GoogleSignInAccount aux = acc.getmUserAccount();
        UpdateUsersTask myTask = new UpdateUsersTask(this);
        myTask.execute(aux.getId(), aux.getEmail(), aux.getDisplayName(), aux.getPhotoUrl().toString());
        myTask.get();

    }

    /**
     * Open the maps screen.
     */
    public void launchMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }



}
