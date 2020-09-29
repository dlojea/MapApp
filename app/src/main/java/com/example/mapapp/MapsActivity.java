package com.example.mapapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private static GoogleMap googleMap;
    private Geocoder geocoder;
    private SQLiteDatabase db;
    private LatLng Ourense = new LatLng(42.335712, -7.863879);
    private ArrayList<MarkerOptions> listOfMarkers = new ArrayList<>();
    private boolean isMapReady = false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        geocoder = new Geocoder(this);

        try {
            File file = new File(this.getFilesDir(),"markers");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null){
                Log.println(Log.INFO,"Read", line);
                String[] stringArray = line.split(";");
                String title = stringArray[0];
                String snippet = stringArray[1];
                LatLng position = new LatLng(Double.parseDouble(stringArray[2]), Double.parseDouble(stringArray[3]));

                MarkerOptions m = new MarkerOptions()
                        .position(position)
                        .title(title)
                        .snippet(snippet);

                listOfMarkers.add(m);

                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        try {
            File file = new File(this.getFilesDir(), "markers");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (MarkerOptions marker : listOfMarkers) {
                bufferedWriter.write(marker.getTitle() + ";" + marker.getSnippet() + ";" + marker.getPosition().latitude + ";" + marker.getPosition().longitude + "\n");
                Log.println(Log.INFO, "Write", marker.getTitle() + ";" + marker.getSnippet() + ";" + marker.getPosition().latitude + ";" + marker.getPosition().longitude);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady (GoogleMap mMap) {
        googleMap = mMap;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Ourense, 15));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_LONG).show();
                MapsActivity.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent i = new Intent(MapsActivity.this, MarkerActivity.class);
                i.putExtra("latLng", latLng);
                startActivityForResult(i, 0);
            }
        });
        isMapReady = true;
        loadMarkers();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                //String name = data.getStringExtra("name");
                String description = data.getStringExtra("description");
                LatLng latLng = (LatLng) data.getExtras().get("latLng");
                addNewMarkerOnMap(description, latLng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMarkers() {
        if (!listOfMarkers.isEmpty()) {
            for (MarkerOptions m : listOfMarkers) {
                googleMap.addMarker(m);
            }
        }
    }

    private void addNewMarkerOnMap (String description, LatLng latLng) throws IOException{
        Address address = this.getAddressFromLatLng(latLng);

        String street = address.getThoroughfare() + ", " + address.getSubThoroughfare();
        String cp = address.getPostalCode() + ", " + address.getLocality();

        MarkerOptions m = new MarkerOptions()
                .position(latLng)
                .title(street + " (" + cp + ")")
                .snippet("Descripcion: " + description);

        googleMap.addMarker(m);
        listOfMarkers.add(m);
        Toast.makeText(this, "Marcador creado", Toast.LENGTH_LONG).show();
    }

    private Address getAddressFromLatLng (final LatLng latLng) throws IOException {
        List<Address> addresses;
        addresses = this.geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
        Address address = null;
        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
        }
        return address;
    }


}
