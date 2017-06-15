package com.ccdle.christophercoverdale.onemillionsteps;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by USER on 5/25/2017.
 */

public class CTHKStore extends RealmObject {

    @PrimaryKey
    private int id;
    private int stepCount;
    private int lastPedometerStepCountReading;
    private String lastUpdateTimeStamp;


    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
    public int getStepCount() {
        return stepCount;
    }


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }


    public void setLastPedometerStepCountReading(int lastPedometerStepCountReading) {
        this.lastPedometerStepCountReading = lastPedometerStepCountReading;
    }
    public int getLastPedometerStepCountReading() {
        return lastPedometerStepCountReading;
    }


    public void setLastUpdateTimeStamp(String lastUpdateTimeStamp) {
        this.lastUpdateTimeStamp = lastUpdateTimeStamp;
    }
    public String getLastUpdateTimeStamp() {
        return lastUpdateTimeStamp;
    }
}
