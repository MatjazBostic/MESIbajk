package com.mesi.mesibajk;

import android.util.Pair;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DBHelperTest {

    DBHelper dbHelper;

    static final String bajkName ="BAJK 1";


    @Before
    public void setUp(){
        dbHelper = new DBHelper(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void getBikeNames() {
        List<String> bikes = dbHelper.getBikeNames();
        for (int i = 1; i <=7; i++) {
            assertEquals( "BAJK " + i, bikes.get(i-1));
        }
    }


    @Test
    public void getAllDrivenKilometers(){
        Date today = new Date();

        dbHelper.newLend("Test name", "Razvoj", today, today,
                20, "Službeni", bajkName);

        dbHelper.newLend("Test name 2", "Prodaja", today, today,
                10, "Službeni", bajkName);

        assertEquals(30, dbHelper.getAllDrivenKilometers(bajkName));
    }

    @Test
    public void getFreeBajk(){

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);


        assertEquals(bajkName, dbHelper.getFreeBike(now, calendar.getTime()));
        dbHelper.newLend("Test name", "Razvoj", now, calendar.getTime(),
                20, "Službeni", bajkName);

        assertEquals("BAJK 2", dbHelper.getFreeBike(now, calendar.getTime()));

        for(int i = 0; i<=5; i++){
            dbHelper.newLend("Test name", "Razvoj", now, calendar.getTime(),
                    20, "Službeni", "BAJK " + (i+2));
        }

        assertNull(dbHelper.getFreeBike(now, calendar.getTime()));

    }

    @Test
    public void isBikeFree(){

        Date now = new Date();

        assertTrue(dbHelper.isBikeFree(bajkName, now, now));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);

        dbHelper.newLend("Test name", "Razvoj", now, calendar.getTime(),
                20, "Službeni", bajkName);

        assertFalse(dbHelper.isBikeFree(bajkName, now, now));

    }

    @Test
    public void getLastTimeBorrowedDate(){
        Date now = new Date();;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);

        dbHelper.newLend("Test name", "Razvoj", now, calendar.getTime(),
                5, "Službeni", bajkName);

        Pair<Date,Date> dates = dbHelper.getLastTimeBorrowedDate(bajkName);

        assertEquals(dates.first.toString(), now.toString());
        assertEquals(dates.second.toString(), calendar.getTime().toString());

    }


}
