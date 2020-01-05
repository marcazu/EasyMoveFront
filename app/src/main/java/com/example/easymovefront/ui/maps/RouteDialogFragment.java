package com.example.easymovefront.ui.maps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.easymovefront.R;

/**
 * This fragment is used to create a route to display on the maps fragment, which has a source and
 * a destionation; if the source is left empty it will use the user's current location
 */
public class RouteDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private EditText mEditText;
    private EditText mEditText2;
    private Context mContext;

    /**
     * This is used to communicate to the main activity that OK has been pressed
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOkPressed(String src, String dest);
    }

    /**
     * Constructor of the class
     * @param context is needed to know which activity this fragment belongs to
     */
    public RouteDialogFragment(Context context) {
        mContext = context;
    }

    /**
     * This handles the event listener for all the elements of the UI
     */
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
                .setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText src = editTextView.findViewById(R.id.source);
                        EditText dest = editTextView.findViewById(R.id.destination);
                        String test = src.getText().toString();
                        mListener.onOkPressed(test, dest.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
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
            mListener.onOkPressed(uri);
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

}
