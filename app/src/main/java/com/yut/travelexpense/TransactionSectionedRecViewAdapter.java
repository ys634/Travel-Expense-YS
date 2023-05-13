package com.yut.travelexpense;

import static com.yut.travelexpense.MainActivity.round;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TransactionSectionedRecViewAdapter extends RecyclerView.Adapter<TransactionSectionedRecViewAdapter.MyViewHolder>
implements Filterable {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<Section> sections;
    private List<Section> sectionsCopy;


    Context context;

    public TransactionSectionedRecViewAdapter(Context context, List<Section> sections) {
        this.context = context;
        this.sections = sections;
        sectionsCopy = new ArrayList<>(sections);

    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.dates_list_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.transactions_list_item, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int sectionIndex = getSectionIndexForPosition(position);
        int transactionIndex = getTransactionIndexWithinSection(position);
        List<Transaction> transactions = sections.get(sectionIndex).getTransactions();

        View view = holder.rootView;

        if (isHeader(position)) {

            TextView txtSectionDate = view.findViewById(R.id.txtSectionDate);
            TextView txtSectionTotal = view.findViewById(R.id.txtSectionTotal);

            LocalDate header = sections.get(sectionIndex).getHeader();
            Double total = 0.00;
            for (Transaction t: transactions) {
                total += t.getConvertedAmount();
            }

            DateTimeFormatter sectionFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd");

            txtSectionDate.setText(header.format(sectionFormatter));
            txtSectionTotal.setText(String.valueOf(round(total, 2)));

        } else {

            TextView txtCategory = view.findViewById(R.id.txtCategory);
            TextView txtDescription = view.findViewById(R.id.txtDescription);
            TextView txtConvertedAmount = view.findViewById(R.id.txtConvertedAmount);
            TextView txtOriginalAmount = view.findViewById(R.id.txtOriginalAmount);
            ConstraintLayout transactionParent = view.findViewById(R.id.transactionParent);
            ImageView imgCategory = view.findViewById(R.id.imgCategory);


            Transaction transactionSelected = transactions.get(transactionIndex);

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


            txtCategory.setText(transactionSelected.getCategory());
            txtDescription.setText(transactionSelected.getDescription());
            txtOriginalAmount.setText(amountInOriginalCurrency);
            txtConvertedAmount.setText(amountInHomeCurrency);
            imgCategory.setImageResource(image);

            transactionParent.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Section section: sections) {
            count += section.getTransactions().size() + 1; // add 1 for the header
        }
        return count;
    }

    private boolean isHeader(int position) {
        int count = 0;
        for (Section section : sections) {
            if (position == count) {
                return true;
            }
            count += section.getTransactions().size() + 1; // add 1 for the header
        }
        return false;
    }



    private int getSectionIndexForPosition(int position) {
        int count = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getTransactionCount() > 0) {
                if (position <= count + sections.get(i).getTransactionCount()) {
                    return i;
                }
                count += sections.get(i).getTransactionCount() + 1; // add 1 for the section header
            }
        }
        return -1; // section not found
    }

    private int getTransactionIndexWithinSection(int position) {
        int count = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getTransactionCount() > 0) {
                if (position <= count + sections.get(i).getTransactionCount()) {
                    break;
                }
                count += sections.get(i).getTransactionCount() + 1;
            }
        }
        return position - count - 1;
    }



    @Override
    public Filter getFilter() {
        return sectionsFilter;
    }

    private Filter sectionsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Transaction> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                for (Section s: sectionsCopy) {
                    filteredList.addAll(s.getTransactions());
                }
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Section s : sectionsCopy) {
                    LocalDate date = s.getHeader();
                    List<Transaction> transactions = s.getTransactions();
                    for (Transaction t : transactions) {
                        if (t.getDescription().toLowerCase().contains(filterPattern) ||
                                t.getCategory().toLowerCase().contains(filterPattern)) {
                            filteredList.add(t);
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            sections.clear();
            Map<LocalDate, List<Transaction>> transactionsByDate = new TreeMap<>();
            ArrayList<Transaction> transactions = (ArrayList<Transaction>) results.values;
            for (Transaction t: transactions) {
                LocalDate date = t.getDate();
                if (!transactionsByDate.containsKey(date)) {
                    transactionsByDate.put(date, new ArrayList<>());
                }
                transactionsByDate.get(date).add(t);
            }

            for (LocalDate date: transactionsByDate.keySet()) {
                sections.add(new Section(date, transactionsByDate.get(date)));
            }

            Collections.sort(sections);
            notifyDataSetChanged();
        }
    };


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public View rootView;

        TextView txtCategory, txtDescription, txtConvertedAmount, txtOriginalAmount;
        ConstraintLayout transactionParent;
        ImageView imgCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rootView = itemView;

        }
    }
}
