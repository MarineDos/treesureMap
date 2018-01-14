package com.marinedos.treesuremap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marinedos.treesuremap.classes.Plant;
import com.marinedos.treesuremap.manager.FirebaseManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private int MY_LOCATION_REQUEST_CODE = 1;

    // UI references
    private GoogleMap mMap;
    private FloatingActionButton mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAddButton = findViewById(R.id.addPlant);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlantCreation();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                initUserPlants();
            } else {
                // We will need user location, inform him that it is important
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage(R.string.dialog_location_needed).setTitle(R.string.dialog_title_location_needed);
                builder.setPositiveButton(R.string.activate_gps, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_LOCATION_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
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

        LatLng defaultLocation = new LatLng(48.021169, -1.473520);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 35.0f));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            initUserPlants();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);

        }
    }

    /**
     * Init markers on the map with user's plants
     */
    private void initUserPlants(){
        FirebaseManager.getInstance().getUserPlants(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                ArrayList<Plant> plants = new ArrayList<>();
                for (DataSnapshot plantSnapshot: dataSnapshot.getChildren()) {
                    Plant plant = FirebaseManager.getInstance().parsePlant(plantSnapshot);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(plant.getLongitude(), plant.getLatitude()))
                            .title(plant.getName() + " - " + sdf.format(plant.getPlantingDate())));
                    plants.add(plant);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO
            }
        });
    }

    /**
     * Open plant creation activity
     */
    private void openPlantCreation() {
        Intent intent = new Intent(this, PlantCreationActivity.class);
        startActivity(intent);
    }
}
