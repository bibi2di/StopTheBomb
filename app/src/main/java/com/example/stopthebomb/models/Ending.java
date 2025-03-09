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

    @Override
    public String toString() {
        return name + (discovered ? " (Discovered)" : " (????)");
    }
}