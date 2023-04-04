package com.yut.travelexpense;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PieChartFragment extends Fragment {

    View view;
    private final TripModel currentTrip = Utils.getInstance(getContext()).getCurrentTrip();
    private final ArrayList<Transaction> transactions = currentTrip.getTransactions();
    PieChart pieChart;
    RecyclerView statsListRecView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        initComponents();
        setUpPieChart();
        ArrayList<NameAmountStatsModel> groups = loadPieChartData();

        NameAmountStatsRecViewAdapter adapter = new NameAmountStatsRecViewAdapter(getActivity(), groups);

        statsListRecView.setAdapter(adapter);
        statsListRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void initComponents() {

        pieChart = view.findViewById(R.id.pieChart);
        statsListRecView = view.findViewById(R.id.statsListRecView);

    }

    public void setUpPieChart() {
            pieChart.setDrawHoleEnabled(true);
            pieChart.setUsePercentValues(true);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setEntryLabelTextSize(12);
        }

        public ArrayList<NameAmountStatsModel> loadPieChartData() {

            ArrayList<NameAmountStatsModel> groups = new ArrayList<>();

            for (Transaction t: transactions) {
                String category = t.getCategory();
                double amount = t.getOriginalAmount();

                boolean groupExists = false;
                for (NameAmountStatsModel s: groups) {
                    if (s.getGroupName().equals(category)) {
                        s.setGroupTotal(s.getGroupTotal() + amount);
                        groupExists = true;
                    }
                }
                if (!groupExists) {
                    groups.add(new NameAmountStatsModel(category, amount));
                }
            }

            Collections.sort(groups, new Comparator<NameAmountStatsModel>() {
                @Override
                public int compare(NameAmountStatsModel o1, NameAmountStatsModel o2) {
                    return (int)Math.ceil(o2.getGroupTotal() - o1.getGroupTotal());
                }
            });
            List<PieEntry> dataEntries = new ArrayList<>();


            for (NameAmountStatsModel s: groups) {
                dataEntries.add(new PieEntry((float) s.getGroupTotal(), s.getGroupName()));
            }


            ArrayList<Integer> colors = new ArrayList<>();
            for (int color: ColorTemplate.MATERIAL_COLORS) {
                colors.add(color);
            }

            for (int color: ColorTemplate.VORDIPLOM_COLORS) {
                colors.add(color);
            }

            PieDataSet dataSet = new PieDataSet(dataEntries, "By Category");
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            dataSet.setDrawValues(true);
            dataSet.setValueFormatter(new PercentFormatter(pieChart));
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.BLACK);

            pieChart.setData(data);

            Legend legend = pieChart.getLegend();
            legend.setEnabled(false);

            pieChart.setNoDataText("No Data");
            pieChart.invalidate();

            return groups;
        }



}