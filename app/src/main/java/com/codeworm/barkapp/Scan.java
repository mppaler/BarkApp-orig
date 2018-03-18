package com.codeworm.barkapp;

/**
 * Created by Mariah on 25/02/2018.
 */

public class Scan {

    public String User;
    public int maxSlots;
    public int occupiedSlots;

    public Scan(String user, int maxSlots, int occupiedSlots) {

        User = user;
        this.maxSlots = maxSlots;
        this.occupiedSlots = occupiedSlots;
    }




    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public int getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(int occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }
}
