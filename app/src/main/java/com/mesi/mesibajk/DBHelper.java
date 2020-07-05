package com.mesi.mesibajk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static final SimpleDateFormat DATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public DBHelper(Context context){
        super(context, "MESI_BAJK_DB",null,1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table KOLESA " +
                        "(IME TEXT PRIMARY KEY)");

        db.execSQL("create table IZPOSOJE " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, IME TEXT, SEKTOR TEXT, OD TEXT, DO TEXT, " +
                "RAZDALJA INTEGER, NAMEN TEXT, BAJK TEXT)");

        for(int i = 1; i <= 7; i++) {
            db.execSQL("INSERT INTO KOLESA (IME) VALUES ('BAJK " + i + "')");
        }
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

    /**
     * @return list of names of all bikes
     */
    public List<String> getBikeNames(){
        List<String> bajks = new ArrayList<>();

        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM KOLESA", null);

        if (cursor.moveToFirst()) {
            do {
                bajks.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bajks;
    }

    /**
     * @return list of all bikes
     */
    public List<Bajk> getBikes(){
        List<Bajk> bajks = new ArrayList<>();

        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM KOLESA", null);

        if (cursor.moveToFirst()) {
            do {
                Date now = new Date();
                bajks.add(new Bajk(cursor.getString(0),
                        isBikeFree(cursor.getString(0), now, now)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bajks;
    }

    /**
     * Saves a new lend into the database
     */
    public void newLend(String nameAndSurname, String sector, Date from, Date to, int distance, String intent, String bikeName){
        getReadableDatabase().execSQL("INSERT INTO IZPOSOJE (IME, SEKTOR, OD, DO, RAZDALJA, NAMEN, BAJK) " +
                "VALUES ('" + nameAndSurname + "','" + sector + "','" + DATABASE_DATE_FORMAT.format(from.getTime()) + "','"
                + DATABASE_DATE_FORMAT.format(to.getTime()) + "'," + distance +
                ",'" + intent + "','" + bikeName + "')");
    }


    /**
     * Gets the first free bike between start and end;
     * @param start start date of desired reservation
     * @param end end date of desired reservation
     * @return name of the free bike
     */
    public String getFreeBike(Date start, Date end){
        for(String bike : getBikeNames()){
            if(isBikeFree(bike, start, end)){
                return bike;
            }
        }
        return null;
    }

    /**
     * Check if the bike with the given name is available
     * @param name name of the bike
     * @param start start date of the reservation
     * @param end end date of the reservation
     * @return true, if the bike is available on the given date span
     */
    public boolean isBikeFree(String name, Date start, Date end){

        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  OD, DO, BAJK FROM IZPOSOJE WHERE BAJK = '" + name +
                "'", null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    Date from = DATABASE_DATE_FORMAT.parse(cursor.getString(0));
                    Date to = DATABASE_DATE_FORMAT.parse(cursor.getString(1));

                    assert to != null;
                    if(!(end.before(from) || to.before(start))){
                        cursor.close();
                        return false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        return  true;
    }

    /**
     * Get start and end date of the last reservation
     * @param bikeName name of the bike
     * @return start and end date of the last reservation
     */
    Pair<Date, Date> getLastTimeBorrowedDate(String bikeName){
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT OD, DO, BAJK FROM IZPOSOJE " +
                "WHERE BAJK = '" + bikeName + "' ORDER BY OD;", null);

        Date today = new Date();

        if (cursor.moveToLast()) {
            do {
                try {
                    Date from = DATABASE_DATE_FORMAT.parse(cursor.getString(0));

                    assert from != null;
                    if (from.before(today)) {
                        return new Pair<>(
                                DATABASE_DATE_FORMAT.parse(cursor.getString(0)),
                                DATABASE_DATE_FORMAT.parse(cursor.getString(1))
                        );

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToPrevious());

        }
        cursor.close();
        return null;
    }

    /**
     * Get start and end date of the next reservation
     * @param bikeName name of the bike
     * @return start and end date of the next reservation
     */
    Pair<Date, Date> getNextBorrowedDate(String bikeName){
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT OD, DO, BAJK from IZPOSOJE " +
                "WHERE BAJK = '" + bikeName + "' ORDER BY OD;", null);

        Date today = new Date();

        if (cursor.moveToLast()) {
            // If there is only one row
            if(!cursor.moveToPrevious()){
                cursor.moveToLast();
                try {
                    Date from = DATABASE_DATE_FORMAT.parse(cursor.getString(0));
                    if(today.before(from)){
                    return new Pair<>(
                            from,
                            DATABASE_DATE_FORMAT.parse(cursor.getString(1)));
                        } else {
                        return null;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            cursor.moveToNext();
            // If there are multiple rows
            do {
                try {
                    Date from = DATABASE_DATE_FORMAT.parse(cursor.getString(0));

                    if (!today.before(from)) {
                        // We went too far. Go one back.
                        cursor.moveToNext();

                        if(cursor.isAfterLast()){
                            return null;
                        }

                        return new Pair<>(
                                DATABASE_DATE_FORMAT.parse(cursor.getString(0)),
                                DATABASE_DATE_FORMAT.parse(cursor.getString(1))
                        );

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToPrevious());

        }
        cursor.close();
        return null;
    }

    /**
     * @param bikeName name of the bike
     * @return Number of all driven kilometers for the given bike
     */
    int getAllDrivenKilometers(String bikeName){
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT RAZDALJA, BAJK FROM IZPOSOJE WHERE BAJK = '" +
                bikeName + "';", null);

        int kms = 0;

        if (cursor.moveToFirst()) {
            do {

                kms += cursor.getInt(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return kms;
    }

    /**
     *
     * @param bajk name of the bike
     * @param intent intent string
     * @param sector sector name
     * @return number of reservation for the given bike name, intent and sector
     */
    int getNumberOfReservations(String bajk, String intent, String sector){
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SEKTOR, NAMEN, BAJK FROM IZPOSOJE WHERE NAMEN = '" +
                intent + "' AND SEKTOR = '" + sector + "' AND BAJK = '" + bajk +"';", null);

        int numReservations = 0;

        if (cursor.moveToFirst()) {
            do {
                numReservations++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        return numReservations;
    }

}