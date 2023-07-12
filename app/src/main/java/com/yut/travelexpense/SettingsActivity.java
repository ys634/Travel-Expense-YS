package com.yut.travelexpense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.yut.travelexpense.databinding.ActivityMainBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MaterialCardView cardHomeCurrency, cardTheme, cardCategory2, cardDonate, cardReachOut,
            cardAbout, cardDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        binding = ActivityMainBinding.inflate(getLayoutInflater());


        //TODO: bottom navigation doesn't work
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.entryNav:
                    Bundle b1 = new Bundle();
                    b1.putString("action", "add");
                    EntryFragment fragment1 = new EntryFragment();
                    fragment1.setArguments(b1);
                    replaceFragment(fragment1);
                    break;
                case R.id.listNav:
                    Bundle b2 = new Bundle();
                    b2.putString("src", "mainActivity");
                    TransactionListFragment fragment2 = new TransactionListFragment();
                    fragment2.setArguments(b2);
                    replaceFragment(fragment2);
                    break;
                case R.id.statsNav:
                    replaceFragment(new StatsFragment());
                    break;
            }


            return true;
        });

        initComponents();

        initClickListeners();

    }



    private void initComponents() {

        cardHomeCurrency = findViewById(R.id.cardHomeCurrency);
        cardTheme = findViewById(R.id.cardTheme);
        cardCategory2 = findViewById(R.id.cardCategory2);
        cardDonate = findViewById(R.id.cardDonate);
        cardReachOut = findViewById(R.id.cardReachOut);
        cardAbout = findViewById(R.id.cardAbout);
        cardDeleteAll = findViewById(R.id.cardDeleteAll);
    }

    private void initClickListeners() {

        cardHomeCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardCategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, CategoryListActivity.class);
                startActivity(intent);
            }
        });

        cardDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardReachOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Are you sure you want to delete all the data? \nThis cannot be reversed.");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.getInstance(SettingsActivity.this).clearAll();
                        Intent intent = new Intent(SettingsActivity.this, AddTripActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setCancelable(true);
                builder.create().show();
            }
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameOriginal, fragment);
        fragmentTransaction.commit();
    }
}