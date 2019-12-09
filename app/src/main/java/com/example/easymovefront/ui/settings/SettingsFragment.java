package com.example.easymovefront.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.network.DeleteUserTask;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    public static final String PREF_REMOVE_ACCOUNT = "removeAccount";
    public static final String PREF_ORIGIN_COLOR = "origin_color";
    public static final String PREF_DESTINATION_COLOR = "destination_color";

    public SettingsFragment(Context context) {
        mContext = context;
    }


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @ param param1 Parameter 1.
     * @ param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if (key.equals(PREF_REMOVE_ACCOUNT)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Title");
                    builder.setMessage("Message");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteUser();
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                }
            }
        };

    }



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
        //((SettingsActivity)getActivity())loadingProgressBar.setVisibility(View.GONE);
        ((SettingsActivity)getActivity()).updateUI();
        getActivity().finish();
    }




    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Preference orgColor = findPreference(PREF_ORIGIN_COLOR);
        Preference dstColor = findPreference(PREF_DESTINATION_COLOR);

        //orgColor.setDefaultValue(getPreferenceScreen().getSharedPreferences().getString(PREF_ORIGIN_COLOR,""));
        //dstColor.setDefaultValue(getPreferenceScreen().getSharedPreferences().getString(PREF_DESTINATION_COLOR,""));

        Preference preference = findPreference(PREF_REMOVE_ACCOUNT);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
                                Toast.makeText(getContext(), "Inside", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
