package com.mesi.mesibajk;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class StatisticsActivityTest {

    static final String bikeName = "BAJK 1";

    @Test
    public void statisticsActivityStart_shouldPutNameOfBikeIntoTextView() {

        Intent i = new Intent();
        i.putExtra("bajkName", bikeName);
        StatisticsActivity activity = Robolectric.buildActivity(StatisticsActivity.class, i).create().get();

        assertEquals(bikeName, ((TextView)activity.findViewById(R.id.bikeNameTV)).getText());

    }

    @Test
    public void databaseEntry_shouldBeDisplayedOnStatisticsActivity() {

        Date today = new Date();

        DBHelper dbHelper = new DBHelper(ApplicationProvider.getApplicationContext());
        dbHelper.newLend("Test name", "Razvoj", today, today,
                20, "Službeni", bikeName);

        String bikeName = "BAJK 1";
        Intent i = new Intent();
        i.putExtra("bajkName", bikeName);
        StatisticsActivity activity = Robolectric.buildActivity(StatisticsActivity.class, i).create().get();

        assertEquals( ApplicationProvider.getApplicationContext().getString(R.string.from) + ": "
                        + BorrowActivity.DISPLAY_DATE_TIME_FORMAT.format(today) + "\n"+
                        ApplicationProvider.getApplicationContext().getString(R.string.to) + ": "  + BorrowActivity.DISPLAY_DATE_TIME_FORMAT.format(today),
                ((TextView)activity.findViewById(R.id.lastBorrowTV)).getText());

    }

}
