package com.example.locationlistener;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class initialPage extends AppCompatActivity {
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);
        start = findViewById(R.id.START);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMeasurement();
            }
        });

    }

    public void openMeasurement(){
        Intent initiate = new Intent (this,GPSTracker.class);
        startActivity(initiate);
    }

}