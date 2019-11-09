package com.example.easymovefront.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.ui.maps.MapsActivity;

import okhttp3.Route;


public class RouteDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private EditText mEditText;
    private EditText mEditText2;
    private Context mContext;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String src, String dest);
    }

    public RouteDialogFragment(Context context) {
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View editTextView = inflater.inflate(R.layout.fragment_route_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(editTextView)
                // Add action buttons
                .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText src = editTextView.findViewById(R.id.source);
                        EditText dest = editTextView.findViewById(R.id.destination);
                        String test = src.getText().toString();
                        mListener.onFragmentInteraction(test, dest.getText().toString());
                    }
                })
                .setNegativeButton(R.string.app_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_dialog, container);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

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

    /*@Override*/
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // Return input text to activity
        EditText src = v.findViewById(R.id.source);
        EditText dest = v.findViewById(R.id.destination);
        mListener.onFragmentInteraction(src.getText().toString(), dest.getText().toString());
        this.dismiss();
        return true;
    }


    /*public void getText(View v) {
        if (mListener != null) {
            EditText src = v.findViewById(R.id.source);
            EditText dest = v.findViewById(R.id.destination);
            mListener.onFragmentInteraction(src.getText().toString(), dest.getText().toString());
        }
    }^/

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
}
