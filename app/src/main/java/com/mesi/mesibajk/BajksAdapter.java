package com.mesi.mesibajk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class BajksAdapter extends ArrayAdapter<Bajk> {

    public BajksAdapter(Context context, List<Bajk> bajks) {
        super(context, 0, bajks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        Bajk bajk = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bajk, parent, false);
        }

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.nameText);
        TextView tvStatus = convertView.findViewById(R.id.statusText);

        // Populate the data into the template view using the data object
        assert bajk != null;
        tvName.setText(bajk.getName());
        tvStatus.setText(bajk.getStatus() ? "Na voljo" : "Izposojeno");

        // Return the completed view to render on screen
        return convertView;

    }

}