package com.yut.travelexpense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryAddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnCreateCategory;
    private EditText edtTxtCategoryName;
    private RelativeRadioGroup rrAllIcons;
    FloatingActionButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Add a New Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCreateCategory = findViewById(R.id.btnCreateCategory);
        edtTxtCategoryName = findViewById(R.id.edtTxtCategoryName);
        rrAllIcons = findViewById(R.id.rrAllIcons);
        btnHome = findViewById(R.id.btnHome);

        btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance(CategoryAddActivity.this).addCategory(new CategoryModel(edtTxtCategoryName.getText().toString(), rrAllIcons.getCheckedRadioButtonId()));

                Intent intent = new Intent(CategoryAddActivity.this, CategoryListActivity.class);
                startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryAddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}