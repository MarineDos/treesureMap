package com.marinedos.treesuremap.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Offer the possibility to validate things (valid email addres, valid date...)
 * Created by Marine on 13/01/2018.
 */

public class ValidatorManager {
    /**
     * Instance to manage singleton
     */
    private static final ValidatorManager mInstance = new ValidatorManager();

    /**
     * Getter of the instance of the singleton
     * @return Instance of the ValidatorManager
     */
    public static ValidatorManager getInstance() {
        return mInstance;
    }

    /**
     * Default constructor
     */
    public ValidatorManager() {

    }

    /**
     * Email regex
     */
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    /**
     * Check if the provided string seems to be an email
     * @param email An email
     * @return true if string seems to be an email
     */
    public boolean isEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }
    /**
     * Check if password seems to be a valid one
     * @param password Entered password
     * @return true if password seems valid
     */
    public boolean isPassword(String password) {
        return password.length() > 4;
    }
}
