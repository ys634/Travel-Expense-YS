package com.yut.travelexpense;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.versionedparcelable.ParcelUtils;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class TransactionListFragment extends Fragment {

    View view;

    RecyclerView transactionListRecView;
    FloatingActionButton floatingBtn;
    TextView txtNoTransaction;
    ImageView imgDownArrow;
    TransactionSectionedRecViewAdapter adapter;
    SearchView searchView;
    MenuItem searchItem;

    Bundle bundle;
    String src;

    ArrayList<Transaction> transactions = new ArrayList<>();

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

        List<Section> sections = new ArrayList<>();

        transactionListRecView = view.findViewById(R.id.transactionListRecView);

        txtNoTransaction = view.findViewById(R.id.txtNoTransaction);
        imgDownArrow = view.findViewById(R.id.imgDownArrow);

        transactions = Utils.getInstance(getContext()).getAllTransactions();
        List<Transaction> tempTransactions = new ArrayList<>();

        if (transactions != null) {
            if (transactions.size() != 0) {
                for (int i = 0; i < transactions.size(); i++) {
                    Transaction t = transactions.get(i);
                    long span = t.getSpread();
                    if (span > 1) {
                        t.setOriginalAmount(t.getOriginalAmount()/span);
                        for(int j = 1; j < span; j++) {
                            Transaction duplicate = new Transaction(t);
                            duplicate.setDate(duplicate.getDate().plusDays(j));
                            duplicate.setOriginalAmount(t.getOriginalAmount());
                            tempTransactions.add(duplicate);
                        }

                    }

                    tempTransactions.add(t);
                }

//                for (Transaction t: tempTransactions) {
//                    Toast.makeText(getContext(), "date: " + t.getDate(), Toast.LENGTH_SHORT).show();
//                }

                if (src.equals("pie")) {
                    String groupName = bundle.getString("sortBy");

                    tempTransactions.removeIf(t -> !t.getCategory().equals(groupName));
                } else if (src.equals("bar")) {
                    LocalDate date = LocalDate.ofEpochDay(bundle.getLong("sortBy"));

                    tempTransactions.removeIf(t -> !t.getDate().isEqual(date));
                }


                Map<LocalDate, List<Transaction>> transactionsByDate = new TreeMap<>();
                for (Transaction t: tempTransactions) {
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

                txtNoTransaction.setVisibility(View.GONE);
                imgDownArrow.setVisibility(View.GONE);

                adapter = new TransactionSectionedRecViewAdapter(getActivity(), sections);

                transactionListRecView.setAdapter(adapter);
                transactionListRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                txtNoTransaction.setVisibility(View.VISIBLE);
                imgDownArrow.setVisibility(View.VISIBLE);
            }
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