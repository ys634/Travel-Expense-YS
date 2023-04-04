package com.yut.travelexpense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class TripListActivity extends AppCompatActivity implements TripsApi {

    RecyclerView tripsRecView;
    TextView txtNoTrip;
    ArrayList<TripModel> trips = new ArrayList<>();
    private Toolbar toolbar;
    FloatingActionButton floatingBtn;

    private static final String TAG = "Trip List Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        Log.d(TAG, "onCreate");

        initComponents();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Trips List");


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripListActivity.this, AddTripActivity.class);
                intent.putExtra("action", "add");
                startActivity(intent);
            }
        });


        //TODO: show the text when there is no trip recorded.
        if (trips.size() == 0) {
            txtNoTrip.setVisibility(View.VISIBLE);
        } else {
            Collections.sort(trips);
            txtNoTrip.setVisibility(View.GONE);
            TripsRecViewAdapter adapter = new TripsRecViewAdapter(this, trips);
            tripsRecView.setAdapter(adapter);
            tripsRecView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void initComponents() {
        tripsRecView = findViewById(R.id.tripsRecView);
        txtNoTrip = findViewById(R.id.txtNoTrip);
        toolbar = findViewById(R.id.toolbar);
        floatingBtn = findViewById(R.id.floatingBtn);
        trips = Utils.getInstance(TripListActivity.this).getAllTrips();

    }

    @Override
    public void callBack() {
        trips = Utils.getInstance(TripListActivity.this).getAllTrips();

        TripsRecViewAdapter adapter = new TripsRecViewAdapter(this, trips);
        tripsRecView.setAdapter(adapter);
        tripsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}