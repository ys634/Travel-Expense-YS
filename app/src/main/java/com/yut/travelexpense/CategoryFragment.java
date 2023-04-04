package com.yut.travelexpense;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    View view;
    private CardView cardCategory;
    RelativeRadioGroup rrGroupCategories;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_category, container, false);

        ArrayList<CategoryModel> categories = Utils.getInstance(getContext()).getAllCategories();
        int noOfCat = categories.size();

        cardCategory = view.findViewById(R.id.cardCategory);
        rrGroupCategories = view.findViewById(R.id.rrAllIcons);


        if (noOfCat <= 8) {
            RadioButton radioButton9 = view.findViewById(R.id.radioBtn9);
            RadioButton radioButton10 = view.findViewById(R.id.radioBtn10);
            RadioButton radioButton11 = view.findViewById(R.id.radioBtn11);
            RadioButton radioButton12 = view.findViewById(R.id.radioBtn12);

            radioButton9.setVisibility(View.GONE);
            radioButton10.setVisibility(View.GONE);
            radioButton11.setVisibility(View.GONE);
            radioButton12.setVisibility(View.GONE);
        }

        ArrayList<Integer> btnIDs = new ArrayList<>();

        for (int i = 0; i < noOfCat; i++) {
            int j = i + 1;
            String btnName = "radioBtn" + j;

            int btnID = this.getResources().getIdentifier(btnName, "id", getActivity().getPackageName());

            btnIDs.add(btnID);

            RadioButton radioButton = (RadioButton) view.findViewById(btnIDs.get(i));
            radioButton.setText(categories.get(i).getName());
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null,
                    ResourcesCompat.getDrawable(getResources(), categories.get(i).getImageURL(), null),
                    null,
                    null);

        }

        cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.white_border, null));
            }
        });


        return view;
    }


    public int getSelectedRbdId() {
        return rrGroupCategories.getCheckedRadioButtonId();
    }

    public String getSelectedCategory(int rdbId) {
        String category;
        if (rdbId == -1) {
            category = "None";
        } else {
            RadioButton rdb = view.findViewById(rdbId);
            category = rdb.getText().toString();
        }
        return category;
    }

    public void changeBackgroundToRed() {
        cardCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.red_border, null));

    }


}