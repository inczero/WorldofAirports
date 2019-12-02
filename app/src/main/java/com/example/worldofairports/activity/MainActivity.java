package com.example.worldofairports.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
