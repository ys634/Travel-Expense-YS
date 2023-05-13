package com.yut.travelexpense;

import static com.yut.travelexpense.MainActivity.round;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity {

    private EditText edtTxtTripName, edtTxtBudget;
    private TextView txtStartDate, txtEndDate, txtHomeCurrency, txtTripNameWarning, txtDateWarning;
    private Button btnAddTrip;
    Toolbar toolbar;
    FloatingActionButton btnHome;
    private static final String TAG = "Add Trip Activity";

    private TripModel tripInProgress;

    final Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

    CurrencyModel homeCurrency;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("Add a new trip");

        homeCurrency = Utils.getInstance(AddTripActivity.this).getHomeCurrency();

        initComponents();


        Intent intent = getIntent();
        // From TripListActivity: action = add
        //      TripRecViewAdapter: action = edit, tripId = id;
        //      CurrencyRecViewAdapter: action = continue, name, startDate, endDate, budget


        if (intent != null) {
            String action = intent.getStringExtra("action");

            if (action.equals("edit") || action.equals("continue")) {
                if (action.equals("edit")) {
                    int id = intent.getIntExtra("tripId", -1);
                    tripInProgress = Utils.getInstance(AddTripActivity.this).searchTripById(id);
                    if (tripInProgress != null) {
                        Toast.makeText(this, "budget: " + round(tripInProgress.getBudget(), 0), Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "start date: " + tripInProgress.getStartDate().toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "end date: " + tripInProgress.getEndDate().toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "id: " + tripInProgress.getId(), Toast.LENGTH_SHORT).show();
                        homeCurrency = tripInProgress.getHomeCurrency();

                        toolbar.setTitle("Edit Trip");
                        edtTxtTripName.setText(tripInProgress.getName());
                        edtTxtBudget.setText(String.valueOf(tripInProgress.getBudget()));
                        txtStartDate.setText(tripInProgress.getStartDate().format(formatter));
                        txtEndDate.setText(tripInProgress.getEndDate().format(formatter));

                    } else {
                        Log.d(TAG, "tripInProgress is null");
                    }
                } else {// action.equals("continue")
                    edtTxtTripName.setText(intent.getStringExtra("name"));
                    txtStartDate.setText(intent.getStringExtra("startDate"));
                    txtEndDate.setText(intent.getStringExtra("endDate"));
                    edtTxtBudget.setText(String.valueOf(intent.getDoubleExtra("budget", 0)));
                }
            }

            txtHomeCurrency.setText(homeCurrency.getShortName());

            setUpClickListeners(action);

        } else {
            Log.d(TAG, "Intent is null");
        }
    }


    public void initComponents() {

        edtTxtTripName = findViewById(R.id.edtTxtTripName);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        edtTxtBudget = findViewById(R.id.edtTxtBudget);
        txtHomeCurrency = findViewById(R.id.txtHomeCurrency);
        btnAddTrip = findViewById(R.id.btnAddTrip);
        txtTripNameWarning = findViewById(R.id.txtTripNameWarning);
        txtDateWarning = findViewById(R.id.txtDateWarning);
        btnHome = findViewById(R.id.btnHome);
    }


    private void setUpClickListeners(String action) {

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(AddTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        String date = m + 1 + "/" + d + "/" + y;
                        txtStartDate.setText(date);

                    }
                }, year, month, day);
                dialog.show();
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(AddTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        String date = m + 1 + "/" + d + "/" + y;
                        txtEndDate.setText(date);

                    }
                }, year, month, day);
                dialog.show();
            }
        });

        txtHomeCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTripActivity.this, "home currency clicked", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(AddTripActivity.this, CurrencyActivity.class);

                intent.putExtra("name", edtTxtTripName.getText().toString());
                intent.putExtra("startDate", txtStartDate.getText().toString());
                intent.putExtra("endDate", txtEndDate.getText().toString());
                intent.putExtra("budget", Double.parseDouble(edtTxtBudget.getText().toString()));

                //TODO: better way to communicate
//                TripModel tempTrip = new TripModel();
//                tempTrip.setName(edtTxtTripName.getText().toString());
//                if (!txtStartDate.getText().toString().isBlank()) {
//                    tempTrip.setStartDate(LocalDate.parse(txtStartDate.getText().toString(), formatter));
//                }
//                if (!txtEndDate.getText().toString().isBlank()) {
//                    tempTrip.setEndDate(LocalDate.parse(txtEndDate.getText().toString(), formatter));
//                }
//                if (!edtTxtBudget.getText().toString().isBlank()) {
//                    tempTrip.setBudget(Double.parseDouble(edtTxtBudget.getText().toString()));
//                }

//                tempTrip.setHomeCurrency(homeCurrency);
//                intent.putExtra("trip", tempTrip);
                intent.putExtra("src", "addTrip");
                startActivity(intent);
            }
        });


        TripModel finalTrip = tripInProgress;
        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocalDate startDate;
                LocalDate endDate;

                startDate = LocalDate.parse(txtStartDate.getText().toString(), formatter);
                endDate = LocalDate.parse(txtEndDate.getText().toString(), formatter);

                if (startDate.isBefore(endDate) && !edtTxtTripName.getText().toString().isBlank()) {
                    // The trip has valid dates entered and trip name is not blank
                    txtDateWarning.setVisibility(View.GONE);
                    txtTripNameWarning.setVisibility(View.GONE);

                    int tripId;
                    if (action.equals("edit")) {
                        assert finalTrip != null;
                        tripId = finalTrip.getId();
                    } else {
                        int lastId = Utils.getInstance(AddTripActivity.this).getLastTripID();
                        tripId = lastId + 1;
                    }

                    TripModel newTrip = new TripModel(edtTxtTripName.getText().toString(),
                            startDate,
                            endDate,
                            Double.parseDouble(edtTxtBudget.getText().toString()),
                            tripId,
                            homeCurrency,
                            new ArrayList<>(),
                            false,
                            startDate.isBefore(LocalDate.now()) && endDate.isAfter(LocalDate.now()));



                    // Add this trip to the list of trips and update the last trip ID if we are not editing an existing trip.
                    if (action.equals("add") || action.equals("continue")) {
                        Toast.makeText(AddTripActivity.this, "new trip created. ID: " + tripId, Toast.LENGTH_SHORT).show();

                        Utils.getInstance(AddTripActivity.this).addTrip(newTrip);

                        Utils.getInstance(AddTripActivity.this).setLastTripID(tripId);
                    } else if (action.equals("edit")) {
                        Toast.makeText(AddTripActivity.this, "Trip edited. ID: " + tripId, Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(AddTripActivity.this, TripListActivity.class);
                    startActivity(intent);

                } else {

                    if (startDate.isAfter(endDate)) {
                        txtDateWarning.setVisibility(View.VISIBLE);
                        Toast.makeText(AddTripActivity.this,
                                "Start date has to be before the end date", Toast.LENGTH_SHORT).show();
                    }

                    if (edtTxtTripName.getText().toString().isBlank()) {
                        txtTripNameWarning.setVisibility(View.VISIBLE);
                        Toast.makeText(AddTripActivity.this,
                                "Enter a trip name", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTripActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}