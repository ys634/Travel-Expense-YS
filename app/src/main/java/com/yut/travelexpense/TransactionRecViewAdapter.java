package com.yut.travelexpense;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TransactionRecViewAdapter extends RecyclerView.Adapter<TransactionRecViewAdapter.MyViewHolder> implements Filterable {

    Context context;
    private ArrayList<Transaction> transactions;
    private ArrayList<Transaction> transactionsCopy;


    public TransactionRecViewAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        transactionsCopy = new ArrayList<>(transactions);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.transactions_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Transaction transactionSelected = transactions.get(position);

        int image = 0;

        switch (transactionSelected.getCategory()) {
            case "Food":
                image = R.drawable.ic_food;
                break;
            case "Transportation":
                image = R.drawable.ic_transportation;
                break;
            case "Accommodation":
                image = R.drawable.ic_accommodation;
                break;
            case "Activities":
                image = R.drawable.ic_activities;
                break;
            case "Entertainment":
                image = R.drawable.ic_entertainment;
                break;
            case "Shopping":
                image = R.drawable.ic_shopping;
                break;
            case "Fees":
                image = R.drawable.ic_fees;
                break;
            case "Other":
                image = R.drawable.ic_others;
                break;
            default:
                break;
        }

        String amountInOriginalCurrency = transactionSelected.getCurrency() + String.valueOf(transactionSelected.getOriginalAmount());
        String amountInHomeCurrency = Utils.getInstance(context).getHomeCurrencyOfCurrentTrip().getSymbol() + " " +
                transactionSelected.getConvertedAmount();


        holder.txtCategory.setText(transactionSelected.getCategory());
        holder.txtDescription.setText(transactionSelected.getDescription());
        holder.txtOriginalAmount.setText(amountInOriginalCurrency);
        holder.txtConvertedAmount.setText(amountInHomeCurrency);
        holder.imgCategory.setImageResource(image);

        holder.transactionParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "ID: " + transactionSelected.getId(), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("action", "edit");
                bundle.putInt("transactionId", transactionSelected.getId());
//                bundle.putParcelable("transaction", transactionSelected);
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                EntryFragment fragment = new EntryFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().
                        replace(R.id.frameOriginal, fragment).
                        addToBackStack(null).
                        commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public Filter getFilter() {
        return transactionsFilter;
    }

    private Filter transactionsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Transaction> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(transactionsCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Transaction t: transactionsCopy) {
                    if (t.getDescription().toLowerCase().contains(filterPattern) ||
                    t.getCategory().toLowerCase().contains(filterPattern)) {
                        filteredList.add(t);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            transactions.clear();
            transactions.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtCategory, txtDescription, txtConvertedAmount, txtOriginalAmount;
        ConstraintLayout transactionParent;
        ImageView imgCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtConvertedAmount = itemView.findViewById(R.id.txtConvertedAmount);
            txtOriginalAmount = itemView.findViewById(R.id.txtOriginalAmount);
            transactionParent = itemView.findViewById(R.id.transactionParent);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }


}

