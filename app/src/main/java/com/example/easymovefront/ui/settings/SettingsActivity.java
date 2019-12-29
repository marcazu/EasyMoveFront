package com.example.easymovefront.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.ui.maps.MapsActivity;
import com.example.easymovefront.ui.profile.ProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {


    private SettingsFragment mSettingsFragment;
    private Toolbar mToolbar;
    private ProgressBar mloadingBar;
    DrawerLayout dLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.settings_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment(this))
                .commit();

        mToolbar = (Toolbar) findViewById(R.id.toolbarSettings);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();

        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);



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

    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout_sett); // initiate a DrawerLayout
        mDrawerToggle = new ActionBarDrawerToggle(this, dLayout, mToolbar,R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        dLayout.addDrawerListener(mDrawerToggle);
        NavigationView navView = (NavigationView) findViewById(R.id.navigationSettings); // initiate a Navigation View
        // implement setNavigationItemSelectedListener event on NavigationView
        navView.getMenu().getItem(2).setChecked(true);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                CharSequence text;
                int duration;
                Toast toast;
                // check selected menu item's id and replace a Fragment Accordingly
                switch (menuItem.getItemId()) {
                    case R.id.mapActivity:
                        Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(mapIntent);
                        finish();
                        return true;
                    case R.id.profile:
                        text = "PROFILE PLACEHOLDER";
                        duration = Toast.LENGTH_LONG;
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);

                        toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                        return true;
                    case R.id.settings:
                        text = "SETTINGS PLACEHOLDER";
                        duration = Toast.LENGTH_LONG;

                        toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
