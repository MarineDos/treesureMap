package com.marinedos.treesuremap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.marinedos.treesuremap.classes.Plant;
import com.marinedos.treesuremap.manager.FirebaseManager;

public class PlantAvatarSelectionActivity extends AppCompatActivity {

    // UI references.
    private Button mSubmitButton;

    private View mCurrentAvatar;
    private Plant mCurrentPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_avatar_selection);

        mSubmitButton = findViewById(R.id.plant_avatar_submit_button);

        mCurrentPlant = (Plant)getIntent().getSerializableExtra("plant");
    }

    /**
     * Perform on click on an avatar. Unselect previous, select new one
     * @param view View where the user has clicked
     */
    public void selectAvatar(View view){
        if(mCurrentAvatar != null) {
            mCurrentAvatar.setBackground(getResources().getDrawable(R.drawable.hexagon_border));
        }
        if(mCurrentAvatar == null || (String)mCurrentAvatar.getTag() != (String)view.getTag()) {
            view.setBackground(getResources().getDrawable(R.drawable.hexagon_fill_border));
            mCurrentAvatar = view;
            mSubmitButton.setAlpha(1);
        } else{
            view.setBackground(getResources().getDrawable(R.drawable.hexagon_border));
            mCurrentAvatar = null;
            mSubmitButton.setAlpha(0.5f);
        }
    }

    /**
     * Perform on click on validation button. Save selected avatar and back to maps activity
     */
    public void validateAvatar(View view){
        if(mCurrentAvatar != null) {
            mCurrentPlant.setImageId((String)mCurrentAvatar.getTag());
            FirebaseManager.getInstance().addPlant(mCurrentPlant);
            Intent intent = new Intent(this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("latitude", mCurrentPlant.getLatitude());
            intent.putExtra("longitude", mCurrentPlant.getLongitude());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.toast_no_avatar_selected,
                    Toast.LENGTH_LONG).show();
        }
    }
}
