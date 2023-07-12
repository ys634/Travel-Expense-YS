package com.yut.travelexpense;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yut.travelexpense.CurrencyConversion.RetrofitBuilder;
import com.yut.travelexpense.CurrencyConversion.RetrofitInterface;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Utils {

    public static final LocalDate today = LocalDate.now();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static Utils instance;
    private final SharedPreferences sharedPreferences;

    public static final String SHARED_PREFS = "sharedPreference";
    public static final String COUNTRY_PREF = "countryPref";
    public static final String CURRENCY_PREF = "currencyPref";
    public static final String HOME_CURRENCY = "homeCurrency";
    public static final String TEMP_TRANSACTION = "temporaryTransaction";
    public static final String LAST_TRANSACTION_ID = "lastTransactionId";
    public static final String LAST_TRIP_ID = "lastTripId";
    public static final String ALL_CATEGORIES_KEY = "allCategories";
    public static final String ALL_TRIPS_KEY = "allTrips";
    public static final String NO_OF_CATEGORIES = "noOfCategories";
    public static final String ON_RESUME_AMOUNT = "onResumeAmount";
    public static final String ON_RESUME_DATE = "onResumeDate";
    public static final String ON_RESUME_DESCRIPTION = "onResumeDescription";
    public static final String IS_EDITING = "isEditing";
    public static final String LAST_CONVERSION_DATE = "lastConversionDate";
    public static final String CONVERSION_RATES = "conversionRates";
    public static final String IS_SPREAD = "isSpread";


    private Utils(Context context) {

        sharedPreferences = context.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);

        if (getAllTrips() == null) {
            initData();
        }

    }


    public static Utils getInstance(Context context) {
        if (instance == null) {
            instance = new Utils(context);
        }
        return instance;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public static String removeZero(double value) {
        DecimalFormat noZeroFormat = new DecimalFormat();
        noZeroFormat.setDecimalSeparatorAlwaysShown(false);
        return noZeroFormat.format(value);
    }

    public static String convertNumberToMonth(int month) {
        switch (month) {
            default:
                return null;
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
        }
    }


    // Get all the transactions from the current trip.
    public ArrayList<Transaction> getAllTransactions() {
        TripModel trip = getCurrentTrip();
        if (trip == null) {
            return null;
        } else {
            return trip.getTransactions();
        }
    }

    private void initData() {

        ArrayList<TripModel> trips = new ArrayList<>();
        CurrencyModel homeCurrency = new CurrencyModel("USD", "United States Dollar", "$");
        ArrayList<CategoryModel> categories = new ArrayList<>();
        addBasicCategories(categories);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
                create();
        Gson gson2 = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                create();
        editor.putString(ALL_CATEGORIES_KEY, gson.toJson(categories));
        editor.putString(COUNTRY_PREF, "United States");
        editor.putString(CURRENCY_PREF, "USD");
        editor.putString(HOME_CURRENCY, gson.toJson(homeCurrency));
        editor.putInt(LAST_TRANSACTION_ID, 0);
        editor.putInt(LAST_TRIP_ID, 0);
        editor.putString(ALL_TRIPS_KEY, gson.toJson(trips));
        editor.putInt(NO_OF_CATEGORIES, 8);
        editor.putString(ON_RESUME_AMOUNT, "");
        editor.putString(ON_RESUME_DATE, LocalDate.now().format(formatter));
        editor.putString(ON_RESUME_DESCRIPTION, "");
        editor.putBoolean(IS_EDITING, false);
        editor.putString(LAST_CONVERSION_DATE, gson2.toJson(LocalDateTime.now().minusDays(1)));
        editor.putString(CONVERSION_RATES, gson.toJson(null));
        editor.putBoolean(IS_SPREAD, false);
        editor.commit();

    }




    //___________________________________________________________________________________________
    // Trips


    public ArrayList<TripModel> getAllTrips() {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
                create();
        Type type = new TypeToken<ArrayList<TripModel>>(){}.getType();
        ArrayList<TripModel> trips = gson.fromJson(sharedPreferences.getString(ALL_TRIPS_KEY, null), type);
        return trips;
    }

    public void addTrip(TripModel tripModel) {
        ArrayList<TripModel> trips = getAllTrips();
        trips.add(tripModel);

        Gson gson = new GsonBuilder().
                registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
                create();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ALL_TRIPS_KEY, gson.toJson(trips));
        editor.commit();
    }

    public void removeTrip(TripModel trip) {
        ArrayList<TripModel> trips = getAllTrips();

        if (trips != null) {
            Iterator<TripModel> iterator = trips.iterator();

            while (iterator.hasNext()) {
                TripModel t = iterator.next();
                if (t.getId() == trip.getId()) {
                    iterator.remove();
                    Gson gson = new GsonBuilder().
                            registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
                            create();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(ALL_TRIPS_KEY);
                    editor.putString(ALL_TRIPS_KEY, gson.toJson(trips));
                    editor.commit();
                    break;
                }
            }
        }
    }

    public void setCurrentTrip(TripModel tripModel) {
        ArrayList<TripModel> trips = getAllTrips();
        for (TripModel trip: trips) {
            trip.setIsCurrentTrip(trip.getId() == tripModel.getId());
        }

        Gson gson = new GsonBuilder().
                registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
                create();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ALL_TRIPS_KEY, gson.toJson(trips));
        editor.commit();
    }

    public TripModel getCurrentTrip() {
        ArrayList<TripModel> trips = getAllTrips();
        if (trips == null) {
            return null;
        }

        TripModel currentTrip = new TripModel();

        for (TripModel trip: trips) {
            if (trip.getIsCurrentTrip()) {
                currentTrip = trip;
            }
        }

        return currentTrip;
    }

    public TripModel searchTripById(int id) {
        ArrayList<TripModel> allTrips = getAllTrips();
        TripModel trip = null;
        if (allTrips != null) {
            for (TripModel t: allTrips) {
                if (t.getId() == id) {
                    trip = t;
                    break;
                }
            }
        }
        return trip;
    }



    //_________________________________________________________________________________________
    //TRANSACTION

    public void addTransaction(Transaction transaction) {

        TripModel trip = getCurrentTrip();

        removeTrip(trip);

        // Get all the transactions from the current trip, add the transaction, then replace with the old list.
        ArrayList<Transaction> transactions = trip.getTransactions();
        transactions.add(transaction);
        trip.setTransactions(transactions);

        addTrip(trip);

    }

    public void removeTransaction(Transaction transaction) {

        TripModel trip = getCurrentTrip();

        removeTrip(trip);

        ArrayList<Transaction> transactions = trip.getTransactions();

        if (transactions != null) {
            Iterator<Transaction> iterator = transactions.iterator();

            while (iterator.hasNext()) {
                Transaction t = iterator.next();
                if (t.getId() == transaction.getId()) {
                    iterator.remove();
                    break;
                }
            }

            trip.setTransactions(transactions);
        }

        addTrip(trip);
    }

    public Transaction searchTransactionById(int id) {
        ArrayList<Transaction> transactions = getAllTransactions();

        Transaction result = null;

        for (Transaction t: transactions) {
            if (t.getId() == id) {
                result = t;
                break;
            }
        }

        return result;
    }

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(COUNTRY_PREF);
        editor.remove(CURRENCY_PREF);
        editor.remove(HOME_CURRENCY);
        editor.remove(TEMP_TRANSACTION);
        editor.remove(LAST_TRANSACTION_ID);
        editor.remove(LAST_TRIP_ID);
        editor.remove(ALL_CATEGORIES_KEY);
        editor.remove(ALL_TRIPS_KEY);
        editor.remove(NO_OF_CATEGORIES);
        editor.remove(ON_RESUME_AMOUNT);
        editor.remove(ON_RESUME_DATE);
        editor.remove(ON_RESUME_DESCRIPTION);
        editor.remove(IS_EDITING);
        editor.remove(LAST_CONVERSION_DATE);
        editor.remove(CONVERSION_RATES);
        editor.remove(IS_SPREAD);

        initData();
    }

    //_________________________________________________________________________________________
    //CURRENCY & COUNTRY PREFERENCES

    public void setCountryPreference(String newCountryCode) {

        String countrySb = getCountryPreference();
        ArrayList<String> preferredCountries = new ArrayList<>(Arrays.asList(countrySb.split(",")));

        boolean isRepeat = false;

        for (String countryCode: preferredCountries) {
            if (newCountryCode.equals(countryCode)) {
                isRepeat = true;
                break;
            }
        }

        if (!isRepeat) {
            preferredCountries.add(0, newCountryCode);

            if (preferredCountries.size() > 4) {
                preferredCountries.remove(preferredCountries.size() - 1);
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < preferredCountries.size(); i++) {
                sb.append(preferredCountries.get(i)).append(",");
            }


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(COUNTRY_PREF, sb.toString());

            editor.commit();
        }
    }

    public String getCountryPreference() {
        return sharedPreferences.getString(COUNTRY_PREF, "");
    }

    public void putCurrencyPreference(String currency) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENCY_PREF, currency);
        editor.commit();
    }

    public String getCurrencyPreference() {
        return sharedPreferences.getString(CURRENCY_PREF, null);
    }


    public void setHomeCurrency(CurrencyModel currency) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(HOME_CURRENCY, gson.toJson(currency));
        editor.commit();
    }

    public CurrencyModel getHomeCurrency() {
        Gson gson = new Gson();
        Type type = new TypeToken<CurrencyModel>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(HOME_CURRENCY, null), type);
    }



    //_________________________________________________________________________________________
    // TEMP TRANSACTION
    // TODO: better way to communicate data?

    public void putTransaction(Transaction transaction) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(TEMP_TRANSACTION, gson.toJson(transaction));
        editor.commit();
    }

    public Transaction getTransaction() {
        Gson gson = new Gson();
        Type type = new TypeToken<Transaction>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(TEMP_TRANSACTION, null), type);
    }



    //__________________________________________________________________________________________
    //ID

    public void setLastTransactionID(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_TRANSACTION_ID, id);
        editor.commit();
    }

    public int getLastTransactionID() {
        return sharedPreferences.getInt(LAST_TRANSACTION_ID, -1);
    }

    public void setLastTripID(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_TRIP_ID, id);
        editor.commit();
    }

    public int getLastTripID() {
        return sharedPreferences.getInt(LAST_TRIP_ID, -1);
    }




    //__________________________________________________________________________________________
    // CATEGORIES

    public ArrayList<CategoryModel> getAllCategories() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CategoryModel>>(){}.getType();

        return gson.fromJson(sharedPreferences.getString(ALL_CATEGORIES_KEY, null), type);
    }

    public void addCategory(CategoryModel category) {
        ArrayList<CategoryModel> categories = getAllCategories();
        categories.add(category);

        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ALL_CATEGORIES_KEY, gson.toJson(categories));
        editor.putInt(NO_OF_CATEGORIES, getNoOfCategories() + 1);
        editor.commit();
    }

    public void addBasicCategories(ArrayList<CategoryModel> categories) {
        categories.add(new CategoryModel("Accommodation", R.drawable.ic_accommodation));
        categories.add(new CategoryModel("Food", R.drawable.ic_food));
        categories.add(new CategoryModel("Transportation", R.drawable.ic_transportation));
        categories.add(new CategoryModel("Activities", R.drawable.ic_activities));
        categories.add(new CategoryModel("Shopping", R.drawable.ic_shopping));
        categories.add(new CategoryModel("Entertainment", R.drawable.ic_entertainment));
        categories.add(new CategoryModel("Fees", R.drawable.ic_fees));
        categories.add(new CategoryModel("Other", R.drawable.ic_others));

    }

    public int getNoOfCategories() {
        return sharedPreferences.getInt(NO_OF_CATEGORIES, -1);
    }

    public void updateCategories() {
        ArrayList<CategoryModel> categories = new ArrayList<>();

        addBasicCategories(categories);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString(ALL_CATEGORIES_KEY, gson.toJson(categories));
        editor.commit();

    }


    //_____________________________________________________________________________________
    // ON RESUME DATA STORAGE

    public void putOnResume(String amount, String date, String description) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ON_RESUME_AMOUNT, amount);
        editor.putString(ON_RESUME_DATE, date);
        editor.putString(ON_RESUME_DESCRIPTION, description);
        editor.commit();
    }

    public String getOnResumeAmount() {
        return sharedPreferences.getString(ON_RESUME_AMOUNT, "");

    }

    public String getOnResumeDate() {
        return sharedPreferences.getString(ON_RESUME_DATE, LocalDate.now().format(formatter));
    }

    public String getOnResumeDescription() {
        return sharedPreferences.getString(ON_RESUME_DESCRIPTION, "");
    }

    public void removeOnResumeData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ON_RESUME_AMOUNT, "");
        editor.putString(ON_RESUME_DATE, LocalDate.now().format(formatter));
        editor.putString(ON_RESUME_DESCRIPTION, "");
        editor.putBoolean(IS_EDITING, false);
        editor.commit();
    }


    public void putIsEditing(boolean isEditing) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_EDITING, isEditing);
        editor.commit();
    }

    public boolean getIsEditing() {
        return sharedPreferences.getBoolean(IS_EDITING, false);
    }

    public void putIsSpread(boolean isSpread) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SPREAD, isSpread);
        editor.commit();
    }

    public boolean getIsSpread() {
        return sharedPreferences.getBoolean(IS_SPREAD, false);
    }





    //____________________________________________________________________________________________
    // CONVERSIONS

    public void fetchConversionRates() {
//        Log.d(TAG, "fetchConversionRates: Started");
        RetrofitInterface retrofitInterface = RetrofitBuilder.getRetrofitInstance().create(RetrofitInterface.class);

        Call<JsonObject> call = retrofitInterface.getExchangeRate(getHomeCurrency().getShortName());

//        try {
//            Response<JsonObject> response = call.execute();
//            if(response.isSuccessful()) {
//                JsonObject results = response.body();
//                JsonObject rates = results.getAsJsonObject("conversion_rates");
//                putConversionRates(rates);
//                putLastConversionDate(LocalDateTime.now());
//            } else {
//
//            }
//        } catch (IOException e) {
//            Log.e("Retrofit", "Error: " + e.getMessage());
//
//        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject results = response.body();
                JsonObject rates = results.getAsJsonObject("conversion_rates");
                putConversionRates(rates);
                putLastConversionDate(LocalDateTime.now());
//                Log.d(TAG, "fetchConversionRates: Completed");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Retrofit", "Error: " + t.getMessage());
            }
        });

    }

    public void putLastConversionDate(LocalDateTime now) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                create();

        editor.putString(LAST_CONVERSION_DATE, gson.toJson(now));
        editor.commit();

    }

    public LocalDateTime getLastConversionDate() {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                create();
        Type type = new TypeToken<LocalDateTime>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(LAST_CONVERSION_DATE, null), type);
    }

    public void putConversionRates(JsonObject object) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString(CONVERSION_RATES, gson.toJson(object));
        editor.commit();
    }

    public JsonObject getConversionRates() {
        Gson gson = new Gson();
        Type type = new TypeToken<JsonObject>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(CONVERSION_RATES, null), type);
    }


    public double convertToHomeCurrency(double amount, String originalCurrency, int places) {
        double multiplier = getMultiplier(originalCurrency);
        return round(amount / multiplier, places);
    }

    public double getMultiplier(String currency) {
        JsonObject rates = getConversionRates();
        return Double.parseDouble(rates.get(currency).toString());
    }






}
