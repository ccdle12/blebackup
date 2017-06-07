package com.ccdle.christophercoverdale.onemillionsteps;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.CTBluetoothDataHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created by USER on 5/8/2017.
 */

public class CTHealthDataSingleton implements SingletonObserver {

    private static CTHealthDataSingleton singleton = null;
    private static int deviceStepCount;
    private static int healthStoreStepCount;
    SingletonObserver.singletonObservable tempPresenter;


    private CTHealthDataSingleton() {}

    public static CTHealthDataSingleton getInstance() {
        if (singleton == null)   { return singleton = new CTHealthDataSingleton(); }
        else                     { return singleton; }
    }

    public static void cacheDeviceStepCount(BluetoothGattCharacteristic characteristic) throws UnsupportedEncodingException {
        deviceStepCount = CTBluetoothDataHelper.getStepCountAsInt(characteristic);
        Log.e("Singleton", "Steps from device in singleton: " + deviceStepCount);
    }

    public static void cacheHealthStoreStepCount(int stepCount) {
        healthStoreStepCount = stepCount;
        Log.e("Singleton", "Steps from HK in singleton: " + healthStoreStepCount);
    }

    public static int getTotalStepCount() {
        return deviceStepCount + healthStoreStepCount;
    }

    @Override
    public void notifyObservable() {
        this.tempPresenter = new TempPresenter();
        tempPresenter.update();
    }
}
