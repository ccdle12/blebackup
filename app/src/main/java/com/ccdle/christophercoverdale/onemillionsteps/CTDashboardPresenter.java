package com.ccdle.christophercoverdale.onemillionsteps;

import android.app.Activity;
import android.content.Context;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.CTInternetConnectionHelper;
import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;

/**
 * Created by USER on 5/6/2017.
 */

public class CTDashboardPresenter implements CTDashboardInterface, CTHealthKitInterface.CTHealthKitCallback,
        SingletonObserver.singletonObservable {


    private PackageModel packageModel;
    private Activity activity;
    private Context context;
    private CTHealthKitInterface CTHealthKitInterface;
    private CTDashboardCallback CTDashboardCallback;

    CTDashboardPresenter(PackageModel packageModel) {
        setPackageModel(packageModel);
        setActivity(packageModel);
        setContext(packageModel);
    }

    private void setPackageModel(PackageModel packageModel) { this.packageModel = packageModel; }
    private void setActivity(PackageModel packageModel) {
        this.activity = packageModel.getActivity();
    }
    private void setContext(PackageModel packageModel) {
        this.context  = packageModel.getContext();
    }

    /*CTDashboard Interface*/
    public void setCTDashboardCallback(CTDashboardCallback CTDashboardCallback) {
        this.CTDashboardCallback = CTDashboardCallback;
    }

    @Override
    public void initializeHealthKit() {
        if (!checkInternetConnection()) {
            this.CTDashboardCallback.showInternetConnectionError();
        } else {
            this.CTHealthKitInterface = new CTHealthKit(this.packageModel);
            this.CTHealthKitInterface.setCTHealthKitCallback(this);
        }
    }

    /*CTHealthKit Callbacks*/
    @Override
    public void healthKitIsConnected() {
        this.CTHealthKitInterface.getRequestForAllStepCounts();
        this.CTDashboardCallback.showListOfBluetoothDevices();
    }

    /*Internet Connection Helper*/
    private boolean checkInternetConnection() {
        return CTInternetConnectionHelper.isConncetedToInternet(this.context);
    }

    /*Singleton Observable*/
    @Override
    public void update() {
        CTHealthDataSingleton.getTotalStepCount();
    }
}
