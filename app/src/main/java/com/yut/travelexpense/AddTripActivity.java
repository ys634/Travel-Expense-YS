package com.yut.travelexpense;

import static com.yut.travelexpense.Utils.convertNumberToMonth;
import static com.yut.travelexpense.Utils.formatter;
import static com.yut.travelexpense.Utils.removeZero;
import static com.yut.travelexpense.Utils.round;
import static com.yut.travelexpense.Utils.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddTripActivity extends AppCompatActivity {

    private EditText edtTxtTripName, edtTxtBudget;
    private TextView txtStartDate, txtEndDate, txtTripNameWarning, txtLengthSummary, txtBudgetSummary;
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


        if (intent == null) {
            Log.d(TAG, "Intent is null");

        } else {
            String action = intent.getStringExtra("action");

            setUpClickListeners(action);
//            setUpTextChangedListeners();
            setUpTypeListeners();

//            if (action.equals("edit") || action.equals("continue")) {

            long duration = 7;
            double dailyBudget = 0;

            if (action.equals("edit")) {
                int id = intent.getIntExtra("tripId", -1);
                tripInProgress = Utils.getInstance(AddTripActivity.this).searchTripById(id);
                if (tripInProgress != null) {
                    Toast.makeText(this, "id: " + tripInProgress.getId(), Toast.LENGTH_SHORT).show();

                    toolbar.setTitle("Edit Trip");
                    btnAddTrip.setText("Edit Trip");
                    edtTxtTripName.setText(tripInProgress.getName());
                    edtTxtBudget.setText(String.valueOf(tripInProgress.getBudget()));
                    txtStartDate.setText(tripInProgress.getStartDate().format(formatter));
                    txtEndDate.setText(tripInProgress.getEndDate().format(formatter));
                    duration = ChronoUnit.DAYS.between(tripInProgress.getStartDate(),
                            tripInProgress.getEndDate()) + 1;

                } else {
                    Log.d(TAG, "tripInProgress is null");
                }
//                } else {// action.equals("continue")
//                    edtTxtTripName.setText(intent.getStringExtra("name"));
//                    txtStartDate.setText(intent.getStringExtra("startDate"));
//                    txtEndDate.setText(intent.getStringExtra("endDate"));
//                    edtTxtBudget.setText(String.valueOf(intent.getDoubleExtra("budget", 0)));
            } else {
                txtStartDate.setText(today.format(formatter));
                txtEndDate.setText(today.plusDays(6).format(formatter));
            }
            txtLengthSummary.setText("Trip length: " + duration + " days");

            setUpClickListeners(action);
            setUpTypeListeners();

        }
    }


    public void initComponents() {

        edtTxtTripName = findViewById(R.id.edtTxtTripName);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        edtTxtBudget = findViewById(R.id.edtTxtBudget);
        btnAddTrip = findViewById(R.id.btnAddTrip);
        txtTripNameWarning = findViewById(R.id.txtTripNameWarning);
        btnHome = findViewById(R.id.btnHome);
        txtLengthSummary = findViewById(R.id.txtLengthSummary);
        txtBudgetSummary = findViewById(R.id.txtBudgetSummary);

    }

    private void setUpClickListeners(String action) {

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(Pair.create(LocalDateTime.of
                                        (LocalDate.parse(txtStartDate.getText().toString(), formatter),
                                                LocalTime.NOON).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                LocalDateTime.of
                                        (LocalDate.parse(txtEndDate.getText().toString(), formatter),
                                                LocalTime.NOON).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()))
                        .build();

                datePicker.show(getSupportFragmentManager(), "Material_date_picker");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        TimeZone timeZoneUTC = TimeZone.getDefault();
                        int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                        Date startDate = new Date(selection.first + offsetFromUTC);
                        Date endDate = new Date(selection.second + offsetFromUTC);

                        txtStartDate.setText(simpleFormat.format(startDate));
                        txtEndDate.setText(simpleFormat.format(endDate));

                        long duration = TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime() + 1, TimeUnit.MILLISECONDS) + 1;
                        String durationText = "Duration of trip: " + duration + " days";

                        txtLengthSummary.setText(durationText);

                        if (!edtTxtBudget.getText().toString().isBlank()) {
                            long dailyBudget = Long.parseLong(edtTxtBudget.getText().toString()) / duration;
                            String budgetText = "Daily Budget: $" + dailyBudget;
                            txtBudgetSummary.setText(budgetText);
                        }
                    }
                });
            }
        });



        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(Pair.create(
                                LocalDateTime.of
                                        (LocalDate.parse(txtStartDate.getText().toString(), formatter),
                                                LocalTime.NOON).
                                        atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                LocalDateTime.of
                                        (LocalDate.parse(txtEndDate.getText().toString(), formatter),
                                                LocalTime.NOON).
                                        atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()))
                        .build();

                datePicker.show(getSupportFragmentManager(), "Material_date_picker");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        TimeZone timeZoneUTC = TimeZone.getDefault();
                        int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                        Date startDate = new Date(selection.first + offsetFromUTC);
                        Date endDate = new Date(selection.second + offsetFromUTC);

                        txtStartDate.setText(simpleFormat.format(startDate));
                        txtEndDate.setText(simpleFormat.format(endDate));

                        long duration = TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime() + 1, TimeUnit.MILLISECONDS) + 1;
                        String durationText = "Duration of trip: " + duration + " days";

                        txtLengthSummary.setText(durationText);

                        if (!edtTxtBudget.getText().toString().isBlank()) {
                            long dailyBudget = Long.parseLong(edtTxtBudget.getText().toString()) / duration;
                            String budgetText = "Daily Budget: $" + dailyBudget;
                            txtBudgetSummary.setText(budgetText);
                        }


                    }
                });
            }
        });


        TripModel finalTrip = tripInProgress;
        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocalDate startDate = LocalDate.parse(txtStartDate.getText().toString(), formatter);
                LocalDate endDate = LocalDate.parse(txtEndDate.getText().toString(), formatter);

                if (!edtTxtTripName.getText().toString().isBlank()) {
                    // The trip has valid dates entered and trip name is not blank
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
                            new ArrayList<>(),
                            false,
                            startDate.isBefore(today) && endDate.isAfter(LocalDate.now()));


                    // Add this trip to the list of trips and update the last trip ID if we are not editing an existing trip.
                    if (action.equals("add") || action.equals("continue")) {
                        Toast.makeText(AddTripActivity.this, "new trip created. ID: " + tripId, Toast.LENGTH_SHORT).show();

                        Utils.getInstance(AddTripActivity.this).setLastTripID(tripId);
                    } else if (action.equals("edit")) {
                        Toast.makeText(AddTripActivity.this, "Trip edited. ID: " + tripId, Toast.LENGTH_SHORT).show();
                        Utils.getInstance(AddTripActivity.this).removeTrip(finalTrip);
                    }

                    Utils.getInstance(AddTripActivity.this).addTrip(newTrip);


                    Intent intent = new Intent(AddTripActivity.this, TripListActivity.class);
                    startActivity(intent);

                } else {

                    txtTripNameWarning.setVisibility(View.VISIBLE);
                    Toast.makeText(AddTripActivity.this,
                            "Enter a trip name", Toast.LENGTH_SHORT).show();

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

    private void setUpTypeListeners() {

        RxTextView.textChanges(edtTxtBudget)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textChanged -> {
                    String budgetText = "";
                    String inputText = edtTxtBudget.getText().toString().trim();

                    if (!inputText.isBlank() && inputText.matches("\\d+(\\.\\d+)?")) {
                        LocalDate startDate = LocalDate.parse(txtStartDate.getText(), formatter);
                        LocalDate endDate = LocalDate.parse(txtEndDate.getText(), formatter);
                        double dailyBudget = Double.parseDouble(edtTxtBudget.getText().toString()) /
                                (ChronoUnit.DAYS.between(startDate, endDate) + 1);
                        budgetText = "Daily Budget: $" + removeZero(round(dailyBudget, 2));
                    } else {
                        budgetText = "Daily Budget: -";
                    }
                    txtBudgetSummary.setText(budgetText);

                    }, throwable -> {
                        Log.e("TAG", "Error occurred: " + throwable.getMessage());
                    });

    }
}