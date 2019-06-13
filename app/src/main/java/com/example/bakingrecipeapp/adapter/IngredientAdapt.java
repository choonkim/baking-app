package com.example.bakingrecipeapp.adapter;

import org.parceler.Parcel;

@Parcel
public class IngredientAdapt {
    String quantity;
    String measurement;
    String ingredient;

    public IngredientAdapt() {
        // constructor
    }

    public String getQuantity() {
        return quantity;
    }
    public String getMeasurement() {
        return measurement;
    }
    public String getIngredient() {
        return ingredient;
    }
}
