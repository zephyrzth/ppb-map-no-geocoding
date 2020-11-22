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

    private static final String CSV_FILENAME = "list_kelurahan_with_latlong.csv";
    private static String CSV_COMPLETE_FILENAME;

    // Error Tag
    private final String ERROR_TAG = "ERROR_TAG";

    // Nama tabel
    private static final String TABLE_KELURAHANS = "kelurahans";

    // Kolom tabel kelurahans
    private static final String KEY_KELURAHANS_ID = "id";
    private static final String KEY_KELURAHANS_NAMA = "nama";
    private static final String KEY_KELURAHANS_LATITUDE = "latitude";
    private static final String KEY_KELURAHANS_LONGITUDE = "longitude";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        CSV_COMPLETE_FILENAME = (context.getExternalFilesDir(null).getAbsolutePath() + "/" + CSV_FILENAME);
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
                KEY_KELURAHANS_LONGITUDE + " DOUBLE" +
                ");";
        db.execSQL(CREATE_MAHASISWA_TABLE);

        try {
            CSVReader csvReader = new CSVReader(new FileReader(CSV_COMPLETE_FILENAME));
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                Kelurahan newKelurahan = new Kelurahan();
                newKelurahan.setNama(nextLine[0]);
                newKelurahan.setLatitude(Double.parseDouble(nextLine[1]));
                newKelurahan.setLongitude(Double.parseDouble(nextLine[2]));

                simpanData(newKelurahan);
            }
        } catch (IOException | CsvValidationException e) {
            Log.e(ERROR_TAG, "ERROR DB: " + e.getMessage());
        }
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

            db.insertOrThrow(TABLE_KELURAHANS, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(ERROR_TAG, "ERROR DB: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public Kelurahan cariData(String nama) {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_KELURAHANS,
                KEY_KELURAHANS_NAMA);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] { nama });

        Kelurahan kelurahan = new Kelurahan();
        try {
            if (cursor.moveToFirst()) {
                kelurahan.setNama(cursor.getString(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_NAMA)));
                kelurahan.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_LATITUDE)));
                kelurahan.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_KELURAHANS_LONGITUDE)));
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