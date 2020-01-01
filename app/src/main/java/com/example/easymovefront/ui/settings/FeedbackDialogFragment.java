package com.example.easymovefront.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.easymovefront.R;

public class FeedbackDialogFragment extends DialogFragment {

    private FeedbackDialogFragment.OnFragmentInteractionListener mListener;
    private EditText mEditText;
    private EditText mEditText2;
    private Context mContext;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOkPressed(String body);
    }

    public FeedbackDialogFragment(Context context) {
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View editTextView = inflater.inflate(R.layout.fragment_feedback_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(editTextView)
                // Add action buttons
                .setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText body = editTextView.findViewById(R.id.body_mail);
                        mListener.onOkPressed(body.getText().toString());
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
        View view = inflater.inflate(R.layout.fragment_feedback_dialog, container);
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
        if (context instanceof FeedbackDialogFragment.OnFragmentInteractionListener) {
            mListener = (FeedbackDialogFragment.OnFragmentInteractionListener) context;
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
        EditText body = v.findViewById(R.id.body_mail);
        mListener.onOkPressed(body.getText().toString());
        this.dismiss();
        return true;
    }


    /*public void getText(View v) {
        if (mListener != null) {
            EditText src = v.findViewById(R.id.source);
            EditText dest = v.findViewById(R.id.destination);
            mListener.onOkPressed(src.getText().toString(), dest.getText().toString());
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