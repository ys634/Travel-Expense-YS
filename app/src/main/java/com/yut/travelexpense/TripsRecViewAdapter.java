package com.yut.travelexpense;

import static com.yut.travelexpense.Utils.formatter;
import static com.yut.travelexpense.Utils.removeZero;
import static com.yut.travelexpense.Utils.round;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TripsRecViewAdapter extends RecyclerView.Adapter<TripsRecViewAdapter.MyViewHolder> {

    private static final String TAG = "TripsRecViewAdapter";
    Context context;
    ArrayList<TripModel> tripModels;
    private static TripsApi tripsApi;

    public TripsRecViewAdapter(Context context, ArrayList<TripModel> tripModels) {
        this.context = context;
        this.tripModels = tripModels;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripsRecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trips_list_item, parent, false);

        return new TripsRecViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsRecViewAdapter.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");

        TripModel tripSelected = tripModels.get(position);

        String startDate = tripSelected.getStartDate().format(formatter);
        String endDate = tripSelected.getEndDate().format(formatter);

        String dateText = startDate + " ~ " + endDate;

        double totalSpent = 0.00;
        ArrayList<Transaction> transactions = tripSelected.getTransactions();
        for (Transaction t: transactions) {
            if (!t.getNoStats()) {
                totalSpent += Utils.getInstance(context).convertToHomeCurrency(t.getOriginalAmount(), t.getCurrency(), 2);
            }
        }


        String spentOverBudget = Utils.getInstance(context).getHomeCurrency().getSymbol() +
                " " +
                removeZero(round(totalSpent, 2)) +
                "/" +
                Utils.getInstance(context).getHomeCurrency().getSymbol() +
                " " +
                removeZero(tripSelected.getBudget());


        holder.txtTripName.setText(tripSelected.getName());
        holder.txtBudget.setText(spentOverBudget);
        holder.txtCountriesVisited.setText("Costa Rica");
        holder.txtDuration.setText(dateText);

        holder.icEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Trip ID: " + tripSelected.getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, AddTripActivity.class);
                intent.putExtra("tripId", tripSelected.getId());
                intent.putExtra("action", "edit");

                context.startActivity(intent);
            }
        });

        holder.icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInterfaceInstance(context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this trip?\n" +
                        "All transactions will be removed.");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.getInstance(context).removeTrip(tripSelected);
                        tripsApi.callBackTrips();
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());

                    }
                });

                builder.setCancelable(true);
                builder.create().show();

            }
        });


        holder.tripParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.getInstance(context).setCurrentTrip(tripSelected);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtTripName, txtDuration, txtBudget, txtCountriesVisited;
        ImageView icDelete, icEdit;
        ConstraintLayout tripParent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTripName = itemView.findViewById(R.id.txtTripName);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtBudget = itemView.findViewById(R.id.txtBudget);
            txtCountriesVisited = itemView.findViewById(R.id.txtCountriesVisited);
            tripParent = itemView.findViewById(R.id.tripParent);
            icDelete = itemView.findViewById(R.id.icDelete);
            icEdit = itemView.findViewById(R.id.icEdit);
            tripParent = itemView.findViewById(R.id.tripParent);
        }
    }

    public static void setInterfaceInstance(Context context) {
        tripsApi = (TripsApi) context;
    }
}
