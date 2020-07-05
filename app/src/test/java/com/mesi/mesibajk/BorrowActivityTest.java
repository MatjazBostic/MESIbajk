package com.mesi.mesibajk;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class BorrowActivityTest {
    @Test
    public void noName_shouldMakeErrorToast() {
        BorrowActivity activity = Robolectric.buildActivity(BorrowActivity.class).create().get();
        activity.findViewById(R.id.saveFab).performClick();
        assertEquals(ApplicationProvider.getApplicationContext().getResources().getString(R.string.enter_name_error),
                ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void correctInput_shouldSaveReservationToDatabase() {
        BorrowActivity activity = Robolectric.buildActivity(BorrowActivity.class).create().get();
        String nameAndSurname = "Test Name";
        ((EditText)activity.findViewById(R.id.nameAndSurnameTxt)).setText(nameAndSurname);
        activity.findViewById(R.id.saveFab).performClick();
        DBHelper dbHelper = new DBHelper(ApplicationProvider.getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM IZPOSOJE", null);
        assertTrue(c.moveToFirst());
        assertEquals(1, c.getInt(0));
        assertEquals(nameAndSurname, c.getString(1));
        assertEquals("Razvoj", c.getString(2));
        assertEquals(5, c.getInt(5));
        assertEquals("Službeni", c.getString(6));
        assertEquals("BAJK 1", c.getString(7));
    }
}
