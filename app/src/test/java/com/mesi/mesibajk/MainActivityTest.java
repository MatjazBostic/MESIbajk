package com.mesi.mesibajk;

import android.content.Intent;
import android.widget.ListView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    @Test
    public void clickingAdd_shouldStartBorrowActivity() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        activity.findViewById(R.id.addFab).performClick();

        Intent expectedIntent = new Intent(activity, BorrowActivity.class);
        //Doesn't work with non-deprecated property
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void listView_shouldHave7Items() {
        //Doesn't work with non-deprecated method
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        ListView bikesListView = activity.findViewById(R.id.bikesLV);

        assertEquals(7, bikesListView.getCount());
    }

    @Test
    public void clickingListItem_shouldStartStatisticsActivity() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        ListView bikesListView = activity.findViewById(R.id.bikesLV);

        int mActivePosition = 0;
        //perform click on the first listView item
        bikesListView.performItemClick(
                bikesListView.getAdapter().getView(mActivePosition, null, null),
                mActivePosition,
                bikesListView.getAdapter().getItemId(mActivePosition));

        Intent expectedIntent = new Intent(activity, StatisticsActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();

        assertEquals(actual.getStringExtra("bajkName"), "BAJK 1");
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }
}