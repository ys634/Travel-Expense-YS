package com.yut.travelexpense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class CategoryListActivity extends AppCompatActivity {

    Button btnNewCategory, btnEditCategory;
    private Toolbar toolbar;
    MenuItem menuItemEdit;
    FrameLayout frameCatInCatList;
    MaterialCardView cardFrame;
    boolean isButtonSelected = false;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Edit Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CategoryFragment fragment = new CategoryFragment();
        fragmentTransaction.replace(R.id.frameCatInCatList, fragment);
        fragmentTransaction.commit();


        btnNewCategory = findViewById(R.id.btnNewCategory);
        btnEditCategory = findViewById(R.id.btnEditCategory);
        frameCatInCatList = findViewById(R.id.frameCatInCatList);
        cardFrame = findViewById(R.id.cardFrame);
        layout = findViewById(R.id.layout);

        // TODO: make cardview clickable

//        ViewTreeObserver observer = cardFrame.getViewTreeObserver();
//
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                isButtonSelected = true;
//                invalidateOptionsMenu();
//            }
//        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isButtonSelected = false;
                invalidateOptionsMenu();
            }
        });

        btnNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryListActivity.this, CategoryAddActivity.class);

                startActivity(intent);

            }
        });

        btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment.getSelectedRbdId() == -1) {
                    fragment.changeBackgroundToRed();
                } else {
                    Intent intent = new Intent(CategoryListActivity.this, CategoryAddActivity.class);

                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        menuItemEdit = menu.findItem(R.id.menuItemEdit);

        if (isButtonSelected) {
            Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
        }
        menuItemEdit.setVisible(isButtonSelected);

        return true;
    }

    //TODO
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}