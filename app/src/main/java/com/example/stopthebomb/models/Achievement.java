package com.example.stopthebomb.models;

public class Achievement {
    public int id;
    public String name;
    public String description;
    public String iconResource;
    public int points;
    public boolean unlocked;
    public String unlockedDate;

    public Achievement() {
        // Default constructor
    }

    @Override
    public String toString() {
        return name + (unlocked ? " (Unlocked)" : " (Locked)");
    }
}