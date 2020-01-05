package com.example.easymovefront.ui.settings;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.easymovefront.R;
import com.example.easymovefront.ui.maps.MapsActivity;
import com.example.easymovefront.ui.profile.ProfileActivity;
import com.example.easymovefront.ui.ranking.RankingActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;


/**
 * A simple class working as a container of {@link SettingsFragment}
 * @see SettingsFragment
 */
public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    private Toolbar mToolbar;
    DrawerLayout dLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProgressBar loadingDrawer;
    private ImageView drawerHeader;

    /**
     * Is executed when the settings menu is first opened.
     * It initializes the view and load the xml and the settingsFragment
     * @param savedInstanceState
     * @see SettingsFragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment(this))
                .commit();

        mToolbar = findViewById(R.id.toolbarSettings);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();

        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);
    }

    /**
     * Function needed for the interaction with {@link SettingsFragment}
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * It revokes the access of the google account from the app.
     * @param mGoogleSignInClient client from which it revokes the access
     */
    protected void revokeAccess(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {});
    }

    /**
     * Defines the {@link DrawerLayout} for the current screen.
     * It highlights the current menu and links the others to the respective screens
     */
    private void setNavigationDrawer() {
        dLayout = findViewById(R.id.drawer_layout_sett); // initiate a DrawerLayout
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
        NavigationView navView = findViewById(R.id.navigationSettings); // initiate a Navigation View
        // implement setNavigationItemSelectedListener event on NavigationView
        navView.getMenu().getItem(3).setChecked(true);
        navView.setNavigationItemSelectedListener(menuItem -> {
            loadingDrawer = findViewById(R.id.loadingDrawer);
            drawerHeader = findViewById(R.id.imageHeader);

            // check selected menu item's id and replace a Fragment Accordingly
            switch (menuItem.getItemId()) {
                case R.id.mapActivity:
                    drawerHeader.setVisibility(View.GONE);
                    loadingDrawer.setVisibility(View.VISIBLE);
                    Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(mapIntent);
                    finish();
                    return true;
                case R.id.profile:
                    drawerHeader.setVisibility(View.GONE);
                    loadingDrawer.setVisibility(View.VISIBLE);
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(profileIntent);
                    finish();
                    return true;
                case R.id.settings:
                    return true;
                case R.id.ranking:
                    drawerHeader.setVisibility(View.GONE);
                    loadingDrawer.setVisibility(View.VISIBLE);
                    Intent rankingIntent = new Intent(getApplicationContext(), RankingActivity.class);
                    startActivity(rankingIntent);
                    finish();
                    return true;
                default:
                    return false;
            }
        });
    }

    /**
     * Defines the functionality when the back button is pressed.
     * It opens the {@link DrawerLayout}
     */
    @Override
    public void onBackPressed() {
        if (!dLayout.isDrawerOpen(GravityCompat.START)) dLayout.openDrawer(GravityCompat.START);
    }

}
