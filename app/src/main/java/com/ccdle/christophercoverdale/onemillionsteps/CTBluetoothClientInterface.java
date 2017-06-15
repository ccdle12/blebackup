package com.ccdle.christophercoverdale.onemillionsteps;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by USER on 5/25/2017.
 */

public interface CTBluetoothClientInterface {
    void sendStepCountCharacteristic(BluetoothGattCharacteristic stepCount);
    int getPedometerStepCount();
}
