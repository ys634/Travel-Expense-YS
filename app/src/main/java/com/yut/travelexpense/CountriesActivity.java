package com.yut.travelexpense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Objects;

public class CountriesActivity extends AppCompatActivity {


    RecyclerView countriesRecView;
    private Toolbar toolbar;
    ArrayList<CountryModel> countryModels = new ArrayList<>();
    CountriesRecViewAdapter adapter;
    SearchView searchView;
    MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Select a Country");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
//        Transaction transaction = intent.getParcelableExtra("transaction");
        countriesRecView = findViewById(R.id.countriesRecView);

        setUpCountryModels();


        adapter = new CountriesRecViewAdapter(this, countryModels);
        countriesRecView.setAdapter(adapter);
        countriesRecView.setLayoutManager(new LinearLayoutManager(this));
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




    private void setUpCountryModels(){
        String[] countryNames = getResources().getStringArray(R.array.countries_name);

        for (String countryName : countryNames) {
            countryModels.add(new CountryModel(countryName));
        }
    }
}