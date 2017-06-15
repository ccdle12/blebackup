package com.ccdle.christophercoverdale.onemillionsteps;

import android.bluetooth.BluetoothDevice;
import android.support.v4.app.DialogFragment;

/**
 * Created by USER on 5/19/2017.
 */

public interface CTDashboardInterface {
    void setCTDashboardCallback(CTDashboardCallback CTDashboardCallback);
    void initializeRealm();
    void initializeHealthKit();
    void bindServiceToBluetoothDevice();
    void unbindServiceToBluetoothDevice();
    void launchScanForBluetoothDevices();
    void sendCommandsToTheDevice();

    interface CTDashboardCallback {
        void onDeviceSelectedCallback(BluetoothDevice device, String name);
        void displayScanForBluetoothDevices(DialogFragment dialogFragment);
        void displayStepCount(int stepCount);
    }
}
