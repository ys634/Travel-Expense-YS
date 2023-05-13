package com.yut.travelexpense;

import static com.yut.travelexpense.MainActivity.formatter;
import static com.yut.travelexpense.MainActivity.round;
import static com.yut.travelexpense.MainActivity.today;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.yut.travelexpense.CurrencyConversion.RetrofitBuilder;
import com.yut.travelexpense.CurrencyConversion.RetrofitInterface;

import java.time.LocalDate;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EntryFragment extends Fragment {
    private static final String TAG = "Entry fragment";
    View view;

    private EditText edtTxtAmount, edtTxtDescription;
    private Button btnEnter, btnCurrency, btnCategoryEditor;
    private TextView txtDate, txtCountry;
    Bundle bundle;
    String action;
    CategoryFragment categoryFragment;
    TripModel currentTrip;


    final Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

    //TODO: remove this button
    private Button btnClear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_entry, container, false);

        categoryFragment = new CategoryFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameCatInMain, categoryFragment);
        fragmentTransaction.commit();


        bundle = this.getArguments();
        if (bundle != null) {
            action = bundle.getString("action");

            // action will either be:
            // "add"
            // "edit"
            // "continue"
        }
        Toast.makeText(getContext(), "action: " + action, Toast.LENGTH_SHORT).show();



        currentTrip = Utils.getInstance(getContext()).getCurrentTrip();

        initComponents(action);
