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
import androidx.cardview.widget.CardView;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.yut.travelexpense.CurrencyConversion.RetrofitBuilder;
import com.yut.travelexpense.CurrencyConversion.RetrofitInterface;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EntryFragment extends Fragment {
    private static final String TAG = "Entry fragment";
    View view;

    private EditText edtTxtAmount, edtTxtDescription;
    private Button btnEnter, btnUnit, btnCategoryEditor;
    private TextView txtDate, txtCountry;
    private CardView cardCategory;
    Bundle bundle;
    String action;
    CategoryFragment categoryFragment;

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

        initComponents(action);
//        initCategories();

        initClickListeners(action);

        String countryPreference = Utils.getInstance(getContext()).getCountryPreference();
        if (countryPreference != null & !countryPreference.equals("")) {
            txtCountry.setText(countryPreference);
        }

        String unitPreference = Utils.getInstance(getContext()).getUnitPreference();
        if (unitPreference != null & !unitPreference.equals("")) {
            btnUnit.setText(unitPreference);
        }

        return view;
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

                        //TODO: create a callback so that the recview is updated


                        Utils.getInstance(getContext()).removeTransaction(incomingTransaction);
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
        btnEnter = view.findViewById(R.id.btnEdit);
        btnUnit = view.findViewById(R.id.btnUnit);
        btnCategoryEditor = view.findViewById(R.id.btnCategoryEditor);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtDate = view.findViewById(R.id.txtDate);
        cardCategory = view.findViewById(R.id.cardCategory);

//        rrGroupCategories = view.findViewById(R.id.rrGroupCategories);


        //TODO: Remove below this
        btnClear = view.findViewById(R.id.btnClear);

        if (!action.equals("add")) {
            setHasOptionsMenu(true);

            Transaction incomingTransaction = bundle.getParcelable("transaction");

            edtTxtAmount.setText(String.valueOf(incomingTransaction.getOriginalAmount()));
            edtTxtDescription.setText(incomingTransaction.getDescription());
            txtCountry.setText(incomingTransaction.getCountry());
            btnUnit.setText(incomingTransaction.getUnit());
            txtDate.setText(incomingTransaction.getDate().format(formatter));
            if (action.equals("edit")) {
                btnEnter.setText("Edit");
            }
        }
    }

    private void initCategories() {
        ArrayList<CategoryModel> categories = Utils.getInstance(getContext()).getAllCategories();

        ArrayList<Integer> btnIDs = new ArrayList<>();

        for (int i = 0; i < categories.size(); i++) {
            int j = i + 1;
            String btnID = "radioBtn" + j;

            int resID = this.getResources().getIdentifier(btnID, "id", getActivity().getPackageName());

            btnIDs.add(resID);

            RadioButton radioButton = (RadioButton) view.findViewById(btnIDs.get(i));
            radioButton.setText(categories.get(i).getName());
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null,
                    ResourcesCompat.getDrawable(getResources(), categories.get(i).getImageURL(), null),
                    null,
                    null);

        }
    }

    private void initClickListeners (String action) {

        // Country button OnClickListener
        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Making it so that input is not lost after navigating to CountriesActivity
                Intent intent = new Intent(getActivity(), CountriesActivity.class);

//                LocalDate entryDate;
//                String text = txtDate.getText().toString();
//
//                if (text.equals("Today")) {
//                    entryDate = today;
//                } else {
//                    entryDate = LocalDate.parse(text, formatter);
//                }
//
//                Transaction newTransaction = createTransaction(entryDate);
                Transaction newTransaction = new Transaction();

                intent.putExtra("transaction", newTransaction);

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

                LocalDate entryDate;
                String text = txtDate.getText().toString();

                if (text.equals("Today")) {
                    entryDate = today;
                } else {

                    entryDate = LocalDate.parse(text, formatter);
                }


                int rdbId = categoryFragment.getSelectedRbdId();

                Toast.makeText(getContext(), "rdbId: " + rdbId, Toast.LENGTH_SHORT).show();

                if (rdbId == -1) {  // No category is selected.
                    categoryFragment.changeBackgroundToRed();
                    // TODO: Show error
                } else {
                    if (entryDate.isAfter(Utils.getInstance(getContext()).getCurrentTrip().getEndDate()) ||
                    entryDate.isBefore(Utils.getInstance(getContext()).getCurrentTrip().getStartDate())) { // Entered date is outside of the trip duration.
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



        // Unit button OnClickListener
        btnUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UnitsActivity.class);
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


        // TODO: change USD to home currency
        Call<JsonObject> call = retrofitInterface.getExchangeRate("USD");


        double finalAmount = Double.parseDouble(amount);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject results = response.body();
                JsonObject rates = results.getAsJsonObject("conversion_rates");
                double multiplier = Double.parseDouble(rates.get(btnUnit.getText().toString()).toString());

                Transaction newTransaction = new Transaction(finalAmount,
                        round(finalAmount / multiplier, 2),
                        btnUnit.getText().toString(),
                        categoryFragment.getSelectedCategory(rdbId),
                        edtTxtDescription.getText().toString(),
                        txtCountry.getText().toString(),
                        date,
                        lastId + 1);

                Utils.getInstance(getContext()).setLastTransactionID(lastId + 1);

                if (action.equals("edit")) {

                    Transaction incomingTransaction = bundle.getParcelable("transaction");

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

            }
        });



    }
}
