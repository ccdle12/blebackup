package com.ccdle.christophercoverdale.onemillionsteps;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by USER on 5/6/2017.
 */

public class CTHealthKit implements CTHealthKitInterface {

    private Realm realmInstance;
    private CTHealthKitCallback CTHealthKitCallback;
    private CTBluetoothClientInterface CTBluetoothClientInterface;
    private int newPedometerStepCountReading;


    CTHealthKit() {
        this.CTBluetoothClientInterface = new CTBluetoothClient();
    }


    /*CTHealthKit Interface*/
    @Override
    public void initializeRealmInstance() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        this.realmInstance = Realm.getInstance(config);
    }

    @Override
    public void setCTHealthKitCallback(CTHealthKitCallback CTHealthKitCallback) {
        this.CTHealthKitCallback = CTHealthKitCallback;
    }

    @Override
    public void getPedometerStepCountFromBluetoothClient() {
        setNewPedometerStepCountReading(this.CTBluetoothClientInterface.getPedometerStepCount());
    }

    private void setNewPedometerStepCountReading(int stepCount) {
        this.newPedometerStepCountReading = stepCount;
        Log.d("CTHealthKit", "Retrieving pedometer step count: " + this.newPedometerStepCountReading);

        this.writeStepCountToHealthStore();
    }

    @Override
    public void writeStepCountToHealthStore() {
        this.initializeRealmInstance();

        CTHKStore CTHKStore = findExistingCTHKStoreObjectInRealm();

        String todaysDate = currentDate();


        this.realmInstance.beginTransaction();

        if (CTHKStore == null) {
            CTHKStore = this.realmInstance.createObject(CTHKStore.class, 0);
            CTHKStore.setStepCount(this.newPedometerStepCountReading);
            CTHKStore.setLastPedometerStepCountReading(this.newPedometerStepCountReading);
            CTHKStore.setLastUpdateTimeStamp(todaysDate);

        } else {

            int realDifferenceInPedometerStepCount = this.newPedometerStepCountReading - CTHKStore.getLastPedometerStepCountReading();

            Log.d("CTHealthKit", "Todays Date: " + todaysDate.toString());
            Log.d("CTHealthKit", "Last updated Date: " + CTHKStore.getLastUpdateTimeStamp().toString());
            Log.d("CTHealthKit", "Last pedometer step count reading: " + CTHKStore.getLastPedometerStepCountReading());
            Log.d("CTHealthKit", "new pedometer step : " + this.newPedometerStepCountReading);


            if (!CTHKStore.getLastUpdateTimeStamp().equals(todaysDate)) {
                int newTotalStepCount = CTHKStore.getStepCount() + this.newPedometerStepCountReading;

                CTHKStore.setStepCount(newTotalStepCount);
                CTHKStore.setLastPedometerStepCountReading(this.newPedometerStepCountReading);
                CTHKStore.setLastUpdateTimeStamp(todaysDate);
            }
            else if (CTHKStore.getLastUpdateTimeStamp().equals(todaysDate) && realDifferenceInPedometerStepCount > 0) {
                int newTotalStepCount = CTHKStore.getStepCount() + realDifferenceInPedometerStepCount;

                CTHKStore.setStepCount(newTotalStepCount);
                CTHKStore.setLastPedometerStepCountReading(this.newPedometerStepCountReading);

            } else {
                Log.d("CTHealthKit", "Already written this data today");
            }
        }

        this.realmInstance.commitTransaction();

        this.CTHealthKitCallback.sendTotalStepCount(this.readStepCountFromHKStore());
    }

    @Override
    public int readStepCountFromHKStore() {

        CTHKStore CTHKStore = findExistingCTHKStoreObjectInRealm();

        int stepCountFromHKStore = CTHKStore.getStepCount();

        Log.d("CTHealthKit", "Step Count Read: " + stepCountFromHKStore);
        return stepCountFromHKStore;
    }


    private CTHKStore findExistingCTHKStoreObjectInRealm(){
        CTHKStore CTHKStore = this.realmInstance.where(CTHKStore.class).equalTo("id", 0).findFirst();

        return CTHKStore;
    }

    private String currentDate() {
        Date currentDate = new Date();
        String stringCurrentDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);

        return stringCurrentDate;
    }

    private void deleteAllCTHKStoreObjects() {

        RealmResults<CTHKStore> results = this.realmInstance.where(CTHKStore.class).findAll();

        this.realmInstance.beginTransaction();
        results.deleteAllFromRealm();
        this.realmInstance.commitTransaction();

    }


}
