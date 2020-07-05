package com.mesi.mesibajk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private Spinner intentSpinner;
    private Spinner sectorSpinner;
    private TextView noOfReservationsTV;
    private DBHelper dbHelper = new DBHelper(this);
    private String bajkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bajkName = getIntent().getStringExtra("bajkName");

        ((TextView)findViewById(R.id.bikeNameTV)).setText(bajkName);

        TextView lastBorrowTV = findViewById(R.id.lastBorrowTV);
        TextView nextBorrowTV = findViewById(R.id.nextBorrowTV);
        TextView noOfKms = findViewById(R.id.noOfKms);
        intentSpinner = findViewById(R.id.intentSpinner);
        sectorSpinner = findViewById(R.id.sectorSpinner);
        noOfReservationsTV = findViewById(R.id.noOfReservations);

        //Set up borrow dates
        Pair<Date, Date> lastBorrowDates = dbHelper.getLastTimeBorrowedDate(bajkName);

        if(lastBorrowDates == null) {
            lastBorrowTV.setText(R.string.no_data);
        } else {
            String lastBorrowStr = "Od: " + BorrowActivity.DISPLAY_DATE_TIME_FORMAT.format(lastBorrowDates.first) +
                    "\ndo: " + BorrowActivity.DISPLAY_DATE_TIME_FORMAT.format(lastBorrowDates.second);
            lastBorrowTV.setText(lastBorrowStr);
        }

        Pair<Date, Date> nextBorrowDates = dbHelper.getNextBorrowedDate(bajkName);

        if(nextBorrowDates == null) {
            nextBorrowTV.setText(R.string.no_data);
        } else {
            String nextBorrowStr = "Od: " + BorrowActivity.DISPLAY_DATE_TIME_FORMAT.format(nextBorrowDates.first) +
                    "\ndo: " + BorrowActivity.DISPLAY_DATE_TIME_FORMAT.format(nextBorrowDates.second);
            nextBorrowTV.setText(nextBorrowStr);
        }

        int kms = dbHelper.getAllDrivenKilometers(bajkName);
        noOfKms.setText(String.format(Locale.US, "%d km", kms));

        intentSpinner.setOnItemSelectedListener(spinnerListener);
        sectorSpinner.setOnItemSelectedListener(spinnerListener);

        setNumOfReservations();
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setNumOfReservations();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    void setNumOfReservations(){
        String sector = (String) sectorSpinner.getSelectedItem();
        String intent = (String) intentSpinner.getSelectedItem();
        int noOfReservations = dbHelper.getNumberOfReservations(bajkName, intent, sector);
        noOfReservationsTV.setText(String.valueOf(noOfReservations));
    }

}