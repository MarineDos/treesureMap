package com.marinedos.treesuremap;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marinedos.treesuremap.classes.Plant;
import com.marinedos.treesuremap.manager.FirebaseManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener {

    private int MY_LOCATION_REQUEST_CODE = 1;

    // UI references
    private GoogleMap mMap;
    private FloatingActionButton mAddButton;
    private LinearLayout mOverlay;
    private TextView mPlantName;
    private TextView mPlantingDate;
    private ImageView mPlantAvatar;
    private LinearLayout mDeletePlant;
    private LinearLayout mEditPlant;

    private Plant mCurrentPlant;
    private boolean mOverlayIsShown;
    private boolean mOverlayIsExpand;
    private double savedLatitude;
    private double savedLongitude;
    /**
     * Map that associated a plant id to a marker in the map
     */
    private Map<String, Marker> mMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMarkers = new HashMap<String, Marker>();

        mOverlay = findViewById(R.id.overlay);
        mPlantName = findViewById(R.id.plant_name);
        mPlantingDate = findViewById(R.id.planting_date);
        mPlantAvatar = findViewById(R.id.plant_avatar);
        mDeletePlant = findViewById(R.id.delete_plant);
        mEditPlant = findViewById(R.id.edit_plant);
        mAddButton = findViewById(R.id.addPlant);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddButton.isShown()){
                    openPlantCreation();
                }
            }
        });
        mDeletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePlant();
            }
        });
        mEditPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPlant();
            }
        });
        mPlantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleOverlay();
            }
        });

        savedLatitude = getIntent().getDoubleExtra("latitude", 48.021169);
        savedLongitude = getIntent().getDoubleExtra("longitude", -1.473520);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mOverlayIsShown = true;
        mOverlayIsExpand = true;
        this.collapseOverlay(false, false);
        this.hideOverlay(false, false);
        this.updateOverlay(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMarkerClickListener(this);
                mMap.setOnCameraMoveStartedListener(this);
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
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng defaultLocation = new LatLng(savedLatitude, savedLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 35.0f));
        mMap.setOnMarkerDragListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnCameraMoveStartedListener(this);
            initUserPlants();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);

        }
    }
    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        Plant plant = (Plant) marker.getTag();
        this.updateOverlay(plant);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    /** Called when the user start dragging a marker. */
    @Override
    public void onMarkerDragStart(final Marker marker) {
        marker.setAlpha(0.7f);
    }

    /** Called when the user drag a marker. */
    @Override
    public void onMarkerDragEnd(final Marker marker) {
        marker.setAlpha(1f);
        // Retrieve the data from the marker.
        Plant plant = (Plant) marker.getTag();

        // Update in DB the plant with new position
        LatLng newPosition = marker.getPosition();
        plant.setLongitude(newPosition.longitude);
        plant.setLatitude(newPosition.latitude);
        FirebaseManager.getInstance().updatePlant(plant);

    }

    /** Called when the user stop dragging a marker. */
    @Override
    public void onMarkerDrag(final Marker marker) {
        marker.setAlpha(0.7f);

    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            this.hideOverlay(true, false);
        }
    }

    /**
     * Update overlay information with plant information
     * @param plant Plant to be displayed
     */
    private void updateOverlay(Plant plant) {
        if (plant != null) {
            mPlantName.setText(plant.getName());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            mPlantingDate.setText(sdf.format(plant.getPlantingDate()));
            if(plant.getImageId() != null) {
                Resources resources = this.getResources();
                final int resourceId = resources.getIdentifier(plant.getImageId(), "drawable", this.getPackageName());
                mPlantAvatar.setImageResource(resourceId);
            } else {
                mPlantAvatar.setImageResource(0);
            }
            mCurrentPlant = plant;
            this.showOverlay(true, true);
        } else {
            // Otherwise, reinit
            mPlantName.setText("");
            mPlantingDate.setText("");
            mCurrentPlant = null;
            mPlantAvatar.setImageResource(0);
            this.hideOverlay(true, true);
        }
    }

    /**
     * Show the overlay with plant information
     * @param animate Boolean to know if animation is needed
     * @param delay Boolean to know if delay is needed
     */
    private void showOverlay(boolean animate, boolean delay) {
        if(!mOverlayIsShown) {
            mAddButton.hide();
            mOverlayIsShown = true;
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            if(animate) {
                animation.setDuration(600);
            }
            if (delay) {
                animation.setStartOffset(200);
            }
            animation.setFillAfter(true);
            animation.setInterpolator(MapsActivity.this, android.R.interpolator.accelerate_decelerate);
            mOverlay.startAnimation(animation);
            mOverlay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide the overlay with plant information
     * @param animate Boolean to know if animation is needed
     * @param delay Boolean to know if delay is needed
     */
    private void hideOverlay(boolean animate, boolean delay) {
        if(mOverlayIsShown) {
            mOverlayIsShown = false;
            mAddButton.show();
            collapseOverlay(false, false);
            Animation animation = new AlphaAnimation(1.0f, 0.0f);
            if(animate) {
                animation.setDuration(600);
            }
            if (delay) {
                animation.setStartOffset(200);
            }
            animation.setFillAfter(true);
            animation.setInterpolator(MapsActivity.this, android.R.interpolator.accelerate_decelerate);
            mOverlay.startAnimation(animation);
            mOverlay.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Toggle overlay with animation
     */
    private void toogleOverlay(){
        if(mOverlayIsExpand && mOverlay.isShown()) {
            collapseOverlay(true, false);
        } else if (mOverlay.isShown()) {
            expandOverlay(true, false);
        }
    }

    /**
     * Expand the overlay
     * @param animate Boolean to know if animation is needed
     * @param delay Boolean to know if delay is needed
     */
    private void expandOverlay(boolean animate, boolean delay) {
        if(!mOverlayIsExpand) {
            mOverlayIsExpand = true;
            final float scale = getResources().getDisplayMetrics().density;
            ValueAnimator animation = ValueAnimator.ofFloat(130 * scale, 0);
            if(animate) {
                animation.setDuration(600);
            }
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    float animatedValue = (float)updatedAnimation.getAnimatedValue();
                    mOverlay.setTranslationY(animatedValue);
                }
            });
            animation.start();
        }
    }

    /**
     * Collapse the overlay
     * @param animate Boolean to know if animation is needed
     * @param delay Boolean to know if delay is needed
     */
    private void collapseOverlay(boolean animate, boolean delay) {
        if(mOverlayIsExpand) {
            mOverlayIsExpand = false;
            final float scale = getResources().getDisplayMetrics().density;
            ValueAnimator animation = ValueAnimator.ofFloat(0f, 130 * scale);
            if(animate) {
                animation.setDuration(600);
            }
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    float animatedValue = (float)updatedAnimation.getAnimatedValue();
                    mOverlay.setTranslationY(animatedValue);
                }
            });
            animation.start();
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
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(plant.getLatitude(), plant.getLongitude()))
                            .draggable(true));
                    marker.setTag(plant);
                    if (plant.getImageId() != null) {
                        Resources resources = getResources();
                        final int resourceId = resources.getIdentifier(plant.getImageId() + "_marker", "drawable", getPackageName());
                        marker.setIcon(BitmapDescriptorFactory.fromResource(resourceId));
                    }
                    Marker previousMarker = mMarkers.get(plant.getId());
                    if(previousMarker != null) {
                        previousMarker.remove();
                    }
                    mMarkers.put(plant.getId(), marker);
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

    /**
     * Delete current selected plant
     */
    private void deletePlant() {
        if (mCurrentPlant != null) {
            FirebaseManager.getInstance().deletePlant(mCurrentPlant);
            mMarkers.get(mCurrentPlant.getId()).remove();
            this.updateOverlay(null);
            Toast.makeText(this, R.string.toast_delete_plant,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Edit current selected plant
     */
    private void editPlant() {

        Intent intent = new Intent(this, PlantEditionActivity.class);
        intent.putExtra("plant", mCurrentPlant);
        startActivity(intent);
    }
}
