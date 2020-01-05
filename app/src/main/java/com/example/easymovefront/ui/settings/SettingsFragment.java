package com.example.easymovefront.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.network.DeleteUserTask;
import com.example.easymovefront.ui.maps.RouteDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * It contains all preferences of the app.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    public static final String PREF_REMOVE_ACCOUNT = "removeAccount";
    public static final String PREF_FEEDBACK = "feedback";


    /**
     * Constructor of the class
     */
    public SettingsFragment(){}

    /**
     * Constructor with a given context
     * @param context
     */
    public SettingsFragment(Context context) {
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Prepares for deleting the logged user.
     * It delegates the task to signOut
     * @see #signOut(GoogleSignInClient)
     */
    private void deleteUser() {
        DeleteUserTask myTask = new DeleteUserTask(getContext());
        myTask.execute(LoggedUser.getInstance().getId());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(mContext.getApplicationContext(), gso);
        signOut(mGoogleSignInClient);
    }


    /**
     * It revokes the access of the google account from the app.
     * When revoked, it closes the app.
     * @param mGoogleSignInClient client from which it revokes the access
     * @see SettingsActivity#revokeAccess(GoogleSignInClient)
     */
    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        ((SettingsActivity)getActivity()).revokeAccess(mGoogleSignInClient);
        LoggedUser.getInstance().setmUserAccount(null);
        getActivity().finish();
    }


    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Preference preference = findPreference(PREF_REMOVE_ACCOUNT);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * When Remove Account preference clicked it generates an {@link AlertDialog}.
             * If confirm is pressed, it erase the user account from the app database, and executes deleteUser.
             * @param preference Remove Account Preference
             * @see SettingsFragment#deleteUser()
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Are you sure?");
                builder.setMessage("Your account will be deleted");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return true;
            }
        });

        Preference feedBackPreference = findPreference(PREF_FEEDBACK);
        feedBackPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * When Feedback Preference is clicked it executes sendEmail
             * @param preference Feedback Preference
             * @see SettingsFragment#sendEmail()
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendEmail();
                return true;
            }
        });
    }

    /**
     * Opens the mail app, chosen by the user, to send a mail.
     * It opens a predefined mail message, with subject and addressee fixed, and a body which works
     * as a hint.
     */
    private void sendEmail() {
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:?subject=" + "Feedback"+ "&body=" + "<i><b>Please, <br>" +
                "delete these lines and explain your issue or bug.</i></b>"
                + "&to=" + "easymovapp@gmail.com");
        mailIntent.setData(data);
        startActivity(Intent.createChooser(mailIntent, "Send mail..."));
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.activity_settings, rootKey);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
