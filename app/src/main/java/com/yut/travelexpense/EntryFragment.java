package com.yut.travelexpense;

import static com.yut.travelexpense.Utils.formatter;
import static com.yut.travelexpense.Utils.round;
import static com.yut.travelexpense.Utils.today;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class EntryFragment extends Fragment {
    private static final String TAG = "Entry fragment";
    View view;

    private EditText edtTxtAmount, edtTxtDescription;
    private Button btnEnter, btnCurrency, btnCategoryEditor;
    private ImageButton btnSwap, btnRefresh, btnBackToSingleDate;
    private TextView txtDate, txtConversionRate, txtUpdateInfo, txtSpread;
    Bundle bundle;
    String action;
    CategoryFragment categoryFragment;
    TripModel currentTrip;
    boolean isCurrentlyEditing = false;
    boolean isSpread = false;
    CountryCodePicker ccp;
    CheckBox cbNoStats;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_entry, container, false);

        Utils.getInstance(getContext()).setHomeCurrency(new CurrencyModel("USD", "US Dollars", "$"));
        categoryFragment = new CategoryFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameCatInMain, categoryFragment);
        fragmentTransaction.commit();


        setHasOptionsMenu(true);

        bundle = this.getArguments();
        if (bundle != null) {
            action = bundle.getString("action");

            isCurrentlyEditing = action.equals("edit");


            // action will either be:
            // "add"
            // "edit"
            // "continue"
        }
        Toast.makeText(getContext(), "action: " + action, Toast.LENGTH_SHORT).show();


        currentTrip = Utils.getInstance(getContext()).getCurrentTrip();

        initComponents(action);

