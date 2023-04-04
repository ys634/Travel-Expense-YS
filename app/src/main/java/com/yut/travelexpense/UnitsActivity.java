package com.yut.travelexpense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Objects;

public class UnitsActivity extends AppCompatActivity {

    private static final String TAG = "Units Activity";
    private Toolbar toolbar;
    UnitsRecViewAdapter adapter;

    RecyclerView unitsRecView;
    MenuItem searchItem;
    SearchView searchView;
    ArrayList<UnitModel> unitModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Select a Unit");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unitsRecView = findViewById(R.id.unitsRecView);

        setUpUnitModels();

        adapter = new UnitsRecViewAdapter(this, unitModels);
        unitsRecView.setAdapter(adapter);
        unitsRecView.setLayoutManager(new LinearLayoutManager(this));


    }


    //TODO: Add toolbar w/ search icon

    private void setUpUnitModels(){
        Log.d(TAG, "UnitModels setup");

        String[] unitShort = getResources().getStringArray(R.array.units_short_name);
        String[] unitFull = getResources().getStringArray(R.array.units_full_name);

        for (int i = 0; i<unitShort.length; i++) {
            unitModels.add(new UnitModel(unitShort[i], unitFull[i]));

        }
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
}