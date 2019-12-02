package com.example.worldofairports.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.worldofairports.R;

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

            //layout elements
            final EditText latitudeInputEditText = findViewById(R.id.latitude_input);
            final EditText longitudeInputEditText = findViewById(R.id.longitude_input);
            final EditText radiusInputEditText = findViewById(R.id.radius_input);
            Button searchButton = findViewById(R.id.search_button);

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
                    if (!(longitudeInputValue >= -90 && longitudeInputValue <= 90)) {
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

                    
                }
            });

        }
    }

    //Check for internet connection
    public boolean isOnline() {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
