package com.yut.travelexpense;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BarChartFragment extends Fragment {

    View view;
    private final TripModel currentTrip = Utils.getInstance(getContext()).getCurrentTrip();
    private final ArrayList<Transaction> transactions = currentTrip.getTransactions();
    BarChart barChart;
    RecyclerView dateAmountStatsRecView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        barChart = view.findViewById(R.id.barChart);

        ArrayList<DateAmountStatsModel> models = setUpBarChart();

        dateAmountStatsRecView = view.findViewById(R.id.dateAmountStatsRecView);

        DateAmountStatsRecViewAdapter adapter = new DateAmountStatsRecViewAdapter(getActivity(), models);

        dateAmountStatsRecView.setAdapter(adapter);
        dateAmountStatsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }


    private ArrayList<DateAmountStatsModel> setUpBarChart() {

        ArrayList<DateAmountStatsModel> models = new ArrayList<>();

        List<BarEntry> barEntries = new ArrayList<>();
        for (Transaction t: transactions) {
            LocalDate date = t.getDate();
            double amount = Utils.getInstance(getContext()).convertToHomeCurrency(t.getOriginalAmount(), t.getCurrency(), 2);


            boolean groupExists = false;
            for (DateAmountStatsModel s: models) {
                if (s.getDate().equals(date)) {
                    s.setAmount(s.getAmount() + amount);
                    groupExists = true;
                }
            }
            if (!groupExists) {
                models.add(new DateAmountStatsModel(date, amount));
            }
        }

        Collections.sort(models, new Comparator<DateAmountStatsModel>() {
            @Override
            public int compare(DateAmountStatsModel o1, DateAmountStatsModel o2) {
                return (int) (o1.getDate().toEpochDay() - o2.getDate().toEpochDay());
            }
        });

        for (DateAmountStatsModel s: models) {
            barEntries.add(new BarEntry(s.getDate().toEpochDay(), (float) s.getAmount()));
        }


        List<String> barLabels = new ArrayList<>();
        BarDataSet barDataSet = new BarDataSet(barEntries, "Days");
        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.setNoDataText("No Data");
        barChart.animateY(2000);


        return models;

    }


}