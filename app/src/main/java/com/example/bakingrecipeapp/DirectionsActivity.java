package com.example.bakingrecipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.bakingrecipeapp.adapter.DirectionsAdapt;
import com.example.bakingrecipeapp.fragments.DirectionsFragment;

import org.parceler.Parcels;

import java.util.ArrayList;


public class DirectionsActivity extends AppCompatActivity {

    private static String TAG = DirectionsActivity.class.getSimpleName();
    private String mVideoUrl = "";
    private String mDescription = "";
    private int mCurrentStep = 0;
    ArrayList<DirectionsAdapt> mSteps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(Constants.STEP_VIDEO_URL_KEY)) {
            mVideoUrl = getIntent().getStringExtra(Constants.STEP_VIDEO_URL_KEY);
            mDescription = getIntent().getStringExtra(Constants.STEP_DESCRIPTION_KEY);
            mSteps = Parcels.unwrap(getIntent().getParcelableExtra(Constants.STEP_LIST_KEY));
            mCurrentStep = getIntent().getIntExtra(Constants.CURRENT_STEP_KEY, 0);

            Log.d(TAG, "onCreate: mVideoURL: " + mVideoUrl);
            Log.d(TAG, "onCreate: mDescription: " + mDescription);
        }

        // expose up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(Constants.STEP_VIDEO_URL_KEY, mVideoUrl);
            arguments.putString(Constants.STEP_DESCRIPTION_KEY, mDescription);
            arguments.putParcelable(Constants.STEP_LIST_KEY, Parcels.wrap(mSteps));
            arguments.putInt(Constants.CURRENT_STEP_KEY, mCurrentStep);

            arguments.putString(DirectionsFragment.ARG_ITEM_ID, getIntent().getStringExtra(DirectionsFragment.ARG_ITEM_ID));
            DirectionsFragment fragment = new DirectionsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
        }
    }

    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, DirectionsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
