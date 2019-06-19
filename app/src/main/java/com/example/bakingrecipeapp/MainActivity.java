package com.example.bakingrecipeapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.bakingrecipeapp.adapter.RecipeAdapt;
import com.example.bakingrecipeapp.model.RecipeMod;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements JSONData.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static ArrayList<RecipeAdapt> mRecipes = new ArrayList<>();
    private RecipeMod mRecipeAdapter;

    // Views
    @BindView(R.id.recipeRecyclerView)
    public RecyclerView mRecipeRecyclerView;
    @BindString(R.string.app_name)
    public String mAppName;

    // espresso test
    @Nullable
    public IdlingResourceForTest mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResourceForTest getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new IdlingResourceForTest();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        JSONData.requestDataByVolley(this, MainActivity.this, mIdlingResource);
    }

    @Override
    public void onDone(ArrayList<RecipeAdapt> recipes) {
        mRecipes = recipes;
        Log.d(TAG, "onDone: Triggered! " + mRecipes.size());
        setRecyclerView();
        setAdapter();
    }

    private void setRecyclerView() {
        // Determined if Table or Phone by using is_tablet.xml (one for normal, one for screen above w900dp) in values directory.
        if (getResources().getBoolean(R.bool.isTablet)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mRecipeRecyclerView.setLayoutManager(gridLayoutManager);
            Log.d(TAG, "onCreate: It is a Tablet");
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecipeRecyclerView.setLayoutManager(linearLayoutManager);
            Log.d(TAG, "onCreate: It is a Phone");
        }
        mRecipeRecyclerView.setHasFixedSize(true);
    }

    private void setAdapter() {
        mRecipeAdapter = new RecipeMod(mRecipes, this);
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_settings) {
            JSONData.requestDataByVolley(this, MainActivity.this, mIdlingResource);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
