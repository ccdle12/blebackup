package com.ccdle.christophercoverdale.onemillionsteps;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.CTBluetoothDataHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created by USER on 5/25/2017.
 */

public class CTBluetoothClient implements CTBluetoothClientInterface {

    private static int pedometerStepCount;

    /* CTBluetooth Client Interface*/
    @Override
    public void sendStepCountCharacteristic(BluetoothGattCharacteristic stepCount) {

        try {
            setPedometerStepCount(stepCount);
            Log.e("CTBluetoothClient", "Step Count from UARTManager received: " + pedometerStepCount);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getPedometerStepCount() {
        Log.e("BluetoothClient", "Returning stepcount to Healthkit: " + pedometerStepCount);
        return pedometerStepCount;
    }

    private void setPedometerStepCount(BluetoothGattCharacteristic stepCount) throws UnsupportedEncodingException {
        this.pedometerStepCount = CTBluetoothDataHelper.getStepCountAsInt(stepCount);
    }
}
