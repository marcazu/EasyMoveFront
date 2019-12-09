package com.example.easymovefront.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.preference.RingtonePreference;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {


    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment(this))
                .commit();


        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return;

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SettingsFragment(this))
                    .commit();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        
    }

    void updateUI() {
        if (LoggedUser.getInstance().getmUserAccount() != null){
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

    void revokeAccess(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

/*

    private static void bindSummaryValue(Preference preference) {
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }


/*

    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {




            /*String stringValue = newValue.toString();
            if (preference instanceof Preference){
                Preference pref = (ListPreference) preference;
                int index = pref;
                // Set the summary to reflect the new value
                preference.setSummary(index >0 ? listPreference.getEntries()[index]
                        :null);
            } else if (preference instanceof EditTextPreference) {
                preference.setSummary(stringValue);
            }
            return false;
        }
    };*/
}
