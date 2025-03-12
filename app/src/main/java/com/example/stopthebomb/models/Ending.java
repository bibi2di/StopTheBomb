package com.example.stopthebomb.models;

public class Ending {
    public int id;
    public String name;
    public String description;
    public String imageResource;
    public boolean discovered;
    public String discoveredDate;

    public Ending() {
        // Default constructor
    }

    public Ending(int id, String name, String description, String imageResource, boolean discovered, String discoveredDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageResource = imageResource;
        this.discovered = discovered;
        this.discoveredDate = discoveredDate;
    }

    @Override
    public String toString() {
        return name + (discovered ? " (Discovered)" : " (????)");
    }
}