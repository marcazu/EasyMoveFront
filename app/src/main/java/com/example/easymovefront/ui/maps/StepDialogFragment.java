package com.example.easymovefront.ui.maps;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.easymovefront.R;

import java.util.Collections;
import java.util.List;

import androidx.fragment.app.DialogFragment;



class StepDialogFragment extends DialogFragment {

    private Context mContext;
    private List<String> values;
    public StepDialogFragment(MapsActivity mapsActivity, List<String> steps) {
        mContext = mapsActivity;
        Collections.copy(values,steps); // values = steps
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_dialog, container, false);

        ListView lstView = (ListView) rootView.findViewById(R.id.listSteps);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
        lstView.setAdapter(adapter);

        // TODO: adapter.setOnItemClickListener
        //    ... handle click
        //    ... load webpage

        return rootView;
    }
}
