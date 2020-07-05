package com.mesi.mesibajk;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BorrowActivity extends AppCompatActivity {

    private TextView fromText;
    private TextView toText;
    TextView distanceText;
    SeekBar distanceSeekBar;

    public static final SimpleDateFormat DISPLAY_DATE_TIME_FORMAT = new SimpleDateFormat("d.MM.yyyy 'ob' HH:mm", Locale.US);

    Calendar fromDateTime;
    Calendar toDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fromText = findViewById(R.id.dateFrom);
        toText = findViewById(R.id.dateTo);
        distanceSeekBar = findViewById(R.id.distanceSeekBar);
        distanceText = findViewById(R.id.distanceTextView);

        distanceSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        distanceText.setText(String.format(Locale.US, "%d km", distanceSeekBar.getProgress()));

        // Sets up default start and end datetime of reservation
        fromDateTime = Calendar.getInstance();
        fromDateTime.add(Calendar.MINUTE, 1);
        fromText.setText(DISPLAY_DATE_TIME_FORMAT.format(fromDateTime.getTime()));

        toDateTime = Calendar.getInstance();
        toDateTime.add(Calendar.HOUR_OF_DAY, 1);
        toText.setText(DISPLAY_DATE_TIME_FORMAT.format(toDateTime.getTime()));
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            distanceText.setText(String.format(Locale.US, "%d km", progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    /**
     * Called when "from" buton is clicked. First, it shows date dialog and then,
     * time dialog for user to input start time of the reservation
     */
    public void onClickFrom(View view) {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                fromDateTime.set(Calendar.YEAR, year);
                fromDateTime.set(Calendar.MONTH, monthOfYear);
                fromDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(BorrowActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        fromDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        fromDateTime.set(Calendar.MINUTE, selectedMinute);

                        fromText.setText(DISPLAY_DATE_TIME_FORMAT.format(fromDateTime.getTime()));
                    }
                }, fromDateTime.get(Calendar.HOUR_OF_DAY), fromDateTime.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }

        };

        DatePickerDialog dialog = new DatePickerDialog(this, date, fromDateTime.get(Calendar.YEAR),
                fromDateTime.get(Calendar.MONTH), fromDateTime.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(new Date().getTime());
        dialog.show();
    }

    /**
     * Called when "to" buton is clicked. First, it shows date dialog and then,
     * time dialog for user to input end time of the reservation
     */
    public void onClickTo(View view) {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                toDateTime.set(Calendar.YEAR, year);
                toDateTime.set(Calendar.MONTH, monthOfYear);
                toDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(BorrowActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        toDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        toDateTime.set(Calendar.MINUTE, selectedMinute);
                        toText.setText(DISPLAY_DATE_TIME_FORMAT.format(toDateTime.getTime()));
                    }
                }, toDateTime.get(Calendar.HOUR_OF_DAY), toDateTime.get(Calendar.MINUTE), true).show();
            }

        };

        DatePickerDialog dialog = new DatePickerDialog(this, date, toDateTime.get(Calendar.YEAR),
                toDateTime.get(Calendar.MONTH), toDateTime.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(fromDateTime.getTimeInMillis());
        dialog.show();
    }

    /**
     * Saves a new borrow into the database, if the input is correct.
     */
    public void onClickSave(View view) {

        String nameAndSurname = ((TextView)findViewById(R.id.nameAndSurnameTxt)).getText().toString();
        if(nameAndSurname.isEmpty()){
            Toast.makeText(this, R.string.enter_name_error, Toast.LENGTH_LONG).show();
            return;
        }

        if(fromDateTime == null || toDateTime == null){
            Toast.makeText(this, R.string.select_start_and_end_date_error, Toast.LENGTH_LONG).show();
            return;
        }

        if(fromDateTime.getTime().after(toDateTime.getTime())){
            Toast.makeText(this, R.string.end_date_before_start_date_err, Toast.LENGTH_LONG).show();
            return;
        }

        if(fromDateTime.getTime().before(new Date())){
            Toast.makeText(this, R.string.reservation_before_current_time_err, Toast.LENGTH_LONG).show();
            return;
        }

        String sector = (String) ((Spinner)findViewById(R.id.sectorSpinner)).getSelectedItem();
        String intent = (String) ((Spinner)findViewById(R.id.intentSpinner)).getSelectedItem();

        DBHelper dbHelper = new DBHelper(this);

        String freeBikeName = dbHelper.getFreeBike(fromDateTime.getTime(), toDateTime.getTime());
        if(freeBikeName == null){
            Toast.makeText(this, R.string.no_bikes_available_for_this_term, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, getString(R.string.you_have_reserved_the_bike) + " " + freeBikeName, Toast.LENGTH_LONG).show();

        // Save into database
        dbHelper.newLend(nameAndSurname, sector,
                fromDateTime.getTime(),toDateTime.getTime(),
                distanceSeekBar.getProgress(), intent, freeBikeName);

        // End the activity
        finish();
    }
}