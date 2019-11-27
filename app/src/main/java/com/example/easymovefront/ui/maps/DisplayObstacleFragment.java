package com.example.easymovefront.ui.maps;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.data.model.ObstacleMap;
import com.example.easymovefront.network.CreateImageFromUrlTask;
import com.example.easymovefront.network.CreateMarkerTask;
import com.example.easymovefront.network.GetMarkerTask;
import com.example.easymovefront.network.GetSingleMarkerTask;
import com.example.easymovefront.network.LikeObstacleTask;
import com.google.android.gms.maps.model.Marker;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DisplayObstacleFragment extends DialogFragment {

    private JSONObject json;
    private String mId;
    private String mIdCreador;
    
    private Context mContext;
    private Marker mMarker;
    private LikeButton mLike;
    private LikeButton mDislike;
    private LikeButton mResolved;

    private TextView mLikenumber;
    private TextView mDislikenumber;

    private ProgressBar mapsLoading;

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
        ImageView pic = editTextView.findViewById(R.id.obstacleView);
        TextView title = editTextView.findViewById(R.id.titleObstacle);
        TextView desc = editTextView.findViewById(R.id.descriptionObstacle);
        mLikenumber = editTextView.findViewById(R.id.likenumber);
        mDislikenumber = editTextView.findViewById(R.id.dislikenumber);
        mLike = editTextView.findViewById(R.id.likeButton);
        mDislike = editTextView.findViewById(R.id.dislikeButton);
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
        CreateImageFromUrlTask pictask = new CreateImageFromUrlTask(mContext);
        try {
            pictask.execute(json.getString("foto"));
            Drawable d = pictask.get();
            pic.setImageDrawable(d);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        title.setText(mMarker.getTitle());
        desc.setText(mMarker.getSnippet());
        builder.setView(editTextView);

        mapsLoading.setVisibility(View.GONE);

        return builder.create();
    }

    private void updateLikeNumber(String type) {
        Integer number;
        if (type == "like") {
            number = Integer.parseInt(mLikenumber.getText().toString());
            number++;
            mLikenumber.setText(String.valueOf(number));
        }
        else if (type == "treurelike") {
            number = Integer.parseInt(mLikenumber.getText().toString());
            number--;
            mLikenumber.setText(String.valueOf(number));
        }
        else if (type == "dislike") {
            number = Integer.parseInt(mDislikenumber.getText().toString());
            number++;
            mDislikenumber.setText(String.valueOf(number));
        }
        else if (type == "treuredislike") {
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
            for(int i=0; i<jarray.length(); i++) {
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