package com.marinedos.treesuremap.manager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marinedos.treesuremap.classes.Plant;
import com.marinedos.treesuremap.classes.User;

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
            mCurrentUser = new User(user);
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
     * Retrieve plants associated to the connected user
     */
    public void getUserPlants() {
        DatabaseReference ref = mDatabase.getReference("plants/" + mCurrentUser.getId());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO
            }
        };
        ref.addValueEventListener(postListener);
    }
}
