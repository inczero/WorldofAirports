package com.example.worldofairports.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldofairports.R;
import com.example.worldofairports.adapter.SearchResultListAdapter;
import com.example.worldofairports.model.Airport;
import com.example.worldofairports.model.AirportAndDistance;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private double latitudeInputValue;
    private double longitudeInputValue;
    private int radiusInputValue;

    private TextView noSearchResultTextView;
    private ProgressBar searchProgressBar;
    private Button searchButton;

    private RecyclerView searchResultRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isOnline()) {
            setContentView(R.layout.activity_main_no_internet);
            Button exitButton = findViewById(R.id.exit_button);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            setContentView(R.layout.activity_main);
            noSearchResultTextView = findViewById(R.id.main_activity_no_search_result);
            noSearchResultTextView.setVisibility(View.GONE);

            //layout elements
            final EditText latitudeInputEditText = findViewById(R.id.latitude_input);
            final EditText longitudeInputEditText = findViewById(R.id.longitude_input);
            final EditText radiusInputEditText = findViewById(R.id.radius_input);
            searchButton = findViewById(R.id.search_button);
            searchProgressBar = findViewById(R.id.main_activity_progress_bar);

            //recycler view setup
            searchResultRecyclerView = findViewById(R.id.main_activity_recycler_view_search_result);
            RecyclerView.LayoutManager searchResultLayoutManager = new LinearLayoutManager(this);
            searchResultRecyclerView.setLayoutManager(searchResultLayoutManager);

            //on click event implementation on search button
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String latitudeInputString = latitudeInputEditText.getText().toString();
                    String longitudeInputString = longitudeInputEditText.getText().toString();
                    String radiusInputString = radiusInputEditText.getText().toString();

                    //region input data check

                    if (latitudeInputString.isEmpty()) {
                        latitudeInputEditText.setError("Please enter latitude in decimal degree!");
                        latitudeInputEditText.requestFocus();
                        return;
                    }

                    //latitude (in decimal degrees) should be in the interval [-90,90]
                    latitudeInputValue = Double.parseDouble(latitudeInputString);
                    if (!(latitudeInputValue >= -90 && latitudeInputValue <= 90)) {
                        latitudeInputEditText.setError("Latitude value is incorrect!");
                        latitudeInputEditText.requestFocus();
                        return;
                    }

                    if (longitudeInputString.isEmpty()) {
                        longitudeInputEditText.setError("Please enter longitude in decimal degree!");
                        longitudeInputEditText.requestFocus();
                        return;
                    }

                    //longitude (in decimal degrees) should be in the interval [-180,180]
                    longitudeInputValue = Double.parseDouble(longitudeInputString);
                    if (!(longitudeInputValue >= -180 && longitudeInputValue <= 180)) {
                        longitudeInputEditText.setError("Longitude value is incorrect!");
                        longitudeInputEditText.requestFocus();
                        return;
                    }

                    if (radiusInputString.isEmpty()) {
                        radiusInputEditText.setError("Please enter radius in kilometers!");
                        radiusInputEditText.requestFocus();
                        return;
                    }

                    radiusInputValue = Integer.parseInt(radiusInputString);

                    //endregion

                    //turn off search button until async task is finished
                    searchButton.setEnabled(false);

                    //query database with async task
                    new DatabaseQuery().execute(buildUrlForDatabaseQuery(latitudeInputValue, longitudeInputValue, radiusInputValue));
                }
            });

        }
    }

    //region check for internet connection
    public boolean isOnline() {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }
    //endregion

    //region URL builder for database query
    private String buildUrlForDatabaseQuery(double latitude, double longitude, int radius) {
        //1 degree of latitude -> 111 km
        double lengthInDegreeLatitude = radius / 111;
        lengthInDegreeLatitude = Math.round(lengthInDegreeLatitude);

        //1 degree of longitude -> cos(latitude)*111km
        double lengthOfOneDegreeLongitude = Math.cos(Math.toRadians(latitude)) * 111;
        double lengthInDegreeLongitude = radius / lengthOfOneDegreeLongitude;
        lengthInDegreeLongitude = Math.round(lengthInDegreeLongitude);

        int firstIntervalEnd = 0;
        int secondIntervalEnd = 0;

        //region preparing the interval end values for the LATITUDE part of the query
        if (latitude < 0) {
            firstIntervalEnd = (int) Math.round(latitude + lengthInDegreeLatitude);
            secondIntervalEnd = (int) Math.round(latitude - lengthInDegreeLatitude);

        } else {
            firstIntervalEnd = (int) Math.round(latitude - lengthInDegreeLatitude);
            secondIntervalEnd = (int) Math.round(latitude + lengthInDegreeLatitude);
        }

        String urlLatitudePart;
        if (firstIntervalEnd < 0) {
            urlLatitudePart = "-/" + Math.abs(firstIntervalEnd) + "%20TO%20";
        } else {
            urlLatitudePart = firstIntervalEnd + "%20TO%20";
        }

        if (secondIntervalEnd < 0) {
            urlLatitudePart += "-/" + Math.abs(secondIntervalEnd);
        } else {
            urlLatitudePart += secondIntervalEnd;
        }
        //endregion

        //region preparing the interval end values for the LONGITUDE part of the query
        if (longitude < 0) {
            firstIntervalEnd = (int) Math.round(longitude + lengthInDegreeLongitude);
            secondIntervalEnd = (int) Math.round(longitude - lengthInDegreeLongitude);

        } else {
            firstIntervalEnd = (int) Math.round(longitude - lengthInDegreeLongitude);
            secondIntervalEnd = (int) Math.round(longitude + lengthInDegreeLongitude);
        }

        String urlLongitudePart;
        if (firstIntervalEnd < 0) {
            urlLongitudePart = "-/" + Math.abs(firstIntervalEnd) + "%20TO%20";
        } else {
            urlLongitudePart = firstIntervalEnd + "%20TO%20";
        }

        if (secondIntervalEnd < 0) {
            urlLongitudePart += "-/" + Math.abs(secondIntervalEnd);
        } else {
            urlLongitudePart += secondIntervalEnd;
        }
        //endregion

        String test = "https://mikerhodes.cloudant.com/airportdb/_design/view1/_search/geo?q=lon:[" +
                urlLongitudePart + "]%20AND%20lat:[" + urlLatitudePart + "]";
        System.out.println(test);

        return test;
//        return "https://mikerhodes.cloudant.com/airportdb/_design/view1/_search/geo?q=lon:[" +
//                urlLongitudePart + "]%20AND%20lat:[" + urlLatitudePart + "]";
    }
    //endregion

    //region AsyncTask (this class will handle the database query in the background, separated from the UI thread)
    private class DatabaseQuery extends AsyncTask<String, Void, List<AirportAndDistance>> {

        @Override
        protected void onPreExecute() {
            searchProgressBar.setVisibility(View.VISIBLE);
            searchResultRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected List<AirportAndDistance> doInBackground(String... strings) {
            String databaseQueryUrlString = strings[0];

            String airportsDataJson = "";

            try {
                airportsDataJson = getJsonFromUrl(databaseQueryUrlString);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "An error happened!", Toast.LENGTH_LONG).show();
            }

            Airport[] airports = getDataFromJson(airportsDataJson);

            List<AirportAndDistance> airportsWithDistanceData = new ArrayList<>();

            double distanceFromCoordinates = 0;
            if (airports.length > 0) {
                for (int i = 0; i < airports.length; i++) {
                    distanceFromCoordinates = getDistanceFromLatLonInKm(airports[i].getAirportData().getLatitude(),
                            airports[i].getAirportData().getLongitude(), latitudeInputValue, longitudeInputValue);
                    if ((int) Math.round(distanceFromCoordinates) <= radiusInputValue) {
                        airportsWithDistanceData.add(new AirportAndDistance(airports[i], distanceFromCoordinates));
                    }
                }
            }

            Collections.sort(airportsWithDistanceData, new AirportAndDistance.SortByDistance());

            return airportsWithDistanceData;
        }

        @Override
        protected void onPostExecute(List<AirportAndDistance> airportsWithDistanceData) {
            searchProgressBar.setVisibility(View.GONE);

            if (airportsWithDistanceData.size() == 0) {
                noSearchResultTextView.setVisibility(View.VISIBLE);
            } else {
                noSearchResultTextView.setVisibility(View.GONE);
                searchResultRecyclerView.setVisibility(View.VISIBLE);

                RecyclerView.Adapter searchResultListAdapter = new SearchResultListAdapter(airportsWithDistanceData);
                searchResultRecyclerView.setAdapter(searchResultListAdapter);
            }

            //re-enable search button
            searchButton.setEnabled(true);
        }

        private String getJsonFromUrl(String url) throws IOException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                return sb.toString();
            } finally {
                is.close();
            }
        }

        private Airport[] getDataFromJson(String dataInJsonFormat) {
            int index = dataInJsonFormat.indexOf("[");

            dataInJsonFormat = dataInJsonFormat.substring(index, dataInJsonFormat.length() - 2);

            Airport[] airports = new GsonBuilder().create().fromJson(dataInJsonFormat, Airport[].class);

            return airports;
        }

        //distance calculator function based on haversine formula
        private double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
            double earthRadius = 6371;

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            double distance = earthRadius * c;

            //rounding up distance to 2 decimal places
            distance *= 100;
            distance = Math.round(distance);
            distance /= 100;

            return distance;
        }

    }
    //endregion
}
