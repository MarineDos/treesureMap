package com.marinedos.treesuremap.classes;

import com.google.firebase.auth.FirebaseUser;

/**
 * Described a TreesureMap user
 * Created by Marine on 13/01/2018.
 */

public class User {

    /**
     * Username of the user
     */
    private String mUsername;
    /**
     * Email of the user
     */
    private String mEmail;
    /**
     * Firebase id of the user
     */
    private String mId;

    /**
     * Getter for username
     * @return Username of the user
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Setter of the username
     * @param username - Username of the user
     */
    public void setUsername(String username) {
        this.mUsername = username;
    }

    /**
     * Getter of the email
     * @return Email of the user
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Setter of the email
     * @param email - Email of the user
     */
    public void setEmail(String email) {
        this.mEmail = email;
    }

    /**
     * Getter of the user id
     * @return Firebase user id
     */
    public String getId() {
        return mId;
    }

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * User constructor. Create a user from {@link FirebaseUser}
     * @param user Relative FirebaseUser
     */
    public User(FirebaseUser user) {
        this.mUsername = user.getDisplayName();
        this.mEmail = user.getEmail();
        this.mId = user.getUid();
    }
}
