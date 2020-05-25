package com.biotag.dogtagreading;

import com.biotag.commons.BaseApplication;

//import io.objectbox.BoxStore;

public class DogTagReadingApplication extends BaseApplication {
    protected static DogTagReadingApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
