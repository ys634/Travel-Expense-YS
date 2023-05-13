package com.yut.travelexpense;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;


public class Utils {

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
        editor.putString(ALL_CATEGORIES_KEY, gson.toJson(categories));
        editor.putString(COUNTRY_PREF, "United States");
        editor.putString(CURRENCY_PREF, "USD");
        editor.putString(HOME_CURRENCY, gson.toJson(homeCurrency));
        editor.putInt(LAST_TRANSACTION_ID, 0);
        editor.putInt(LAST_TRIP_ID, 0);
        editor.putString(ALL_TRIPS_KEY, gson.toJson(trips));
        editor.putInt(NO_OF_CATEGORIES, 8);
        editor.putString(ON_RESUME_AMOUNT, "");
        editor.putString(ON_RESUME_DATE, "Today");
        editor.putString(ON_RESUME_DESCRIPTION, "");
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

        initData();
    }

    //_________________________________________________________________________________________
    //CURRENCY & COUNTRY PREFERENCES

    public void putCountryPreference(String country) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COUNTRY_PREF, country);
        editor.commit();
    }

    public String getCountryPreference() {
        return sharedPreferences.getString(COUNTRY_PREF, null);
    }

    public void putCurrencyPreference(String currency) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENCY_PREF, currency);
        editor.commit();
    }

    public String getCurrencyPreference() {
        return sharedPreferences.getString(CURRENCY_PREF, null);
    }


    public void putHomeCurrency(CurrencyModel currency) {
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

    public CurrencyModel getHomeCurrencyOfCurrentTrip() {
        return getCurrentTrip().getHomeCurrency();
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
//        if (amount.isEmpty()) {
//           return "";
//        } else {
//            return amount;
//        }
//
    }

    public String getOnResumeDate() {
        return sharedPreferences.getString(ON_RESUME_DATE, "Today");
    }

    public String getOnResumeDescription() {
        return sharedPreferences.getString(ON_RESUME_DESCRIPTION, "");
    }

    public void removeOnResumeData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ON_RESUME_AMOUNT, "");
        editor.putString(ON_RESUME_DATE, "Today");
        editor.putString(ON_RESUME_DESCRIPTION, "");
        editor.commit();
    }

}
