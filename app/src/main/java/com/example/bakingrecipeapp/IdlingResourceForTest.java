package com.example.bakingrecipeapp;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class IdlingResourceForTest implements IdlingResource {

    @Nullable
    private volatile ResourceCallback mResourceCallback;

    private AtomicBoolean mAtomicBoolean = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mAtomicBoolean.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;

    }

    public void setIdleState(boolean isIdleNow) {
        mAtomicBoolean.set(isIdleNow);
        if (isIdleNow && mResourceCallback != null) {
            mResourceCallback.onTransitionToIdle();
        }
    }

}