//        initCategories();

        initClickListeners();

        String countryPreference = Utils.getInstance(getContext()).getCountryPreference();
        if (countryPreference != null & !countryPreference.equals("")) {
            txtCountry.setText(countryPreference);
        }

        String currencyPreference = Utils.getInstance(getContext()).getCurrencyPreference();
        if (!currencyPreference.isEmpty()) {
            btnCurrency.setText(currencyPreference);
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (action.equals("continue")) {
            edtTxtAmount.setText(Utils.getInstance(getContext()).getOnResumeAmount());
            txtDate.setText(Utils.getInstance(getContext()).getOnResumeDate());
            edtTxtDescription.setText(Utils.getInstance(getContext()).getOnResumeDescription());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("action").equals("edit")) {
                menu.findItem(R.id.menuItemDelete).setVisible(true).setEnabled(true);
            } else {
                menu.findItem(R.id.menuItemDelete).setVisible(false).setEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuItemDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete this transaction?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Transaction incomingTransaction = bundle.getParcelable("transaction");

                        Utils.getInstance(getContext()).removeTransaction(incomingTransaction);


                        //TODO:                         notifyItemRemoved(holder.getAdapterPosition());
                        //                        notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());??
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("src", "entry");
                        TransactionListFragment fragment = new TransactionListFragment();
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.frameOriginal, fragment);
                        fragmentTransaction.commit();
                    }
                });

                builder.setCancelable(true);
                builder.create().show();

                break;
            case R.id.menuItemMore:
                //TODO: More icon on toolbar
                Toast.makeText(getContext(), "Working on it", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // Initialize components
    private void initComponents(String action) {
        Log.d(TAG, "initComponents: Started");

        edtTxtAmount = view.findViewById(R.id.edtTxtAmount);
        edtTxtDescription = view.findViewById(R.id.edtTxtDescription);
        btnEnter = view.findViewById(R.id.btnEnter);
        btnCurrency = view.findViewById(R.id.btnCurrency);
        btnCategoryEditor = view.findViewById(R.id.btnCategoryEditor);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtDate = view.findViewById(R.id.txtDate);

//        rrGroupCategories = view.findViewById(R.id.rrGroupCategories);


        //TODO: Remove below this
        btnClear = view.findViewById(R.id.btnClear);


        txtCountry.setText(Utils.getInstance(getContext()).getCountryPreference());
        btnCurrency.setText(Utils.getInstance(getContext()).getCurrencyPreference());

        if (action.equals("edit") || action.equals("continue")) {
            setHasOptionsMenu(true);

            if (action.equals("edit")) {
                Transaction incomingTransaction = Utils.getInstance(getContext()).
                        searchTransactionById(bundle.getInt("transactionId"));

                edtTxtAmount.setText(String.valueOf(incomingTransaction.getOriginalAmount()));
                edtTxtDescription.setText(incomingTransaction.getDescription());
                txtCountry.setText(incomingTransaction.getCountry());
                btnCurrency.setText(incomingTransaction.getCurrency());
                if (incomingTransaction.getDate() != null) {
                    txtDate.setText(incomingTransaction.getDate().format(formatter));
                }

                btnEnter.setText("Edit");

            }
        }
    }


    private void initClickListeners () {

        // Country button OnClickListener
        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = "";
                if (!edtTxtAmount.getText().toString().isBlank()) {
                    amount = edtTxtAmount.getText().toString();
                }

                Utils.getInstance(getContext()).putOnResume(amount,
                        txtDate.getText().toString(), edtTxtDescription.getText().toString());

                Intent intent = new Intent(getActivity(), CountriesActivity.class);


                startActivity(intent);

            }
        });


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDate.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.white_border, null));

                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        String date = m + 1 + "/" + d + "/" + y;
                        txtDate.setText(date);

                    }
                }, year, month, day);
                dialog.show();
            }
        });

        // Enter button OnClickListener
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.getInstance(getContext()).removeOnResumeData();

                LocalDate entryDate;
                String text = txtDate.getText().toString();

                if (text.equals("Today")) {
                    entryDate = today;
                } else {

                    entryDate = LocalDate.parse(text, formatter);
                }


                int rdbId = categoryFragment.getSelectedRbdId();

                if (rdbId == -1) {  // No category is selected.
                    categoryFragment.changeBackgroundToRed();
                    // TODO: Show error
                } else {
                    if (entryDate.isAfter(currentTrip.getEndDate()) ||
                    entryDate.isBefore(currentTrip.getStartDate())) { // Entered date is outside of the trip duration.
                        txtDate.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.red_border, null));
                        //TODO: Show error

                    } else {

                        createTransaction(entryDate, rdbId);

                        Bundle bundle = new Bundle();
                        bundle.putString("src", "entry");
                        TransactionListFragment fragment = new TransactionListFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameOriginal, fragment);
                        fragmentTransaction.commit();
                    }
                }
            }
        });



        // Currency button OnClickListener
        btnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = "";
                if (!edtTxtAmount.getText().toString().isBlank()) {
                    amount = edtTxtAmount.getText().toString();
                }

                Utils.getInstance(getContext()).putOnResume(amount,
                        txtDate.getText().toString(), edtTxtDescription.getText().toString());

                Intent intent = new Intent(getActivity(), CurrencyActivity.class);
                intent.putExtra("src", "entryFragment");

                startActivity(intent);
            }


        });

        btnCategoryEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryListActivity.class);
                startActivity(intent);
            }
        });

        //TODO: Remove this
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance(getContext()).clearAll();
            }
        });

    }

    public void createTransaction (LocalDate date, int rdbId) {

        int lastId = Utils.getInstance(getContext()).getLastTransactionID();

        String amount = edtTxtAmount.getText().toString();

        if (amount.isEmpty()) {
            amount = "0.00";
        }

        RetrofitInterface retrofitInterface = RetrofitBuilder.getRetrofitInstance().create(RetrofitInterface.class);

        Toast.makeText(getContext(), "home currency: " + currentTrip.getHomeCurrency().getShortName(), Toast.LENGTH_SHORT).show();
        Call<JsonObject> call = retrofitInterface.getExchangeRate(currentTrip.getHomeCurrency().getShortName());

        double finalAmount = Double.parseDouble(amount);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject results = response.body();
                JsonObject rates = results.getAsJsonObject("conversion_rates");
                double multiplier = Double.parseDouble(rates.get(btnCurrency.getText().toString()).toString());

                Transaction newTransaction = new Transaction(finalAmount,
                        round(finalAmount / multiplier, 2),
                        btnCurrency.getText().toString(),
                        categoryFragment.getSelectedCategory(rdbId),
                        edtTxtDescription.getText().toString(),
                        txtCountry.getText().toString(),
                        date,
                        lastId + 1);

                Utils.getInstance(getContext()).setLastTransactionID(lastId + 1);

                if (action.equals("edit")) {

                    Transaction incomingTransaction = Utils.getInstance(getContext()).
                            searchTransactionById(bundle.getInt("transactionId"));

                    Utils.getInstance(view.getContext()).removeTransaction(incomingTransaction);
                    Toast.makeText(view.getContext(), "Transaction edited. ID: " + newTransaction.getId(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Transaction entered. ID: " + newTransaction.getId(), Toast.LENGTH_SHORT).show();
                }
                Utils.getInstance(view.getContext()).addTransaction(newTransaction);
                edtTxtAmount.getText().clear();
                edtTxtDescription.getText().clear();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Retrofit", "Error: " + t.getMessage());
            }
        });

    }


}
