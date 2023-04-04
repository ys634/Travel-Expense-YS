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

public class UnitsRecViewAdapter extends RecyclerView.Adapter<UnitsRecViewAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "UnitsRecViewAdapter";

    Context context;
    ArrayList<UnitModel> unitModels;
    ArrayList<UnitModel> unitModelsCopy;

    public UnitsRecViewAdapter(Context context, ArrayList<UnitModel> unitModels) {
        this.context = context;
        this.unitModels = unitModels;
        unitModelsCopy = new ArrayList<>(unitModels);
    }


    @NonNull
    @Override
    public UnitsRecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.units_list_item, parent, false);

        return new UnitsRecViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitsRecViewAdapter.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");

        String unitSelected = unitModels.get(position).getShortName();
        holder.txtUnitShort.setText(unitSelected);
        holder.txtUnitFull.setText(unitModels.get(position).getFullName());



        holder.unitsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.getInstance(context).putUnitPreference(unitSelected);

                Toast.makeText(context, unitModels.get(holder.getAdapterPosition()).getFullName() + " Selected", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return unitModels.size();
    }

    @Override
    public Filter getFilter() {
        return unitsFilter;
    }

    private Filter unitsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UnitModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(unitModelsCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (UnitModel u: unitModelsCopy) {
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
            unitModels.clear();
            unitModels.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtUnitShort, txtUnitFull;
        ConstraintLayout unitsParent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUnitShort = itemView.findViewById(R.id.txtUnitShort);
            txtUnitFull = itemView.findViewById(R.id.txtUnitFull);
            unitsParent = itemView.findViewById(R.id.unitsParent);
        }
    }
}
