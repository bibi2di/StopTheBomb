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

    public Achievement(int id, String name, String description, String iconResource, int points, boolean unlocked, String unlockedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResource = iconResource;
        this.points = points;
        this.unlocked = unlocked;
        this.unlockedDate = unlockedDate;
    }

    @Override
    public String toString() {
        return name + (unlocked ? " (Unlocked)" : " (Locked)");
    }
}