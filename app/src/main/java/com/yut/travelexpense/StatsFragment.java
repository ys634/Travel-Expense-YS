package com.yut.travelexpense;

import static com.yut.travelexpense.MainActivity.round;
import static com.yut.travelexpense.MainActivity.today;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


public class StatsFragment extends Fragment {

    View view;
    TextView txtMoneySpent, txtAvgSpent, txtDailyBudget, txtSurplus;
    private final TripModel currentTrip = Utils.getInstance(getContext()).getCurrentTrip();
    private final LocalDate startDate = currentTrip.getStartDate();
    private final LocalDate endDate = currentTrip.getEndDate();
    private final ArrayList<Transaction> transactions = currentTrip.getTransactions();

    private ViewPager2 viewPager2;
    private VPAdapter adapter;
    private TabLayout tabLayout;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stats, container, false);

        initComponents();

        setUpExpenditureBox();

        FragmentManager fragmentManager = getParentFragmentManager();
        adapter = new VPAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        return view;
    }


    public void initComponents () {
        txtMoneySpent = view.findViewById(R.id.txtMoneySpent);
        txtAvgSpent = view.findViewById(R.id.txtAvgSpent);
        txtDailyBudget = view.findViewById(R.id.txtDailyBudget);
        txtSurplus = view.findViewById(R.id.txtSurplus);


        tabLayout = view.findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Pie Chart"));
        tabLayout.addTab(tabLayout.newTab().setText("Bar Chart"));

        viewPager2 = view.findViewById(R.id.viewPager);
    }

    private void setUpExpenditureBox() {

        double totalSpent = 0, avgSpent, dailyBudget, budget = currentTrip.getBudget(), surplus;

        for (Transaction t : transactions) {
            totalSpent = totalSpent + t.getConvertedAmount();
        }

        double daysPassed, daysLeft, duration;

        duration = ChronoUnit.DAYS.between(startDate, endDate) + 1;


        if (endDate.isBefore(today) || startDate.isAfter(today)) {
            daysPassed = duration;
            daysLeft = 1;
        } else { // Trip is ongoing
            daysPassed = ChronoUnit.DAYS.between(startDate, today) + 1;
            daysLeft = ChronoUnit.DAYS.between(today, endDate) + 1;
        }


        surplus = round(budget / duration * daysPassed - totalSpent, 2);

        avgSpent = round(totalSpent / daysPassed, 2);

        dailyBudget = round((budget - totalSpent) / daysLeft, 2);

        String spentOverBudget = "$" + round(totalSpent, 2) + "/ $" + budget;
        String strAvgSpent = "$" + avgSpent;
        String strDailyBudget = "$" + dailyBudget;
        String strSurplus = "$" + surplus;

        txtMoneySpent.setText(spentOverBudget);
        txtAvgSpent.setText(strAvgSpent);
        txtDailyBudget.setText(strDailyBudget);
        txtSurplus.setText(strSurplus);
    }

}