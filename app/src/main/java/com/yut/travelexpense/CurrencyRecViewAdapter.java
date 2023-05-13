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

public class CurrencyRecViewAdapter extends RecyclerView.Adapter<CurrencyRecViewAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "CurrencyRecViewAdapter";

    Context context;
    ArrayList<CurrencyModel> currencyModels;
    ArrayList<CurrencyModel> currencyModelsCopy;
    String src;
    String name;
    String startDate;
    String endDate;
    Double budget;

    Double amount;
    String date;
    String description;


    public CurrencyRecViewAdapter(Context context, ArrayList<CurrencyModel> currencyModels,
                                  String src, String name, String startDate, String endDate,
                                  Double budget) {
        this.context = context;
        this.currencyModels = currencyModels;
        currencyModelsCopy = new ArrayList<>(currencyModels);
        this.src = src;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
    }

    public CurrencyRecViewAdapter(Context context, ArrayList<CurrencyModel> currencyModels, String src) {
        this.context = context;
        this.currencyModels = currencyModels;
        currencyModelsCopy = new ArrayList<>(currencyModels);
        this.src = src;
    }



    @NonNull
    @Override
    public CurrencyRecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.currency_list_item, parent, false);

        return new CurrencyRecViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyRecViewAdapter.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        CurrencyModel currencySelected = currencyModels.get(position);
        holder.txtCurrencyShort.setText(currencySelected.getShortName());
        holder.txtCurrencyFull.setText(currencyModels.get(position).getFullName());
        holder.txtCurrencySymbol.setText(currencyModels.get(position).getSymbol());



        holder.currencyParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (src.equals("addTrip")) {
                    Utils.getInstance(context).putHomeCurrency(currencySelected);
                    Intent intent = new Intent(context, AddTripActivity.class);
                    intent.putExtra("action", "continue");
                    intent.putExtra("name", name);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    intent.putExtra("budget", budget);
                    context.startActivity(intent);
                } else if (src.equals("entryFragment")) {

                    //TODO: Make it CurrencyModel not String?
                    Utils.getInstance(context).putCurrencyPreference(currencySelected.getShortName());

                    Toast.makeText(context, currencyModels.get(holder.getAdapterPosition()).getFullName() + " Selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Something wrong in CurrencyRecViewAdapter", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencyModels.size();
    }

    @Override
    public Filter getFilter() {
        return currencyFilter;
    }

    private Filter currencyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CurrencyModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(currencyModelsCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CurrencyModel u: currencyModelsCopy) {
                    if (u.getFullName().toLowerCase().contains(filterPattern) ||
                    u.getShortName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(u);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            currencyModels.clear();
            currencyModels.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtCurrencyShort, txtCurrencyFull, txtCurrencySymbol;
        ConstraintLayout currencyParent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCurrencyShort = itemView.findViewById(R.id.txtCurrencyShort);
            txtCurrencyFull = itemView.findViewById(R.id.txtCurrencyFull);
            txtCurrencySymbol = itemView.findViewById(R.id.txtCurrencySymbol);
            currencyParent = itemView.findViewById(R.id.currencyParent);
        }
    }


}
