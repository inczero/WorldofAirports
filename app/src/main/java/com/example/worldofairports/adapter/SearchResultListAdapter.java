package com.example.worldofairports.adapter;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.worldofairports.R;
import com.example.worldofairports.model.Airport;

import java.util.Locale;

public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListAdapter.SearchResultViewHolder> {

    private ArrayMap<Airport, Double> airportsWithDistanceData;

    public SearchResultListAdapter(ArrayMap<Airport, Double> airportsWithDistanceData) {
        this.airportsWithDistanceData = airportsWithDistanceData;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.name.setText(airportsWithDistanceData.keyAt(position).getAirportData().getName());
        holder.latitude.setText(Double.toString(airportsWithDistanceData.keyAt(position).getAirportData().getLatitude()));
        holder.longitude.setText(Double.toString(airportsWithDistanceData.keyAt(position).getAirportData().getLongitude()));
        holder.distanceToUserCoordinates.setText(Double.toString(airportsWithDistanceData.valueAt(position)));
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_search_result_list_row, parent, false);
        return new SearchResultViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return airportsWithDistanceData.size();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView latitude;
        private TextView longitude;
        private TextView distanceToUserCoordinates;

        SearchResultViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.search_result_text_view_name);
            latitude = itemView.findViewById(R.id.search_result_text_view_latitude);
            longitude = itemView.findViewById(R.id.search_result_text_view_longitude);
            distanceToUserCoordinates = itemView.findViewById(R.id.search_result_text_view_distance);
        }
    }
}
