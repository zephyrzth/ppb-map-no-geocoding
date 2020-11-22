package com.example.mapwithoutgeocoding;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String ERROR_TAG = "ERROR_TAG";
    private static final float ZOOM = 16.0f;
    private static final String CSV_FILENAME = "list_kelurahan_with_latlong.csv";
    private String csvCompleteFileName;

    private DatabaseHelper databaseHelper;
    private GoogleMap mMap;
    private EditText editTextCari1, editTextCari2;
    private TextView textViewJarak;
    private Button btnCari1, btnCari2, btnImport;
    private Marker markerCari1, markerCari2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initView();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng its = new LatLng(-7.28,112.79);
        markerCari1 = mMap.addMarker(new MarkerOptions().position(its).title("Marker in ITS"));
        markerCari2 = mMap.addMarker(new MarkerOptions().position(its).title("Marker in ITS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(its, ZOOM));
    }

    private void initView() {
        databaseHelper = DatabaseHelper.getInstance(this);
        csvCompleteFileName = (getExternalFilesDir(null).getAbsolutePath() + "/" + CSV_FILENAME);

        editTextCari1 = (EditText) findViewById(R.id.editTextCari1);
        editTextCari2 = (EditText) findViewById(R.id.editTextCari2);
        textViewJarak = (TextView) findViewById(R.id.textViewJarak);
        btnCari1 = (Button) findViewById(R.id.btnCari1);
        btnCari2 = (Button) findViewById(R.id.btnCari2);
        btnImport = (Button) findViewById(R.id.btnImport);

        btnCari1.setOnClickListener(operasi);
        btnCari2.setOnClickListener(operasi);
        btnImport.setOnClickListener(operasi);
    }

    private final View.OnClickListener operasi = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnCari1:
                    hideKeyboard(view);
                    cari1();
                    break;
                case R.id.btnCari2:
                    hideKeyboard(view);
                    cari2();
                    break;
                case R.id.btnImport:
                    importCsv();
                    break;
            }
        }
    };

    private void hideKeyboard(View view) {
        InputMethodManager a = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        a.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void importCsv() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvCompleteFileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                Kelurahan newKelurahan = new Kelurahan();
                newKelurahan.setNama(row[0]);
                newKelurahan.setLatitude(Double.parseDouble(row[1]));
                newKelurahan.setLongitude(Double.parseDouble(row[2]));

                String soundex = Soundex.getGode(row[0]);
                newKelurahan.setSoundex(soundex);

                databaseHelper.simpanData(newKelurahan);
            }
            Log.d(ERROR_TAG, "Berhasil import CSV: " + csvCompleteFileName);
        } catch (Exception e) {
            Log.d(ERROR_TAG, "ERROR: " + e.getMessage());
        }
    }

    public static boolean validateNullText(EditText t) {
        if (t.getText().toString().trim().matches("")) {
            t.setError("Harap diisi!");
            return false;
        }
        return true;
    }

    private void cari1() {
        if (validateNullText(editTextCari1)) {
            String namaKelurahan = editTextCari1.getText().toString();
            Kelurahan kelurahan = databaseHelper.cariData(namaKelurahan);
            if (kelurahan.getNama() == null) {
                Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_LONG).show();
            } else {
                LatLng location = new LatLng(kelurahan.getLatitude(), kelurahan.getLongitude());
                markerCari1.setPosition(location);
                markerCari1.setTitle(kelurahan.getNama());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM));
                setJarak();
                Log.d(ERROR_TAG, "Berhasil cari1");
            }
        }
    }

    private void cari2() {
        if (validateNullText(editTextCari2)) {
            String namaKelurahan = editTextCari2.getText().toString();
            Kelurahan kelurahan = databaseHelper.cariData(namaKelurahan);
            if (kelurahan.getNama() == null) {
                Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_LONG).show();
            } else {
                LatLng location = new LatLng(kelurahan.getLatitude(), kelurahan.getLongitude());
                markerCari2.setPosition(location);
                markerCari2.setTitle(kelurahan.getNama());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM));
                setJarak();
                Log.d(ERROR_TAG, "Berhasil cari2");
            }
        }
    }

    private void setJarak() {
        LatLng location1 = markerCari1.getPosition();
        LatLng location2 = markerCari2.getPosition();

        float hasilJarak = hitungJarak(location1.latitude, location1.longitude, location2.latitude, location2.longitude);
        textViewJarak.setText("Jarak: " + hasilJarak + " km");
        Log.d(ERROR_TAG, "Berhasil setJarak");
    }

    private float hitungJarak(double latAsal, double lngAsal, double latTujuan, double lngTujuan) {
        Location asal = new Location("Asal");
        Location tujuan = new Location("Tujuan");
        asal.setLatitude(latAsal);
        asal.setLongitude(lngAsal);
        tujuan.setLatitude(latTujuan);
        tujuan.setLongitude(lngTujuan);

        float hasilJarak = (float) asal.distanceTo(tujuan) / 1000;
        return hasilJarak;
    }
}