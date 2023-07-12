package com.yut.travelexpense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import java.util.ArrayList;

public class CurrencyActivity extends AppCompatActivity {

    private static final String TAG = "Currency Activity";
    private Toolbar toolbar;
    CurrencyRecViewAdapter adapter;

    RecyclerView currencyRecView;
    MenuItem searchItem;
    SearchView searchView;
    ArrayList<CurrencyModel> currencyModels = new ArrayList<>();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        setUpToolBar();

        currencyRecView = findViewById(R.id.currencyRecView);

        setUpCurrencyModels();

        intent = getIntent();
        // Either from EntryFragment: src = entryFragment
        //             AddTripActivity: src = addTrip, name, startDate, endDate, budget


        if (intent != null) {
            String src = intent.getStringExtra("src");

            if (src.equals("entryFragment")) {

                adapter = new CurrencyRecViewAdapter(this, currencyModels, src);
//
//            } else if (src.equals("addTrip")) {
//                String name = intent.getStringExtra("name");
//                String startDate = intent.getStringExtra("startDate");
//                String endDate = intent.getStringExtra("endDate");
//                Double budget = intent.getDoubleExtra("budget", 0);
//
//                adapter = new CurrencyRecViewAdapter(this, currencyModels, src, name, startDate, endDate, budget);

            }

            currencyRecView.setAdapter(adapter);
            currencyRecView.setLayoutManager(new LinearLayoutManager(this));
        }
    }


    private void setUpCurrencyModels(){
        Log.d(TAG, "CurrencyModels setup");

        String[] currencyShort = getResources().getStringArray(R.array.currency_short_name);
        String[] currencyFull = getResources().getStringArray(R.array.currency_full_name);
        String[] symbols = getResources().getStringArray(R.array.currency_symbol);

        for (int i = 0; i < currencyShort.length; i++) {
            currencyModels.add(new CurrencyModel(currencyShort[i], currencyFull[i], symbols[i]));

        }
    }

    private void setUpToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Select a Currency");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        searchItem = menu.findItem(R.id.menuItemSearch);

        searchItem.setVisible(true).setEnabled(true);

        searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {

        String src = "";
        TripModel tempTrip = new TripModel();

        if (intent != null) {
            src = intent.getStringExtra("src");
            if (src.equals("addTrip")) {
                tempTrip = intent.getParcelableExtra("trip");
            }
        }

        Intent intentSending;

//        if (src.equals("addTrip")) {
//            intentSending = new Intent(this, AddTripActivity.class);
//            intentSending.putExtra("action", "continue");
//            intentSending.putExtra("name", intent.getStringExtra("name"));
//            intentSending.putExtra("startDate", intent.getStringExtra("startDate"));
//            intentSending.putExtra("endDate", intent.getStringExtra("endDate"));
//            intentSending.putExtra("budget", intent.getStringExtra("budget"));
//
//            startActivity(intentSending);
//        } else if (src.equals("entryFragment")) {
        if (src.equals("entryFragment")) {
            intentSending = new Intent(this, MainActivity.class);
            startActivity(intentSending);
        }
    }
}