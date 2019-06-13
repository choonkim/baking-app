package com.example.bakingrecipeapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakingrecipeapp.adapter.RecipeAdapt;
import com.example.bakingrecipeapp.model.DirectionsMod;
import com.example.bakingrecipeapp.widget.BakingWidget;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

public class DeviceLayoutActivity extends AppCompatActivity {

    @BindView(R.id.ingredient_textView)
    public TextView mIngredientTextView;
    @BindView(R.id.item_list)
    public RecyclerView mStepsRecyclerView;
    @BindView(R.id.add_widget_button_imageView)
    public ImageView mAddWidgetButton;

    private boolean mTwoPane; // is tablet or phone layout
    private static RecipeAdapt mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        // recipe array from intent
        if (getIntent().hasExtra(Constants.RECIPE_ARRAY_KEY)) {
            mRecipe = Parcels.unwrap(getIntent().getParcelableExtra(Constants.RECIPE_ARRAY_KEY));
            // Set Toolbar Title
            getSupportActionBar().setTitle(mRecipe.getName());
        }

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        mAddWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWidget();
            }
        });

        // ingredients
        setupIngredientTextView();
        // Populate Steps Values
        setupRecyclerView();
    }

    private void addWidget() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            result.append("- ");
            result.append(mRecipe.getIngredients().get(i).getIngredient());
            result.append(" (");
            result.append(mRecipe.getIngredients().get(i).getQuantity());
            result.append(" ");
            result.append(mRecipe.getIngredients().get(i).getMeasurement());
            result.append(").");
            if (i != (mRecipe.getIngredients().size() - 1)) {
                result.append("\n");
            }
        }

        // widget state
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        if (appWidgetIds.length == 0) {
            Toast.makeText(this, "Please make a home screen widget first!", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < appWidgetIds.length; i++) {
                BakingWidget.updateAppWidget(this, appWidgetManager, appWidgetIds[i], mRecipe.getName() + " - Ingredients", result.toString());
                Toast.makeText(this, "Widget Added!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupIngredientTextView() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            result.append("- ");
            result.append(mRecipe.getIngredients().get(i).getIngredient());
            result.append(" (");
            result.append(mRecipe.getIngredients().get(i).getQuantity());
            result.append(" ");
            result.append(mRecipe.getIngredients().get(i).getMeasurement());
            result.append(").");
            if (i != (mRecipe.getIngredients().size() - 1)) {
                result.append("\n");
            }
        }
        // display
        mIngredientTextView.setText(result.toString());
    }

    private void setupRecyclerView() {
        DirectionsMod directionsMod = new DirectionsMod(this, mRecipe, mTwoPane);
        mStepsRecyclerView.setAdapter(directionsMod);
        mStepsRecyclerView.setFocusable(false);
    }

    // data into saveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.RECIPE_ARRAY_KEY, Parcels.wrap(mRecipe));
    }

    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
