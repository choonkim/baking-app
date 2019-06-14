package com.example.bakingrecipeapp.adapter;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class RecipeAdapt {
    String id;
    String name;
    ArrayList<IngredientAdapt> ingredients;
    ArrayList<DirectionsAdapt> steps;
    Integer servings;
    String image;

    public RecipeAdapt() {
        // constructor
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public ArrayList<IngredientAdapt> getIngredients() {
        return ingredients;
    }
    public ArrayList<DirectionsAdapt> getDirections() {
        return steps;
    }
    public Integer getServings() {
        return servings;
    }
    public String getImage() {
        return image;
    }
}
