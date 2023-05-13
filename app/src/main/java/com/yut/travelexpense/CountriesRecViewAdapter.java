package com.yut.travelexpense;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CountriesRecViewAdapter extends RecyclerView.Adapter<CountriesRecViewAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "CountriesRecViewAdapter";

    Context context;
    ArrayList<CountryModel> countryModels;
    ArrayList<CountryModel> countryModelsCopy;


    public CountriesRecViewAdapter(Context context, ArrayList<CountryModel> countryModels) {
        this.context = context;
        this.countryModels = countryModels;
        countryModelsCopy = new ArrayList<>(countryModels);

    }

    @NonNull
    @Override
    public CountriesRecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.countries_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountriesRecViewAdapter.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");

        String countrySelected = countryModels.get(position).getName();
        holder.txtCountry.setText(countrySelected);


        // Listener for when a country is selected
        holder.countryParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.getInstance(context).putCountryPreference(countrySelected);

                Toast.makeText(context, countrySelected + " Selected", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("src", "country");
                //TODO: pass transaction to EntryFragment
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return countryModels.size();
    }

    @Override
    public Filter getFilter() {
        return countriesFilter;
    }

    private Filter countriesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CountryModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(countryModelsCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CountryModel c: countryModelsCopy) {
                    if (c.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(c);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countryModels.clear();
            countryModels.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtCountry;
        ConstraintLayout countryParent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCountry = itemView.findViewById(R.id.txtCurrencyShort);
            countryParent = itemView.findViewById(R.id.countryParent);
        }
    }
}
