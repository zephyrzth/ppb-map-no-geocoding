package com.example.mapwithoutgeocoding;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "mapDatabase";
    private static final int DATABASE_VERSION = 1;

    // Error Tag
    private static final String ERROR_TAG = "ERROR_TAG";

    // Nama tabel
    private static final String TABLE_KELURAHANS = "kelurahans";

    // Kolom tabel kelurahans
    private static final String KEY_KELURAHANS_ID = "id";
    private static final String KEY_KELURAHANS_NAMA = "nama";
    private static final String KEY_KELURAHANS_LATITUDE = "latitude";
    private static final String KEY_KELURAHANS_LONGITUDE = "longitude";
    private static final String KEY_KELURAHANS_SOUNDEX = "soundex";


    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MAHASISWA_TABLE = "CREATE TABLE " + TABLE_KELURAHANS +
                "(" +
                KEY_KELURAHANS_ID + " INTEGER PRIMARY KEY," +
                KEY_KELURAHANS_NAMA + " TEXT," +
                KEY_KELURAHANS_LATITUDE + " DOUBLE," +
                KEY_KELURAHANS_LONGITUDE + " DOUBLE," +
                KEY_KELURAHANS_SOUNDEX + " TEXT" +
                ");";
        db.execSQL(CREATE_MAHASISWA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void simpanData(Kelurahan kelurahan) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_KELURAHANS_NAMA, kelurahan.getNama());
            contentValues.put(KEY_KELURAHANS_LATITUDE, kelurahan.getLatitude());
            contentValues.put(KEY_KELURAHANS_LONGITUDE, kelurahan.getLongitude());
            contentValues.put(KEY_KELURAHANS_SOUNDEX, kelurahan.getSoundex());

            db.insertOrThrow(TABLE_KELURAHANS, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(ERROR_TAG, "ERROR DB: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public Kelurahan cariData(String nama) {
        String query = String.format("SELECT * FROM %s WHERE %s = ? OR %s = ?",
                TABLE_KELURAHANS,
                KEY_KELURAHANS_NAMA,
                KEY_KELURAHANS_SOUNDEX);
        String soundex = Soundex.getGode(nama);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] { nama, soundex });

        Kelurahan kelurahan = new Kelurahan();
        try {
            if (cursor.moveToFirst()) {
                kelurahan.setNama(cursor.getString(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_NAMA)));
                kelurahan.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_LATITUDE)));
                kelurahan.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_LONGITUDE)));
                kelurahan.setSoundex(cursor.getString(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_SOUNDEX)));
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, "ERROR DB: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return kelurahan;
    }
}