//        txtDate.setText(LocalDate.now().format(formatter));

        if (Utils.getInstance(getContext()).getLastConversionDate().toLocalDate().toEpochDay() !=
                (today.toEpochDay())) {
            Utils.getInstance(getContext()).fetchConversionRates();
        }

        initClickListeners();



        //TODO: set preferences by each trip

        String currencyPreference = Utils.getInstance(getContext()).getCurrencyPreference();
        if (!currencyPreference.isEmpty()) {
            btnCurrency.setText(currencyPreference);
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!action.equals("edit")) {
            edtTxtAmount.setText(Utils.getInstance(getContext()).getOnResumeAmount());
            txtDate.setText(Utils.getInstance(getContext()).getOnResumeDate());
            isCurrentlyEditing = Utils.getInstance(getContext()).getIsEditing();
            isSpread = Utils.getInstance(getContext()).getIsSpread();
            edtTxtDescription.setText(Utils.getInstance(getContext()).getOnResumeDescription());

        }

        if (isSpread) {
            txtSpread.setVisibility(View.GONE);
            btnBackToSingleDate.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        if (isCurrentlyEditing) {
            menu.findItem(R.id.menuItemDelete).setVisible(true).setEnabled(true);
        } else {
            menu.findItem(R.id.menuItemDelete).setVisible(false).setEnabled(true);
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
                        Transaction incomingTransaction = Utils.getInstance(getContext()).
                                searchTransactionById(bundle.getInt("transactionId"));

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

                return true;
//            case R.id.changeTheme:
//
//
//                switch (item.getItemId()){
//                    case R.id.darkTheme:
//
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    case R.id.lightTheme:
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    case R.id.sameTheme:
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//                }
//                return true;
//
//            case R.id.changeHomeCurrency:
//                Toast.makeText(getContext(), "Working on it as well", Toast.LENGTH_SHORT).show();
//
//                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    // Initialize components
    private void initComponents(String action) {
        Log.d(TAG, "initComponents: Started");

        edtTxtAmount = view.findViewById(R.id.edtTxtAmount);
        edtTxtDescription = view.findViewById(R.id.edtTxtDescription);
        btnEnter = view.findViewById(R.id.btnEnter);
        btnCurrency = view.findViewById(R.id.btnCurrency);
        btnCurrency.setText(Utils.getInstance(getContext()).getCurrencyPreference());

        btnCategoryEditor = view.findViewById(R.id.btnCategoryEditor);
        btnSwap = view.findViewById(R.id.btnSwap);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        txtDate = view.findViewById(R.id.txtDate);
        txtConversionRate = view.findViewById(R.id.txtConversionRate);
        txtUpdateInfo = view.findViewById(R.id.txtUpdateInfo);
        cbNoStats = view.findViewById(R.id.cbNoStats);
        txtSpread = view.findViewById(R.id.txtSpread);
        btnBackToSingleDate = view.findViewById(R.id.btnBackToSingleDate);

        String lastUpdate = "Last Updated: " +
                Utils.getInstance(getContext()).
                        getLastConversionDate().
                        format(DateTimeFormatter.ofPattern("MMMM d, HH:mm"));
        txtUpdateInfo.setText(lastUpdate);

        ccp = view.findViewById(R.id.ccp);
        ccp.setCountryPreference(Utils.getInstance(getContext()).getCountryPreference());

        txtSpread.setVisibility(View.VISIBLE);
        btnBackToSingleDate.setVisibility(View.GONE);

        if (isCurrentlyEditing || action.equals("continue")) {
            setHasOptionsMenu(true);

            if (isCurrentlyEditing) {
                Transaction incomingTransaction = Utils.getInstance(getContext()).
                        searchTransactionById(bundle.getInt("transactionId"));

                edtTxtAmount.setText(String.valueOf(incomingTransaction.getOriginalAmount()));
                edtTxtDescription.setText(incomingTransaction.getDescription());
                ccp.setCountryForNameCode(incomingTransaction.getCountry());

                btnCurrency.setText(incomingTransaction.getCurrency());
                if (incomingTransaction.getDate() != null) {
                    if (incomingTransaction.getSpread() == 1) {
                        txtDate.setText(incomingTransaction.getDate().format(formatter));
                    } else {
                        String rangeOfDate;
                        rangeOfDate = incomingTransaction.getDate().format(formatter) + " ~ " +
                                incomingTransaction.getDate().plusDays(incomingTransaction.getSpread() - 1).format(formatter);
                        txtDate.setText(rangeOfDate);
                    }
                }
                cbNoStats.setChecked(incomingTransaction.getNoStats());

                if (incomingTransaction.getSpread() > 1) {
                    txtSpread.setVisibility(View.GONE);
                    btnBackToSingleDate.setVisibility(View.VISIBLE);
                    isSpread = true;
                }

                btnEnter.setText("Edit");

            }
        }

    }


    private void initClickListeners () {

        txtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                txtDate.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.white_border, null));

                if (!isSpread) {

                    LocalDate date = LocalDate.parse(txtDate.getText().toString(), formatter);

                    MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select Date").setSelection(LocalDateTime.of(date, LocalTime.NOON).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())
                            .build();

                    datePicker.show(getParentFragmentManager(), "Material_date_picker");

                    datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<>() {
                        @Override
                        public void onPositiveButtonClick(Long selection) {
                            TimeZone timeZoneUTC = TimeZone.getDefault();
                            int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                            Date date = new Date(selection + offsetFromUTC);

                            txtDate.setText(simpleFormat.format(date));
                        }
                    });
                } else {

                    LocalDate startDate = LocalDate.parse(Arrays.asList(txtDate.getText().toString().split(" ~ ")).get(0), formatter);
                    LocalDate endDate = LocalDate.parse(Arrays.asList(txtDate.getText().toString().split(" ~ ")).get(1), formatter);

                    MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                            .setSelection(Pair.create(LocalDateTime.of(startDate, LocalTime.NOON).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    LocalDateTime.of(endDate, LocalTime.NOON).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()))
                            .build();

                    datePicker.show(getParentFragmentManager(), "Material_date_picker");

                    datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                        @Override
                        public void onPositiveButtonClick(Pair<Long, Long> selection) {
                            TimeZone timeZoneUTC = TimeZone.getDefault();
                            int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                            Date startDate = new Date(selection.first + offsetFromUTC);
                            Date endDate = new Date(selection.second + offsetFromUTC);

                            String dateRange = simpleFormat.format(startDate) + " ~ " + simpleFormat.format(endDate);
                            txtDate.setText(dateRange);

                            btnBackToSingleDate.setVisibility(View.VISIBLE);
                            txtSpread.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        // Enter button OnClickListener
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.getInstance(getContext()).removeOnResumeData();

                LocalDate entryDate = LocalDate.parse(Arrays.asList(txtDate.getText().toString().split(" ~ ")).get(0), formatter);

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
                Utils.getInstance(getContext()).putIsEditing(isCurrentlyEditing);
                Utils.getInstance(getContext()).putIsSpread(isSpread);
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

        txtConversionRate.setOnClickListener(new View.OnClickListener() {
            boolean infoIsVisible = false;
            @Override
            public void onClick(View v) {
                if (infoIsVisible) {
                    txtUpdateInfo.setVisibility(View.GONE);
                    infoIsVisible = false;
                } else {
                    txtUpdateInfo.setVisibility(View.VISIBLE);
                    infoIsVisible = true;
                }
            }
        });

        btnSwap.setOnClickListener(new View.OnClickListener() {
            boolean isHomeCurrencyLeft = true;

            @Override
            public void onClick(View v) {


                String symbolLeft;
                String symbolRight;
                double value = Utils.getInstance(getContext()).
                        convertToHomeCurrency(1, btnCurrency.getText().toString(), 5);

                if (isHomeCurrencyLeft) {
                    symbolLeft = btnCurrency.getText().toString();
                    symbolRight = Utils.getInstance(getContext()).getHomeCurrency().getSymbol();

                    isHomeCurrencyLeft = false;
                } else {
                    symbolLeft = Utils.getInstance(getContext()).getHomeCurrency().getSymbol();
                    symbolRight = btnCurrency.getText().toString();
                    isHomeCurrencyLeft = true;
                    value = round(1 / value, 5);

                }

                txtConversionRate.setText(symbolLeft + "1 ≈ " + symbolRight + value);

            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance(getContext()).fetchConversionRates();

                String conversionText = Utils.getInstance(getContext()).getHomeCurrency().getSymbol() + "1 ≈ " +
                        Utils.getInstance(getContext()).convertToHomeCurrency(1, btnCurrency.getText().toString(), 5);

                txtConversionRate.setText(conversionText);

            }
        });

        txtSpread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSpread = true;
                MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(Pair.create(LocalDateTime.of(LocalDate.parse(txtDate.getText().toString(), formatter), LocalTime.NOON).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                        LocalDateTime.of(LocalDate.parse(txtDate.getText().toString(), formatter), LocalTime.NOON).plusDays(1).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()))
                        .build();
                datePicker.show(getParentFragmentManager(), "Material_date_picker");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        TimeZone timeZoneUTC = TimeZone.getDefault();
                        int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                        Date startDate = new Date(selection.first + offsetFromUTC);
                        Date endDate = new Date(selection.second + offsetFromUTC);

                        String dateRange = simpleFormat.format(startDate) + " ~ " + simpleFormat.format(endDate);
                        txtDate.setText(dateRange);

                        btnBackToSingleDate.setVisibility(View.VISIBLE);
                        txtSpread.setVisibility(View.GONE);
                    }
                });

            }
        });

        btnBackToSingleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpread = false;

                btnBackToSingleDate.setVisibility(View.GONE);
                txtSpread.setVisibility(View.VISIBLE);
                String startDate = Arrays.asList(txtDate.getText().toString().split(" ~ ")).get(0);

                txtDate.setText(startDate);
            }
        });

    }

    public void createTransaction (LocalDate date, int rdbId) {

        int lastId = Utils.getInstance(getContext()).getLastTransactionID();

        String amount = edtTxtAmount.getText().toString();

        if (amount.isEmpty()) {
            amount = "0.00";
        }

        long spread = 1;
        if (isSpread) {
            LocalDate startDate = LocalDate.parse(Arrays.asList(txtDate.getText().toString().split(" ~ ")).get(0), formatter);
            LocalDate endDate = LocalDate.parse(Arrays.asList(txtDate.getText().toString().split(" ~ ")).get(1), formatter);
            spread = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }

        Transaction newTransaction = new Transaction(Double.parseDouble(amount),
                btnCurrency.getText().toString(),
                categoryFragment.getSelectedCategory(rdbId),
                edtTxtDescription.getText().toString(),
                ccp.getSelectedCountryNameCode(),
                date,
                lastId + 1,
                cbNoStats.isChecked(),
                spread);

        Utils.getInstance(getContext()).setCountryPreference(ccp.getSelectedCountryNameCode());

        Toast.makeText(getContext(), "Country:" + newTransaction.getCountry(), Toast.LENGTH_SHORT).show();

        Utils.getInstance(getContext()).setLastTransactionID(lastId + 1);


        if (isCurrentlyEditing) {

            Transaction incomingTransaction = Utils.getInstance(getContext()).
                    searchTransactionById(bundle.getInt("transactionId"));

            Utils.getInstance(view.getContext()).removeTransaction(incomingTransaction);
            Toast.makeText(view.getContext(), "Transaction edited. ID: " + newTransaction.getId(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(view.getContext(), "Transaction entered. ID: " + newTransaction.getId(), Toast.LENGTH_SHORT).show();
        }
        Utils.getInstance(view.getContext()).addTransaction(newTransaction);
        cbNoStats.setChecked(false);
        Utils.getInstance(getContext()).putIsSpread(false);
        edtTxtAmount.getText().clear();
        edtTxtDescription.getText().clear();


    }


}
