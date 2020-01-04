package com.example.easymovefront.ui.maps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.CurrentBitmap;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.data.model.ObstacleMap;
import com.example.easymovefront.network.CreateImageFromUrlTask;
import com.example.easymovefront.network.CreateMarkerTask;
import com.example.easymovefront.network.GetMarkerTask;
import com.example.easymovefront.network.GetSingleMarkerTask;
import com.example.easymovefront.network.LikeObstacleTask;
import com.example.easymovefront.network.UpdateMarkerTask;
import com.google.android.gms.maps.model.Marker;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

public class DisplayObstacleFragment extends DialogFragment implements AsyncResponse {

    private JSONObject json;
    private String mId;
    private String mIdCreador;

    private Context mContext;
    private Marker mMarker;
    private LikeButton mLike;
    private LikeButton mDislike;
    private LikeButton mResolved;
    private LikeButton mEdit;

    private TextView mLikenumber;
    private TextView mDislikenumber;
    private String photoUrl;

    private ProgressBar mapsLoading;
    private ProgressBar obstacleLoading;

    private ImageView pic;
    private ImageView editpic;
    private TextView title;
    private TextView desc;
    private Button buttonCam;
    private EditText editTitle;
    private EditText editDesc;

    private Boolean isEditFinish = false;

    private Boolean isCameraEnabled = true;
    private Bitmap mPicture = null;
    private String mPicturePath;

