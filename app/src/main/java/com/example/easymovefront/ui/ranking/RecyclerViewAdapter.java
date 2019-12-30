package com.example.easymovefront.ui.ranking;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.RankingUser;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList<RankingUser> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ImageView imgView;
        public MyViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.rankTextView);
            imgView = (ImageView) v.findViewById(R.id.rankIcon);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(Context context, ArrayList<RankingUser> myDataset) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rankingtext, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int rank = position + 1;
        String text = String.valueOf(rank) + ". " + mDataset.get(position).getNom() + "\n   " + mDataset.get(position).getPuntuacio() + " points\n";
        holder.textView.setText(text);
        if (rank == 1)
            holder.imgView.setImageResource(R.drawable.first);
        else if (rank == 2)
            holder.imgView.setImageResource(R.drawable.second);
        else if (rank == 3)
            holder.imgView.setImageResource(R.drawable.third);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
