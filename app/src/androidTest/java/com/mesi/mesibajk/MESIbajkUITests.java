package com.mesi.mesibajk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class MESIbajkUITests {

    // Doesn't work with the non-deprecated InstrumentationRegistry
    private static final String TARGET_PACKAGE =
            InstrumentationRegistry.getTargetContext().getPackageName();

    private static final int LAUNCH_TIMEOUT = 5000;

    UiDevice mDevice;

    /**
     * Sets up the app
     */
    @Before
    public void startBlueprintActivityFromHomeScreen() {

        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the blueprint app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
        assert intent != null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        assert resolveInfo != null;
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * The test adds a new reservation and checks if this reservation is shown correctly in the statistics page.
     * The test won't work correctly if all the bikes are taken
     */
    @Test
    public void test() throws InterruptedException {

        //press the button.
        mDevice.findObject(By.res(TARGET_PACKAGE, "addFab")).click();

        sleep(300);

        String name = "Matjaž Boštic";
        String sector = "Prodaja";

        mDevice.findObject(By.res(TARGET_PACKAGE, "nameAndSurnameTxt"))
                .setText(name);

        // Click an item in spinner
        mDevice.findObject(By.res(TARGET_PACKAGE, "sectorSpinner")).click();
        sleep(100);
        List<UiObject2> children = mDevice.findObjects(By.res("android:id/text1"));
        sleep(100);
        for (UiObject2 uio2 : children) {
            if (sector.equals(uio2.getText())) {
                uio2.click();
                break;
            }
        }

        String selectedDate =  "Od: " + mDevice.findObject(By.res(TARGET_PACKAGE, "dateFrom")).getText() +
        "\ndo: " + mDevice.findObject(By.res(TARGET_PACKAGE, "dateTo")).getText();

        mDevice.findObject(By.res(TARGET_PACKAGE, "saveFab")).click();
        sleep(100);

        DBHelper dbHelper = new DBHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT IME, SEKTOR, BAJK FROM IZPOSOJE ORDER BY ID DESC LIMIT 1", null);
        c.moveToFirst();
        assertEquals(name, c.getString(0));
        assertEquals(sector, c.getString(1));
        String bikeName = c.getString(2);
        c.close();

        int listIndex = Integer.parseInt(bikeName.split(" ")[1]) - 1;
        sleep(1000);
        UiObject2 listView = mDevice.findObject(By.res(TARGET_PACKAGE, "bikesLV"));
        listView.getChildren().get(listIndex).click();

        UiObject2 nextReservationTV = mDevice.findObject(By.res(TARGET_PACKAGE, "nextBorrowTV"));

        assertEquals(bikeName, mDevice.findObject(By.res(TARGET_PACKAGE, "bikeNameTV")).getText());

        assertEquals(selectedDate, nextReservationTV.getText());

    }

}
