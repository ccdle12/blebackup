package com.ccdle.christophercoverdale.onemillionsteps;

/**
 * Created by USER on 5/6/2017.
 */

public interface CTDashboardInterface {
    void setCTDashboardCallback(CTDashboardCallback CTDashboardCallback);
    void initializeHealthKit();

    interface CTDashboardCallback {
        void showInternetConnectionError();
        void showListOfBluetoothDevices();
    }
}
