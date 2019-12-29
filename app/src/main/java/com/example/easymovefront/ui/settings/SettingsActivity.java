package com.example.easymovefront.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {


    private SettingsFragment mSettingsFragment;
    private Toolbar mToolbar;
    private ProgressBar mloadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment(this))
                .commit();

        mToolbar = (Toolbar) findViewById(R.id.toolbarSettings);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


            setResult(Activity.RESULT_OK);
            CharSequence text = "Welcome";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
            //finish();
        } else {

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
