package com.ccdle.christophercoverdale.onemillionsteps;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by USER on 5/6/2017.
 */

public class CTHealthKit implements CTHealthKitInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String DEBUG_TAG = "CTHealthKit";
    private static final int REQUEST_OATH = 1;
    private boolean authInProgress = false;

    private PackageModel packageModel;
    private Activity activity;
    private Context context;

    private GoogleApiClient healthStoreClient;
    private CTHealthKitCallback CTHealthKitCallback;

    private int stepCountFromHealthStore;

    CTHealthKit(PackageModel packageModel) {
        setPackageModel(packageModel);
        setContext();
        setActivity();
        buildHealthStoreClient();
        connectToHealthStore();
    }

    private void setPackageModel(PackageModel packageModel) {
        this.packageModel = packageModel;
    }
    private void setActivity() {
        this.activity = this.packageModel.getActivity();
    }
    private void setContext() {
        this.context = this.packageModel.getContext();
    }
    private void buildHealthStoreClient() {
        this.healthStoreClient = new GoogleApiClient.Builder(this.context)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    private void connectToHealthStore() {
        this.healthStoreClient.connect();
    }


    /*CTHealthKit Interface*/
    @Override
    public void setCTHealthKitCallback(CTHealthKitCallback CTHealthKitCallback) {
        this.CTHealthKitCallback = CTHealthKitCallback;
    }

    @Override
    public void getRequestForAllStepCounts() {
        new GetRequestTotalStepCount()
                .execute();
    }


    /*Health Store On Connected Callbacks*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        this.CTHealthKitCallback.healthKitIsConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(DEBUG_TAG, "Health Store connection suspended");
    }

    /*Health Store on Connection Failed Callback*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(DEBUG_TAG, "Health Store connection failed");

        if (!authInProgress) {
            authInProgress = true;
            try {
                connectionResult.startResolutionForResult(this.activity, REQUEST_OATH);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("GoogleFit", "authInProgress");
        }
    }


    /*Async Classes - retrieves the 1 year step count*/
    private class GetRequestTotalStepCount extends AsyncTask<Void, Void, Void> {
        DataReadResult dataReadResult;

        protected Void doInBackground(Void... params) {
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.YEAR, -1);
            long startTime = cal.getTimeInMillis();

            java.text.DateFormat dateFormat = DateFormat.getDateInstance();
            Log.e("History", "Range Start: " + dateFormat.format(startTime));
            Log.e("History", "Range End: " + dateFormat.format(endTime));

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            dataReadResult = Fitness.HistoryApi.readData(healthStoreClient, readRequest).await(1, TimeUnit.MINUTES);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Used for aggregated data
            if (dataReadResult.getBuckets().size() > 0) {
                Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        retrieveTotalStepCountHistory(dataSet);
                    }
                }
            }
            //Used for non-aggregated data
            else if (dataReadResult.getDataSets().size() > 0) {
                Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    retrieveTotalStepCountHistory(dataSet);
                }
            }

            /*Cache Health Kit Step Count*/
            CTHealthDataSingleton.cacheHealthStoreStepCount(stepCountFromHealthStore);
        }

        private void retrieveTotalStepCountHistory(DataSet dataSet) {
            DateFormat dateFormat = DateFormat.getDateInstance();
            DateFormat timeFormat = DateFormat.getTimeInstance();


            for (DataPoint dp : dataSet.getDataPoints()) {
                Log.e("History", "Data point:");

                Log.e("History", "\tType: " + dp.getDataType().getName());
                Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                for (Field field : dp.getDataType().getFields()) {
                    Log.e("History", "\tField: " + field.getName() +
                            " Value: " + dp.getValue(field));
                    stepCountFromHealthStore += Integer.valueOf(String.valueOf(dp.getValue(field)));

                }
            }
            Log.e("History", "Step Count from HealthStore: " + stepCountFromHealthStore);
        }
    }
}
