package com.example.bakingrecipeapp.fragments;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bakingrecipeapp.Constants;
import com.example.bakingrecipeapp.R;
import com.example.bakingrecipeapp.adapter.DirectionsAdapt;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

public class DirectionsFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    // objects
    private static String TAG = DirectionsFragment.class.getSimpleName();
    ArrayList<DirectionsAdapt> mSteps = new ArrayList<>();
    String mVideoUrl = "";
    String mDescription = "";
    private SimpleExoPlayer mPlayer;
    private int mTotalSteps;
    private int mCurrentStep = 0;
    private long mVideoPlayingPosition;
    private boolean mPlayWhenReady = true;

    // views
    private MediaSessionCompat mMediaSession;
    private PlayerView mPlayerView;
    private TextView mDetailTextView;
    private ImageView mNoAvailableImageView;
    private TextView mNextButton;
    private TextView mBackButton;
    private TextView mCurrentTextView;
    private ImageView mRecipeStepDetailImageView;

    public DirectionsFragment() {
        // constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_step_detail, container, false);

        // Getting Step Values
        mVideoUrl = getArguments().getString(Constants.STEP_VIDEO_URL_KEY);
        mDescription = getArguments().getString(Constants.STEP_DESCRIPTION_KEY);
        mSteps = Parcels.unwrap(getArguments().getParcelable(Constants.STEP_LIST_KEY));
        mCurrentStep = getArguments().getInt(Constants.CURRENT_STEP_KEY);
        mTotalSteps = mSteps.size();

        // on rotation load from saveInstance
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.CURRENT_VIDEO_PLAY_POSITION_KEY)) {
            mVideoUrl = savedInstanceState.getString(Constants.STEP_VIDEO_URL_KEY);
            mDescription = savedInstanceState.getString(Constants.STEP_DESCRIPTION_KEY);
            mSteps = Parcels.unwrap(savedInstanceState.getParcelable(Constants.STEP_LIST_KEY));
            mCurrentStep = savedInstanceState.getInt(Constants.CURRENT_STEP_KEY);
            mVideoPlayingPosition = savedInstanceState.getLong(Constants.CURRENT_VIDEO_PLAY_POSITION_KEY);
            mPlayWhenReady = savedInstanceState.getBoolean(Constants.PLAY_WHEN_READY_KEY);
            Log.d(TAG, "onCreateView: savedInstanceState Called!!!! | mVideoPlayingPosition -> " + mVideoPlayingPosition + "mPlayWhenReady -> " + mPlayWhenReady);
        }

        // Find Views
        mPlayerView = rootView.findViewById(R.id.player_view);
        mDetailTextView = rootView.findViewById(R.id.item_detail);
        mNoAvailableImageView = rootView.findViewById(R.id.no_video_available_ImageView);
        mNextButton = (TextView) rootView.findViewById(R.id.next_button_textView);
        mBackButton = (TextView) rootView.findViewById(R.id.back_button_textView);
        mCurrentTextView = (TextView) rootView.findViewById(R.id.current_textView);
        mRecipeStepDetailImageView = (ImageView) rootView.findViewById(R.id.recipe_step_detail_imageView);


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStep < mTotalSteps - 1) {
                    if (mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                        mMediaSession.setActive(false);
                    }
                    // reset player status
                    mVideoPlayingPosition = 0;
                    mPlayWhenReady = true;

                    mCurrentStep = mCurrentStep + 1;
                    populateStepValues(
                            mSteps.get(mCurrentStep).getDescription(),
                            mSteps.get(mCurrentStep).getVideoURL(),
                            mSteps.get(mCurrentStep).getThumbnailURL(),
                            ("Step # " + mCurrentStep));
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStep > 0) {
                    // Stop Playing
                    if (mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                        mMediaSession.setActive(false);
                    }
                    // reset player status
                    mVideoPlayingPosition = 0;
                    mPlayWhenReady = true;

                    mCurrentStep = mCurrentStep - 1;
                    populateStepValues(
                            mSteps.get(mCurrentStep).getDescription(),
                            mSteps.get(mCurrentStep).getVideoURL(),
                            mSteps.get(mCurrentStep).getThumbnailURL(),
                            String.valueOf("Step # " + mCurrentStep));
                }
            }
        });

        if (!getResources().getBoolean(R.bool.isTablet) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Set Activity Screen to Immersive --> https://developer.android.com/training/system-ui/immersive.htm
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
            // Hide ActionBar
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            // Hide all views except mPlayerView, So it become a fullscreen video
            mNextButton.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mCurrentTextView.setVisibility(View.GONE);
            mDetailTextView.setVisibility(View.GONE);
            // Make mPlayerView Width and Height match_parent in size.
            mPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            populateStepValues(
                    mSteps.get(mCurrentStep).getDescription(),
                    mSteps.get(mCurrentStep).getVideoURL(),
                    mSteps.get(mCurrentStep).getThumbnailURL(),
                    String.valueOf("Step # " + mCurrentStep));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            populateStepValues(
                    mSteps.get(mCurrentStep).getDescription(),
                    mSteps.get(mCurrentStep).getVideoURL(),
                    mSteps.get(mCurrentStep).getThumbnailURL(),
                    String.valueOf("Step # " + mCurrentStep));
        }
    }

    private void populateStepValues(String description, String videoUrl, String thumbnailUrl, String currentStep) {

        if (videoUrl.equals("")) {
            mPlayerView.setVisibility(View.GONE);
            mNoAvailableImageView.setVisibility(View.VISIBLE);
        } else {
            mPlayerView.setVisibility(View.VISIBLE);
            mNoAvailableImageView.setVisibility(View.GONE);
            initializePlayer(videoUrl);
        }

        // description
        mDetailTextView.setText(description);

        // current step
        mCurrentTextView.setText(currentStep);

        // thumbnail
        try {
            if (!thumbnailUrl.equals("")) {
                mRecipeStepDetailImageView.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.ic_kitchen_black)
                        .error(R.drawable.ic_kitchen_black)
                        .into(mRecipeStepDetailImageView);
            }
        } catch (
                Exception e) {
            Log.d(TAG, "populateStepValues: " + e.toString());
        }
    }

    private void initializePlayer(String videoUrl) {
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        // Preparing PlaybackState for media buttons
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(stateBuilder.build());
        mMediaSession.setActive(true);

        if (mPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mPlayer);

            String userAgent = Util.getUserAgent(getActivity(), getContext().getString(R.string.app_name));
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), userAgent);
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(
                    Uri.parse(videoUrl),
                    dataSourceFactory,
                    extractorsFactory,
                    null,
                    null);

            mPlayer.prepare(mediaSource);

            // forward position
            if (mVideoPlayingPosition != 0) {
                mPlayer.seekTo(mVideoPlayingPosition);
                Log.d(TAG, "initializePlayer: mVideoPlayingPosition -> " + mVideoPlayingPosition);
            }
            // Stop or Play as user decided
            mPlayer.setPlayWhenReady(mPlayWhenReady);
            Log.d(TAG, "initializePlayer: mPlayWhenReady -> " + mPlayWhenReady);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlayWhenReady = mPlayer.getPlayWhenReady();
            mVideoPlayingPosition = mPlayer.getCurrentPosition();
        }
        outState.putString(Constants.STEP_VIDEO_URL_KEY, mVideoUrl);
        outState.getString(Constants.STEP_DESCRIPTION_KEY, mDescription);
        outState.putParcelable(Constants.STEP_LIST_KEY, Parcels.wrap(mSteps));
        outState.putInt(Constants.CURRENT_STEP_KEY, mCurrentStep);
        outState.putLong(Constants.CURRENT_VIDEO_PLAY_POSITION_KEY, mVideoPlayingPosition);
        outState.putBoolean(Constants.PLAY_WHEN_READY_KEY, mPlayWhenReady);
        Log.d(TAG, "onSaveInstanceState: Called" + mVideoPlayingPosition + " " + mPlayWhenReady);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayWhenReady = mPlayer.getPlayWhenReady();
            mVideoPlayingPosition = mPlayer.getCurrentPosition();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
