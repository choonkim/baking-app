package com.example.bakingrecipeapp.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakingrecipeapp.Constants;
import com.example.bakingrecipeapp.DeviceLayoutActivity;
import com.example.bakingrecipeapp.R;
import com.example.bakingrecipeapp.adapter.DirectionsAdapt;
import com.example.bakingrecipeapp.adapter.RecipeAdapt;
import com.example.bakingrecipeapp.fragments.DirectionsFragment;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectionsMod extends RecyclerView.Adapter<DirectionsMod.StepViewHolder> {

    private static final String TAG = DirectionsMod.class.getSimpleName();
    private RecipeAdapt mRecipe;
    private ArrayList<DirectionsAdapt> mDirectionsMods;
    private final DeviceLayoutActivity mParentActivity;
    private final boolean mTwoPane;

    public DirectionsMod(DeviceLayoutActivity parent, RecipeAdapt recipe, boolean twoPane) {
        mRecipe = recipe;
        mDirectionsMods = mRecipe.getDirections();
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_step, parent, false);
        StepViewHolder viewHolder = new StepViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDirectionsMods.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.content)
        TextView mStepTitleTextView;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            String stepNumber = mDirectionsMods.get(position).getId();
            String stepShortDescription = mDirectionsMods.get(position).getShortDescription();
            mStepTitleTextView.setText(stepNumber + ". " + stepShortDescription);
        }

        @Override
        public void onClick(View v) {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(DirectionsFragment.ARG_ITEM_ID, mDirectionsMods.get(getAdapterPosition()).getId());
                DirectionsFragment fragment = new DirectionsFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
                // tablet
                arguments.putString(Constants.STEP_VIDEO_URL_KEY, mRecipe.getDirections().get(getAdapterPosition()).getVideoURL());
                arguments.putString(Constants.STEP_DESCRIPTION_KEY, mRecipe.getDirections().get(getAdapterPosition()).getDescription());
                arguments.putParcelable(Constants.STEP_LIST_KEY, Parcels.wrap(mRecipe.getDirections()));
                arguments.putInt(Constants.CURRENT_STEP_KEY, getAdapterPosition());
            } else {
                // mobile
                Context context = v.getContext();
                Intent intent = new Intent(context, DirectionsFragment.class);
                intent.putExtra(Constants.STEP_VIDEO_URL_KEY, mRecipe.getDirections().get(getAdapterPosition()).getVideoURL());
                intent.putExtra(Constants.STEP_DESCRIPTION_KEY, mRecipe.getDirections().get(getAdapterPosition()).getDescription());
                intent.putExtra(Constants.STEP_LIST_KEY, Parcels.wrap(mRecipe.getDirections()));
                intent.putExtra(Constants.CURRENT_STEP_KEY, getAdapterPosition());
                context.startActivity(intent);
            }
        }
    }
}