    public DisplayObstacleFragment(Context context, Marker marker, ProgressBar loading) {
        mapsLoading = loading;
        mContext = context;
        mMarker = marker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        obtainMarkerID();
        getUpdatedMarker();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View editTextView = inflater.inflate(R.layout.fragment_display_obstacle, null);
        pic = editTextView.findViewById(R.id.obstacleView);
        editpic = editTextView.findViewById(R.id.editphotoView);
        title = editTextView.findViewById(R.id.titleObstacle);
        desc = editTextView.findViewById(R.id.descriptionObstacle);
        mLikenumber = editTextView.findViewById(R.id.likenumber);
        mDislikenumber = editTextView.findViewById(R.id.dislikenumber);
        mLike = editTextView.findViewById(R.id.likeButton);
        mDislike = editTextView.findViewById(R.id.dislikeButton);
        mResolved = editTextView.findViewById(R.id.resolvedObstacleButton);
        mEdit = editTextView.findViewById(R.id.editObstacleButton);
        obstacleLoading = editTextView.findViewById(R.id.loadingDisplayObstacle);
        updateEditButton();
        updateLikeStatus();
        mLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (mDislike.isLiked()) {
                    mDislike.setLiked(false);
                    updateLikeNumber("treuredislike");
                    LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                    myTask.execute(mId, "treuredislike", LoggedUser.getInstance().getId());
                }
                updateLikeNumber("like");
                LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                myTask.execute(mId, "like", LoggedUser.getInstance().getId());

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                updateLikeNumber("treurelike");
                LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                myTask.execute(mId, "treurelike", LoggedUser.getInstance().getId());
            }
        });
        mDislike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (mLike.isLiked()) {
                    mLike.setLiked(false);
                    updateLikeNumber("treurelike");
                    LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                    myTask.execute(mId, "treurelike", LoggedUser.getInstance().getId());
                }
                updateLikeNumber("dislike");
                LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                myTask.execute(mId, "dislike", LoggedUser.getInstance().getId());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                updateLikeNumber("treuredislike");
                LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                myTask.execute(mId, "treuredislike", LoggedUser.getInstance().getId());
            }
        });
        mResolved.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                myTask.execute(mId, "solucionar", LoggedUser.getInstance().getId());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                LikeObstacleTask myTask = new LikeObstacleTask(mContext);
                myTask.execute(mId, "truresolucionar", LoggedUser.getInstance().getId());
            }
        });
        mEdit.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                isEditFinish = false;
                editObstacle(editTextView);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (!isEditFinish) {
                    buttonCam.setVisibility(View.GONE);
                    obstacleLoading.setVisibility(View.VISIBLE);
                    isEditFinish = true;
                    updateObstacleBackend();
                }
            }
        });
        CreateImageFromUrlTask pictask = new CreateImageFromUrlTask(mContext);
        try {
            photoUrl = json.getString("foto");
            pictask.execute(photoUrl);
            Drawable d = pictask.get();
            pic.setImageDrawable(d);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            title.setText(json.getString("nom"));
            desc.setText(json.getString("descripcio"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setView(editTextView);

        mapsLoading.setVisibility(View.GONE);

        return builder.create();
    }

    @Override
    public void processFinish(String output) {
        editTitle.setVisibility(View.GONE);
        editDesc.setVisibility(View.GONE);
        buttonCam.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        mLike.setVisibility(View.VISIBLE);
        mLikenumber.setVisibility(View.VISIBLE);
        mDislike.setVisibility(View.VISIBLE);
        mDislikenumber.setVisibility(View.VISIBLE);
        mResolved.setVisibility(View.VISIBLE);
        if (pic != null) {
            pic.setVisibility(View.VISIBLE);
            editpic.setVisibility(View.GONE);
        }
        obstacleLoading.setVisibility(View.GONE);
    }

    @Override
    public void processFinish(JSONObject output) {

    }

    private void updateObstacleBackend() {
        String picUrl = photoUrl;
        if (mPicture != null) {
            CurrentBitmap.getInstance().setBitMap(mPicture);
            picUrl = "0";
        }
        UpdateMarkerTask myTask = new UpdateMarkerTask(mContext);
        myTask.asyncResponse = this;
        String editdesc = editDesc.getText().toString();
        String edittitle = editTitle.getText().toString();
        if (editdesc.equals("")) editdesc = desc.getText().toString();
        else desc.setText(editdesc);
        if (edittitle.equals("")) edittitle = title.getText().toString();
        else title.setText(edittitle);
        myTask.execute(editdesc, edittitle, mId, mIdCreador, String.valueOf(mMarker.getPosition().latitude), String.valueOf(mMarker.getPosition().longitude), picUrl);
    }

    private void editObstacle(View v) {
        pic.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        desc.setVisibility(View.GONE);
        mLike.setVisibility(View.GONE);
        mLikenumber.setVisibility(View.GONE);
        mDislike.setVisibility(View.GONE);
        mDislikenumber.setVisibility(View.GONE);
        mResolved.setVisibility(View.GONE);

        editTitle = v.findViewById(R.id.Edittitle);
        editTitle.setVisibility(View.VISIBLE);
        editDesc = v.findViewById(R.id.Editdescription);
        editDesc.setVisibility(View.VISIBLE);
        buttonCam = v.findViewById(R.id.Editpic);
        buttonCam.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            isCameraEnabled = false;
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        buttonCam.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File imgFile = new  File(mPicturePath);
            if(imgFile.exists()){
                pic = null;
                mPicture = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                editpic.setImageBitmap(mPicture);
                editpic.setVisibility(View.VISIBLE);
                buttonCam.setText("CHANGE PHOTO");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                isCameraEnabled = true;
            }
        }
    }

    private void updateEditButton() {
        if (!LoggedUser.getInstance().getId().equals(mIdCreador)) {
            mEdit.setVisibility(View.GONE);
        }
    }

    private void updateLikeNumber(String type) {
        Integer number;
        if (type == "like") {
            number = Integer.parseInt(mLikenumber.getText().toString());
            number++;
            mLikenumber.setText(String.valueOf(number));
        } else if (type == "treurelike") {
            number = Integer.parseInt(mLikenumber.getText().toString());
            number--;
            mLikenumber.setText(String.valueOf(number));
        } else if (type == "dislike") {
            number = Integer.parseInt(mDislikenumber.getText().toString());
            number++;
            mDislikenumber.setText(String.valueOf(number));
        } else if (type == "treuredislike") {
            number = Integer.parseInt(mDislikenumber.getText().toString());
            number--;
            mDislikenumber.setText(String.valueOf(number));
        }
    }

    private void getUpdatedMarker() {
        GetSingleMarkerTask myTask = new GetSingleMarkerTask(mContext);
        myTask.execute(mId);
        try {
            String result = myTask.get();
            json = new JSONObject(result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateLikeStatus() {
        try {
            JSONArray jarray = json.getJSONArray("usuarisLike");
            mLikenumber.setText(String.valueOf(jarray.length()));
            for (int i = 0; i < jarray.length(); i++) {
                if (jarray.getInt(i) == Integer.parseInt(LoggedUser.getInstance().getId())) {
                    mLike.setLiked(true);
                    break;
                }
            }
            jarray = json.getJSONArray("usuarisDisLike");
            mDislikenumber.setText(String.valueOf(jarray.length()));
            if (!mLike.isLiked()) {
                for (int i = 0; i < jarray.length(); i++) {
                    if (jarray.getInt(i) == Integer.parseInt(LoggedUser.getInstance().getId())) {
                        mDislike.setLiked(true);
                        break;
                    }
                }
            }
            jarray = json.getJSONArray("usuarisResolt");
            if (!mResolved.isLiked()) {
                for (int i = 0; i < jarray.length(); i++) {
                    if (jarray.getInt(i) == Integer.parseInt(LoggedUser.getInstance().getId())) {
                        mResolved.setLiked(true);
                        break;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtainMarkerID() {
        json = ObstacleMap.getInstance().getMap().get(mMarker);
        try {
            mId = json.getString("id");
            mIdCreador = json.getString("idUsuariCreador");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_obstacle, container);
        return view;
    }
}