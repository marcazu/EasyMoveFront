package com.example.easymovefront.ui.maps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easymovefront.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * This fragment is used to create an obstacle, which has a title, description and a picture
 */
public class ObstacleDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private EditText mEditText;
    private EditText mEditText2;
    private Context mContext;
    private ImageView img;
    private Button camerabutton;
    private Boolean isCameraEnabled = true;
    private Bitmap mPicture = null;
    private String mPicturePath;

    /**
     * This is used to communicate to the main activity that OK has been pressed
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOkPressedObstacle(String pos, String desc, Bitmap foto, String title);
    }

    /**
     * Constructor of the class
     * @param context is needed to know which activity this fragment belongs to
     */
    public ObstacleDialogFragment(Context context) {
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
        final View editTextView = inflater.inflate(R.layout.fragment_obstacle_dialog, null);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            isCameraEnabled = false;
            ActivityCompat.requestPermissions(this.getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        camerabutton = editTextView.findViewById(R.id.button_callcamera);
        img = editTextView.findViewById(R.id.imageviewPhoto);
        camerabutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isCameraEnabled) {
                    File dir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);
                    mPicturePath = dir.getAbsolutePath() + "/" + "obstacle.png";
                    File file = new File(mPicturePath);
                    Uri outputfile = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().
                            getPackageName() + ".provider", file);
                    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile );
                    startActivityForResult(takePictureIntent, 1);

                }
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(editTextView)
                // Add action buttons
                .setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //EditText loc = editTextView.findViewById(R.id.location);
                        EditText desc = editTextView.findViewById(R.id.description);
                        EditText photo = null; //editTextView.findViewById(R.id.photo);
                        EditText tit = editTextView.findViewById(R.id.title);
                        String locat = "";
                        String description = desc.getText().toString();
                        String photography = null;
                        String title = tit.getText().toString();
                        boolean valid = true;
                        CharSequence text = "";
                        if (mPicture == null) {
                            valid = false;
                            text = getString(R.string.missingPhoto);
                        }
                        if (title.equals("")) {
                            valid = false;
                            text = getString(R.string.missingTitle);
                        }
                        if (description.equals("")) {
                            valid = false;
                            text = getString(R.string.missingDesc);
                        }
                        if (!valid) {
                            int duration;
                            Toast toast;
                            duration = Toast.LENGTH_LONG;

                            toast = Toast.makeText(mContext, text, duration);
                            toast.show();
                        }
                        else {
                            mListener.onOkPressedObstacle(locat, description, mPicture, title);
                        }
                    }
                })
                .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    /**
     * Handles the camera return response
     * @param requestCode not used, always 1
     * @param resultCode if the call was successful or not
     * @param data the intent data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File imgFile = new  File(mPicturePath);
            if(imgFile.exists()){
                mPicture = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                img.setImageBitmap(mPicture);
                img.setVisibility(View.VISIBLE);
                camerabutton.setText("CHANGE PHOTO");
            }
        }
    }

    /**
     * Handles permissions response of camera
     * @param requestCode not used
     * @param permissions number of permissions
     * @param grantResults if the permission was granted or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                isCameraEnabled = true;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_obstacle_dialog, container);
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
