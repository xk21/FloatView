package com.example.chenmingying.floatview.bean;

import android.os.Bundle;

public class GeekEvent {
    private int mType;
    private Bundle mBundle;

    public GeekEvent(int type, Bundle bundle) {
        mType = type;
        mBundle = bundle;
    }

    public GeekEvent(int type) {
        mType = type;
        mBundle = null;
    }

    public int getType() {
        return mType;
    }
    public Bundle getBundle() {
        return mBundle;
    }

}
