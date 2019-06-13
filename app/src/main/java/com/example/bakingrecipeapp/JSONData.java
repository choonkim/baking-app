package com.example.bakingrecipeapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.bakingrecipeapp.adapter.DirectionsAdapt;
import com.example.bakingrecipeapp.adapter.IngredientAdapt;
import com.example.bakingrecipeapp.adapter.RecipeAdapt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONData {

    private static final String TAG = JSONData.class.getSimpleName();
    final static ArrayList<RecipeAdapt> mRecipes = new ArrayList<>();

    interface Callback {
        void onDone(ArrayList<RecipeAdapt> recipes);
    }

    static void requestDataByVolley(Context context, final Callback callback) {


        String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject;
                mRecipes.clear();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        jsonObject = response.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        RecipeAdapt recipe = gson.fromJson(jsonObject.toString(), RecipeAdapt.class);
                        mRecipes.add(recipe);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "-----------------------------------");
                for (int i = 0; i < mRecipes.size(); i++) {
                    RecipeAdapt currentRecipe = mRecipes.get(i);
                    Log.d(TAG, "* Recipe Name: " + currentRecipe.getId());
                    Log.d(TAG, "* Recipe Servings: " + currentRecipe.getServings());
                    Log.d(TAG, "* Recipe Image: " + currentRecipe.getImage());
                    Log.d(TAG, "* Recipe Ingredients: ");

                    for (int j = 0; j < currentRecipe.getIngredients().size(); j++) {
                        IngredientAdapt currentIngredient = currentRecipe.getIngredients().get(j);
                        String quantity = currentIngredient.getQuantity();
                        String measure = currentIngredient.getMeasurement();
                        String ingredient = currentIngredient.getIngredient();
                        Log.d(TAG, "  - " + ingredient + " (" + quantity + " " + measure + ").");
                    }

                    Log.d(TAG, "* Recipe Steps: ");
                    for (int j = 0; j < currentRecipe.getDirections().size(); j++) {
                        DirectionsAdapt currentStep = currentRecipe.getDirections().get(j);
                        String id = currentStep.getId();
                        String shortDescription = currentStep.getShortDescription();
                        String description = currentStep.getDescription();
                        String videoURL = currentStep.getVideoURL();
                        String thumbnailURL = currentStep.getThumbnailURL();
                        Log.d(TAG, "  - " + id + ". " + shortDescription);
                        Log.d(TAG, "    - Description: " + description);
                        Log.d(TAG, "    - VideoURL: " + videoURL);
                        Log.d(TAG, "    - ThumbnailURL: " + thumbnailURL);
                    }
                    Log.d(TAG, "-----------------------------------");
                }
                callback.onDone(mRecipes);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        // request object
        requestQueue.add(jsonArrayRequest);
    }
}
