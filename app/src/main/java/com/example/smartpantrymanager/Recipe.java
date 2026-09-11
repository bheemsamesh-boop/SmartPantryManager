package com.example.smartpantrymanager;

public class Recipe {

    private int id;
    private String name;
    private String instructions;

    public Recipe(int id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }
}