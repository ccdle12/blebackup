package com.ccdle.christophercoverdale.onemillionsteps;

/**
 * Created by USER on 5/6/2017.
 */

public interface CTHealthKitInterface {
    void setCTHealthKitCallback(CTHealthKitCallback CTHealthKitCallback);
    void initializeRealmInstance();
    void getPedometerStepCountFromBluetoothClient();
    void writeStepCountToHealthStore();
    int readStepCountFromHKStore();

    interface CTHealthKitCallback {
        void sendTotalStepCount(int stepCount);
    }
}
