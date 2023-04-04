package com.yut.travelexpense;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NameAmountStatsRecViewAdapter extends RecyclerView.Adapter<NameAmountStatsRecViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<NameAmountStatsModel> nameAmountStatsModels;
    public NameAmountStatsRecViewAdapter(Context context, ArrayList<NameAmountStatsModel> nameAmountStatsModels) {
        this.context = context;
        this.nameAmountStatsModels = nameAmountStatsModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.name_amount_stats_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameAmountStatsRecViewAdapter.MyViewHolder holder, int position) {
        NameAmountStatsModel currentStats = nameAmountStatsModels.get(position);

        int image = 0;

        switch (currentStats.getGroupName()) {
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

        holder.txtGroupName.setText(currentStats.getGroupName());
        holder.txtGroupTotal.setText(String.valueOf(currentStats.getGroupTotal()));
        holder.imgGroupIcon.setImageResource(image);

        holder.pieChartListParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("src", "pie");
                bundle.putString("sortBy", currentStats.getGroupName());
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
        return nameAmountStatsModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgGroupIcon;
        TextView txtGroupName, txtGroupTotal;
        ConstraintLayout pieChartListParent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgGroupIcon = itemView.findViewById(R.id.imgGroupIcon);
            txtGroupName = itemView.findViewById(R.id.txtGroupName);
            txtGroupTotal = itemView.findViewById(R.id.txtGroupTotal);
            pieChartListParent = itemView.findViewById(R.id.pieChartListParent);
        }
    }
}

