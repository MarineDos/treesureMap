package com.marinedos.treesuremap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.marinedos.treesuremap.classes.Plant;
import com.marinedos.treesuremap.manager.FirebaseManager;

public class PlantAvatarSelectionActivity extends AppCompatActivity {

    private View mCurrentAvatar;
    private Plant mCurrentPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_avatar_selection);
        mCurrentPlant = (Plant)getIntent().getSerializableExtra("plant");
    }

    /**
     * Perform on click on an avatar. Unselect previous, select new one
     * @param view View where the user has clicked
     */
    public void selectAvatar(View view){
        if(mCurrentAvatar != null) {
            mCurrentAvatar.setBackgroundColor(Color.TRANSPARENT);
        }
        if(mCurrentAvatar == null || (String)mCurrentAvatar.getTag() != (String)view.getTag()) {
            view.setBackgroundColor(getResources().getColor(R.color.yellow));
            mCurrentAvatar = view;
        } else{
            mCurrentAvatar = null;
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
