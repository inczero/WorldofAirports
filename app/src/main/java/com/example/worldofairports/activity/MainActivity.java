package com.example.worldofairports.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.worldofairports.R;
import com.example.worldofairports.model.Airport;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
            TextView noSearchResultTextView = findViewById(R.id.main_activity_no_search_result);
            noSearchResultTextView.setVisibility(View.GONE);

            //asynctask
            final DatabaseQuery databaseQueryAsyncTask = new DatabaseQuery();

            //layout elements
            final EditText latitudeInputEditText = findViewById(R.id.latitude_input);
            final EditText longitudeInputEditText = findViewById(R.id.longitude_input);
            final EditText radiusInputEditText = findViewById(R.id.radius_input);
            Button searchButton = findViewById(R.id.search_button);

            //on click event implementation on search button
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //checking if database query is already running in the background
                    if (databaseQueryAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                        return;
                    }

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
                    double latitudeInputValue = Double.parseDouble(latitudeInputString);
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
                    double longitudeInputValue = Double.parseDouble(longitudeInputString);
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

                    int radiusInputValue = Integer.parseInt(radiusInputString);

                    //endregion

                    //query database with async task
                    databaseQueryAsyncTask.execute(buildUrlForDatabaseQuery(latitudeInputValue, longitudeInputValue, radiusInputValue));
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
        double lengthInDegreeLatitude = radius/111;
        lengthInDegreeLatitude = Math.round(lengthInDegreeLatitude);

        //1 degree of longitude -> cos(latitude)*111km
        double lengthOfOneDegreeLongitude = Math.cos(Math.toRadians(latitude))*111;
        double lengthInDegreeLongitude = radius/lengthOfOneDegreeLongitude;
        lengthInDegreeLongitude = Math.round(lengthInDegreeLongitude);

        int firstIntervalEnd = 0;
        int secondIntervalEnd = 0;

        //preparing the interval end values for the latitude part of the query
        if (latitude < 0) {
            firstIntervalEnd = (int) Math.round(latitude + lengthInDegreeLatitude);
            secondIntervalEnd = (int) Math.round(latitude - lengthInDegreeLatitude);

        } else {
            firstIntervalEnd = (int) Math.round(latitude - lengthInDegreeLatitude);
            secondIntervalEnd = (int) Math.round(latitude + lengthInDegreeLatitude);
        }
        String urlLatitudePart = firstIntervalEnd + "%20TO%20" + secondIntervalEnd;

        //preparing the interval end values for the longitude part of the query
        if (longitude < 0) {
            firstIntervalEnd = (int) Math.round(longitude + lengthInDegreeLongitude);
            secondIntervalEnd = (int) Math.round(longitude - lengthInDegreeLongitude);

        } else {
            firstIntervalEnd = (int) Math.round(longitude - lengthInDegreeLongitude);
            secondIntervalEnd = (int) Math.round(longitude + lengthInDegreeLongitude);
        }
        String urlLongitudePart = firstIntervalEnd + "%20TO%20" + secondIntervalEnd;

        return "https://mikerhodes.cloudant.com/airportdb/_design/view1/_search/geo?q=lon:[" +
                urlLongitudePart + "]%20AND%20lat:[" + urlLatitudePart + "]";
    }
    //endregion

    //region AsyncTask (this class will handle the database query in the background, separated from the UI thread)
    private class DatabaseQuery extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            List<Airport> airports;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    //endregion
}
