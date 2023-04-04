package com.yut.travelexpense;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;


public class TransactionListFragment extends Fragment {

    View view;

    RecyclerView transactionListRecView;
    FloatingActionButton floatingBtn;
    TextView txtNoTransaction;
    TransactionRecViewAdapter adapter;
    SearchView searchView;
    MenuItem searchItem;

    Bundle bundle;
    String src;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        bundle = this.getArguments();
        if (bundle != null) {
            src = bundle.getString("src");

            // src will either be:
            // "pie"
            // "bar"
            // "mainActivity"
            // "entry"
        }
        Toast.makeText(getContext(), "src: " + src, Toast.LENGTH_SHORT).show();

        setHasOptionsMenu(true);

        floatingBtn = view.findViewById(R.id.floatingBtn);

        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "add");
                EntryFragment fragment = new EntryFragment();
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameOriginal, fragment);
                fragmentTransaction.commit();

            }
        });


        transactionListRecView = view.findViewById(R.id.transactionListRecView);

        txtNoTransaction = view.findViewById(R.id.txtNoTransaction);

        ArrayList<Transaction> transactions = Utils.getInstance(getContext()).getAllTransactions();

        if (src.equals("pie")) {
            String groupName = bundle.getString("sortBy");

            transactions.removeIf(t -> !t.getCategory().equals(groupName));
        } else if (src.equals("bar")) {
            LocalDate date = LocalDate.ofEpochDay(bundle.getLong("sortBy"));

            transactions.removeIf(t -> !t.getDate().isEqual(date));
        }

        if (transactions.size() != 0) {
            Collections.sort(transactions);

            txtNoTransaction.setVisibility(View.GONE);
            adapter = new TransactionRecViewAdapter(getActivity(), transactions);


            transactionListRecView.setAdapter(adapter);
            transactionListRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            txtNoTransaction.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        searchItem = menu.findItem(R.id.menuItemSearch);

        searchItem.setVisible(true).setEnabled(true);

        searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }


}