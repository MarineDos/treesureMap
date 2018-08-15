package com.marinedos.treesuremap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.marinedos.treesuremap.classes.Plant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlantCreationActivity extends AppCompatActivity {

    private int MY_LOCATION_REQUEST_CODE = 1;
    private String DATE_FORMAT = "dd/MM/yyyy";

    // UI references.
    private EditText mPlantName;
    private EditText mPlantingDate;
    private EditText mLocation;
    private Button mSubmit;

    final Calendar mCalendar = Calendar.getInstance();
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_creation);

        mPlantName = findViewById(R.id.plant_name);
        mPlantingDate = findViewById(R.id.planting_date);
        mLocation = findViewById(R.id.plant_location);
        mSubmit = findViewById(R.id.plant_creation_submit_button);
        mPlantName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mPlantName.getText().toString().length() > 0) {
                    mSubmit.setAlpha(1);
                } else {
                    mSubmit.setAlpha(0.5f);
                }
            }
        });

                // Create a date picker
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDatePickerLabel();
            }

        };

        mPlantingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PlantCreationActivity.this, date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Add location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. So put it in corresponding editText
                            if (location != null) {
                                mLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                            }
                        }
                    });
        }

        // Add plant to DB
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlant();
            }
        });

        // Init date with today
        updateDatePickerLabel();
    }

    /**
     * Update edit text when date as been take from date picker
     */
    private void updateDatePickerLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.FRENCH);
        mPlantingDate.setText(sdf.format(mCalendar.getTime()));
    }

    /**
     * Create plant from user input and add it to the database
     */
    private void addPlant(){

        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.FRENCH);
        Date date = null;
        try {
            date = format.parse(mPlantingDate.getText().toString());
        } catch (ParseException e) {
            date = new Date();
        }
        String plantName = mPlantName.getText().toString();
        if (plantName.length() > 0){
            Plant plant = new Plant(mPlantName.getText().toString(), date, mLocation.getText().toString());

            Intent intent = new Intent(this, PlantAvatarSelectionActivity.class);
            intent.putExtra("plant", plant);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.plant_name_required,
                    Toast.LENGTH_LONG).show();
        }
    }
}
