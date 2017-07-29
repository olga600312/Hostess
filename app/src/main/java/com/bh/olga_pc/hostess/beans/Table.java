package com.bh.olga_pc.hostess.beans;

/**
 * Created by Olga-PC on 7/9/2017.
 */

public class Table {
    private int id;
    private int region;
    private int maxGuests;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }
}
