package com.marinedos.treesuremap.classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Described a plant that belongs to a user with his location
 * Created by Marine on 13/01/2018.
 */

public class Plant  implements Serializable {
    /**
     * Getter for plant name
     * @return Plant name
     */
    public String getName() {
        return mName;
    }

    /**
     * Setter for the plant name
     * @param name Plant name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Getter for planting date
     * @return Planting date as a timestamp
     */
    public long getPlantingDate() {
        return mPlantingDate;
    }

    /**
     * Setter for planting date
     * @param plantingDate Planting date
     */
    public void setPlantingDate(Date plantingDate) {
        this.mPlantingDate = plantingDate.getTime();
    }

    /**
     * Getter of the longitude
     * @return Longitude where the plant is
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Setter of the longitude
     * @param longitude Longitude where the plant is
     */
    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    /**
     * Getter of the latitude
     * @return Latitude where the plant is
     */
    public double getLatitude() {
        return mLatitude;
    }
    /**
     * Setter of the latitude
     * @param latitude Latitude where the plant is
     */
    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    /**
     * Getter for the id
     * @return Firebase id of the plant
     */
    public String getId() {
        return mId;
    }

    /**
     * Setter of the plant id
     * @param id Firebase id of the plant
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Getter for the image id
     * @return Firebase id of the plant
     */
    public String getImageId() {
        return mImageId;
    }

    /**
     * Setter of the image id
     * @param id Firebase id of the plant
     */
    public void setImageId(String id) {
        this.mImageId = id;
    }

    /**
     * Id of the plant in Firebase
     */
    private String mId;
    /**
     * Name of the plant
     */
    private String mName;
    /**
     * Planting date of the plant. Stored as a timestamp
     */
    private long mPlantingDate;

    /**
     * Longitude of the plant
     */
    private double mLongitude;

    /**
     * latitude of the plant
     */
    private double mLatitude;

    /**
     * Associated image id
     */
    private String mImageId;

    /**
     * Default constructor
     */
    public Plant(){
    }

    /**
     * Plant constructor
     * @param name Name of the plant
     * @param plantingDate Planting date
     * @param location Location as a String under format "longitude, latitude"
     */
    public Plant(String name, Date plantingDate, String location){
        this.mName = name;
        this.mPlantingDate = plantingDate.getTime();
        String[] parts = location.split(", ");
        mLatitude = Double.parseDouble(parts[0]);
        mLongitude = Double.parseDouble(parts[1]);
    }

    /**
     * Plant constructor
     * @param name Name of the plant
     * @param plantingDate Planting date
     * @param location Location as a String under format "longitude, latitude"
     */
    public Plant(String id, String name, Date plantingDate, String location){
        this(name, plantingDate, location);
        this.mId = id;
    }
}
