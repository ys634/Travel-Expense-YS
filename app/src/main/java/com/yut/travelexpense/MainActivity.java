package com.yut.travelexpense;

import static com.yut.travelexpense.Utils.round;
import static com.yut.travelexpense.Utils.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.yut.travelexpense.CurrencyConversion.RetrofitBuilder;
import com.yut.travelexpense.CurrencyConversion.RetrofitInterface;
import com.yut.travelexpense.databinding.ActivityMainBinding;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//TODO: set flags
// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//TODO: passing data across activities
// putExtra(Parcelable)

// Token: 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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Toolbar toolbar;
    private static final String TAG = "Main Activity";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.darkTheme:
//                setTheme(R.style.Theme_TravelExpenseYS_Night);
//                recreate();
//                return true;
//            case R.id.lightTheme:
//                setTheme(R.style.Theme_TravelExpenseYS);
//                recreate();
//                return true;
//            case R.id.changeHomeCurrency:
//                Toast.makeText(MainActivity.this, "Working on it as well", Toast.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Utils.getInstance(this).clearAll();


        boolean isUserRegistered;

        boolean isTripRecorded = Utils.getInstance(this).getAllTrips().size() > 0;

        // Run whenever category icons are messed up
//        Utils.getInstance(MainActivity.this).updateCategories();

        // Check if there is any trip recorded
        if (!isTripRecorded) {
            Intent intent = new Intent(MainActivity.this, AddTripActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        } else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setUpToolBar();


            Bundle bundle = new Bundle();

            Intent intent = getIntent();
            if (intent != null) {
                if (Objects.equals(intent.getStringExtra("action"), "continue")) {
                    bundle.putString("action", "continue");
                } else {
                    bundle.putString("action", "add");

                }
            }

            EntryFragment entryFragment = new EntryFragment();
            entryFragment.setArguments(bundle);
            replaceFragment(entryFragment);

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {

                Utils.getInstance(MainActivity.this).removeOnResumeData();

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
                    case R.id.settingsNav:
                        Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                    break;
                }


                return true;
            });
        }
    }



    private void setUpToolBar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(Utils.getInstance(this).getCurrentTrip().getName());

        toolbar.setOnClickListener(view -> {
            Utils.getInstance(MainActivity.this).removeOnResumeData();
            Intent intent = new Intent(MainActivity.this, TripListActivity.class);
            startActivity(intent);

        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameOriginal, fragment);
        fragmentTransaction.commit();
    }


}