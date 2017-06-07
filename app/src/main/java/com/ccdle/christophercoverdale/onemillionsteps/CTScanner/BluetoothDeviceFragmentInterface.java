package com.ccdle.christophercoverdale.onemillionsteps.CTScanner;

/**
 * Created by USER on 5/8/2017.
 */

public interface BluetoothDeviceFragmentInterface {

    void initializeBluetoothAdapter();
    void enableBluetooth();
    void startScan();
    void stopScan();


    interface BluetoothDeviceFragmentInterfaceCallbacks {
        void dismissDialog();
    }
}
