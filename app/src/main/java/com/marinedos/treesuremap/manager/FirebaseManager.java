package com.marinedos.treesuremap.manager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marinedos.treesuremap.classes.Plant;
import com.marinedos.treesuremap.classes.User;

import java.util.Date;

/**
 * Manage Firebase interaction (authentication, database...). It is a singleton
 * Created by Marine on 11/01/2018.
 */

public class FirebaseManager {
    /**
     * Instance to manage singleton
     */
    private static final FirebaseManager mInstance = new FirebaseManager();

    /**
     * Getter of the instance of the singleton
     * @return Instance of the FirebaseManager
     */
    public static FirebaseManager getInstance() {
        return mInstance;
    }

    /**
     * Firebase authentication instance
     */
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Firebase database instance
     */
    private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();


    /**
     * Firebase database instance
     */
    private static final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();

    /**
     * Currently connected user
     */
    private static User mCurrentUser = null;

    /**
     * Default constructor
     */
    private FirebaseManager() {
        // Enable offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    /**
     * Method to authenticate to Firebase with a mail and a password
     * @param email - User's email
     * @param password - User's password
     * @return Authentication task
     */
    public Task<AuthResult> signInWithEmailAndPassword(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Retrieve currently logged user
     * @return Currently logged user
     */
    public User getCurrentUser() {
        if (mCurrentUser == null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null) {
                mCurrentUser = new User(user);
            }
        }
        return mCurrentUser;
    }

    /**
     * Add a user in the database of Firebase
     */
    public void addUser() {
        if (mCurrentUser != null) {
            mDatabaseRef.child("users").child(mCurrentUser.getId()).setValue(mCurrentUser);
        }
    }

    /**
     * Add a plant in the database of Firebase
     * @param plant Plant to add
     */
    public void addPlant(Plant plant) {
        if (plant != null) {
            String key = mDatabaseRef.child("plants").child(mCurrentUser.getId()).push().getKey();
            mDatabaseRef.child("plants").child(mCurrentUser.getId()).child(key).setValue(plant);
        }
    }

    /**
     * Update a plant already present in the database of Firebase
     * @param plant Plant to update
     */
    public void updatePlant(Plant plant) {
        if (plant != null) {
            mDatabaseRef.child("plants").child(mCurrentUser.getId()).child(plant.getId()).setValue(plant);
        }
    }

    /**
     * Delete a plant from the database of Firebase
     * @param plant Plant to delete
     */
    public void deletePlant(Plant plant) {
        if (plant != null) {
            mDatabaseRef.child("plants").child(mCurrentUser.getId()).child(plant.getId()).removeValue();
        }
    }

    /**
     * Retrieve plants associated to the connected user
     */
    public void getUserPlants(ValueEventListener listener) {
        DatabaseReference ref = mDatabase.getReference("plants/" + mCurrentUser.getId());
        ref.addValueEventListener(listener);
    }

    /**
     * Convert Firebase snapshot to plant
     * @param plantSnapshot Firebase snapshot of a plant
     * @return The plant
     */
    public Plant parsePlant(DataSnapshot plantSnapshot) {
        String name = (String) plantSnapshot.child("name").getValue();
        long plantingDate = (long) plantSnapshot.child("plantingDate").getValue();
        double longitude = (double) plantSnapshot.child("longitude").getValue();
        double latitude = (double) plantSnapshot.child("latitude").getValue();

        return new Plant(plantSnapshot.getKey(), name, new Date(plantingDate),longitude +", " +latitude);
    }
}
