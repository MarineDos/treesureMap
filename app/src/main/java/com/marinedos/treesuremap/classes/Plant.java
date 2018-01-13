package com.marinedos.treesuremap.classes;

import java.util.Date;

/**
 * Described a plant that belongs to a user with his location
 * Created by Marine on 13/01/2018.
 */

public class Plant {
    /**
     * Getter for plant name
     * @return Plant name
     */
    public String getName() {
        return mName;
    }

    /**
     * Setter for the plant name
     * @param name - Plant name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Getter for planting date
     * @return Planting date
     */
    public Date getPlantingDate() {
        return new Date(mPlantingDate);
    }

    /**
     * Setter for planting date
     * @param plantingDate - Planting date
     */
    public void setPlantingDate(Date plantingDate) {
        this.mPlantingDate = plantingDate.getTime();
    }

    /**
     * Name of the plant
     */
    private String mName;
    /**
     * Planting date of the plant. Stored as a timestamp
     */
    private long mPlantingDate;

    /**
     * Default constructor
     */
    public Plant(){
    }

    /**
     * Plant constructor
     * @param name - Name of the plant
     * @param plantingDate - Planting date
     */
    public Plant(String name, Date plantingDate){
        this.mName = name;
        this.mPlantingDate = plantingDate.getTime() / 1000;
    }
}
