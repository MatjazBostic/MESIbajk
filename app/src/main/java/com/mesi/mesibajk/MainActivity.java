package com.mesi.mesibajk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.bikesLV);
    }

    @Override
    public void onStart(){
        super.onStart();
        final List<Bajk> bajkList = new DBHelper(this).getBikes();
        // Starts the statistics activity when item in listView gets clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, StatisticsActivity.class);

                i.putExtra("bajkName", bajkList.get(position).getName());
                startActivity(i);
            }
        });

        // Create the adapter to convert the array to views
        BajksAdapter adapter = new BajksAdapter(this, bajkList);

        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
    }

    public void onClickAdd(View view) {
        startActivity(new Intent(this, BorrowActivity.class));
    }
}