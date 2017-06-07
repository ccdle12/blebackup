package com.ccdle.christophercoverdale.onemillionsteps;

/**
 * Created by USER on 5/6/2017.
 */

public interface CTHealthKitInterface {
    void setCTHealthKitCallback(CTHealthKitCallback CTHealthKitCallback);
    void getRequestForAllStepCounts();

    interface CTHealthKitCallback {
        void healthKitIsConnected();
    }
}
