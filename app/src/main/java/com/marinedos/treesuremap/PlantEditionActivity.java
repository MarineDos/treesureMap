package com.marinedos.treesuremap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.marinedos.treesuremap.classes.Plant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlantEditionActivity extends AppCompatActivity {
    private int MY_LOCATION_REQUEST_CODE = 1;
    private String DATE_FORMAT = "dd/MM/yyyy";

    // UI references.
    private EditText mPlantName;
    private EditText mPlantingDate;
    private EditText mLocation;
    private Button mSubmit;
    private Plant mCurrentPlant;

    final Calendar mCalendar = Calendar.getInstance();
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_edition);
        mCurrentPlant = (Plant)getIntent().getSerializableExtra("plant");

        mPlantName = findViewById(R.id.plant_name);
        mPlantingDate = findViewById(R.id.planting_date);
        mLocation = findViewById(R.id.plant_location);
        mSubmit = findViewById(R.id.plant_edition_submit_button);

        mPlantName.setText(mCurrentPlant.getName());
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

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.FRENCH);
        mPlantingDate.setText(sdf.format(mCurrentPlant.getPlantingDate()));

        mLocation.setText(mCurrentPlant.getLatitude() + ", " + mCurrentPlant.getLongitude());

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
                new DatePickerDialog(PlantEditionActivity.this, date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // Add plant to DB
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPlant();
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
    private void editPlant(){

        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.FRENCH);
        Date date = null;
        try {
            date = format.parse(mPlantingDate.getText().toString());
            mCurrentPlant.setPlantingDate(date);
        } catch (ParseException e) {}
        String plantName = mPlantName.getText().toString();
        if (plantName.length() > 0){
            mCurrentPlant.setName(mPlantName.getText().toString());
            Intent intent = new Intent(this, PlantAvatarSelectionEditionActivity.class);
            intent.putExtra("plant", mCurrentPlant);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.plant_name_required,
                    Toast.LENGTH_LONG).show();
        }
    }

}
