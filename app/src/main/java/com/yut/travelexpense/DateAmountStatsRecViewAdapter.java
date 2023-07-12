package com.yut.travelexpense;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DateAmountStatsRecViewAdapter  extends RecyclerView.Adapter<DateAmountStatsRecViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<DateAmountStatsModel> dateAmountStatsModels;

    public DateAmountStatsRecViewAdapter(Context context, ArrayList<DateAmountStatsModel> dateAmountStatsModels) {
        this.context = context;
        this.dateAmountStatsModels = dateAmountStatsModels;
    }

    @NonNull
    @Override
    public DateAmountStatsRecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.date_amount_stats_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateAmountStatsRecViewAdapter.MyViewHolder holder, int position) {

        DateAmountStatsModel currentStats = dateAmountStatsModels.get(position);
        String dateTotal = Utils.getInstance(context).getHomeCurrency().getSymbol() + " " +
                String.valueOf(currentStats.getAmount());
        holder.txtDate.setText(currentStats.getDate().toString());
        holder.txtDateTotal.setText(dateTotal);

        holder.dateAmountStatsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("src", "bar");
                bundle.putLong("sortBy", currentStats.getDate().toEpochDay());
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                TransactionListFragment fragment = new TransactionListFragment();
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
        return dateAmountStatsModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtDateTotal;
        ConstraintLayout dateAmountStatsParent;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDateTotal = itemView.findViewById(R.id.txtDateTotal);
            dateAmountStatsParent = itemView.findViewById(R.id.dateAmountStatsParent);
        }

    }
}
