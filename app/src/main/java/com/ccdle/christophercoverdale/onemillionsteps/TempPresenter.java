package com.ccdle.christophercoverdale.onemillionsteps;

import android.util.Log;

/**
 * Created by USER on 5/8/2017.
 */

public class TempPresenter implements SingletonObserver.singletonObservable {
    @Override
    public void update() {
        CTHealthDataSingleton.getTotalStepCount();
        Log.e("Test", "Total: " + CTHealthDataSingleton.getTotalStepCount());
    }
}
