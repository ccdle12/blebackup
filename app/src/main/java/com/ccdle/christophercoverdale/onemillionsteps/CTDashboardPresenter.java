package com.ccdle.christophercoverdale.onemillionsteps;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;
import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.ScannerFragment;
import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.UARTInterface;
import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.UARTService;

import io.realm.Realm;

/**
 * Created by USER on 5/19/2017.
 */

public class CTDashboardPresenter implements CTDashboardInterface, CTHealthKitInterface.CTHealthKitCallback, ScannerFragment.OnDeviceSelectedListener, UARTInterface.UARTInterfaceCallback {


    private PackageModel packageModel;
    private CTDashboardInterface.CTDashboardCallback CTDashboardCallback;
    private CTHealthKitInterface CTHealthKitInterface;
    private UARTInterface uartInterface;
    private DialogFragment dialogScanForBluetoothDevices;


    public CTDashboardPresenter(PackageModel packageModel) {
        setPackageModel(packageModel);
    }

    private void setPackageModel(PackageModel packageModel) {
        this.packageModel = packageModel;
    }


    /* CT Dashboard Interface*/
    @Override
    public void setCTDashboardCallback(CTDashboardInterface.CTDashboardCallback CTDashboardCallback) {
        this.CTDashboardCallback = CTDashboardCallback;
    }

    @Override
    public void initializeRealm() {
        Realm.init(this.packageModel.getContext());
    }

    @Override
    public void initializeHealthKit() {
        this.CTHealthKitInterface = new CTHealthKit();
        this.CTHealthKitInterface.setCTHealthKitCallback(this);
    }

    @Override
    public void bindServiceToBluetoothDevice() {
        this.bindServiceToBle();
    }

    @Override
    public void unbindServiceToBluetoothDevice() {
        this.unbindServiceToBle();
    }

    @Override
    public void launchScanForBluetoothDevices() {
        /* CT Blackbox 2 */
            dialogScanForBluetoothDevices = ScannerFragment.getInstance(this);
            this.CTDashboardCallback.displayScanForBluetoothDevices(this.dialogScanForBluetoothDevices);
    }

    @Override
    public void sendCommandsToTheDevice() {
        if (uartInterface != null) {
            this.uartInterface.send();
        }
    }


    /* Scanner Fragment Callback */
    @Override
    public void onDeviceSelected(BluetoothDevice device, String name) {
        this.CTDashboardCallback.onDeviceSelectedCallback(device, name);
    }

    @Override
    public void onDialogCanceled() {

    }


    /* CT Blackbox 10 */
    private ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            final UARTService.UARTBinder bleService = (UARTService.UARTBinder) service;
            uartInterface = bleService;
            setUARTInterfaceCallback();
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            uartInterface = null;
        }
    };

    public void bindServiceToBle() {
        final Intent service = new Intent(this.packageModel.getActivity(), UARTService.class);
        this.packageModel.getActivity().bindService(service, bleServiceConnection, 0); // we pass 0 as a flag so the service will not be created if not exists
    }

    public void unbindServiceToBle() {
        try {
            this.packageModel.getActivity().unbindService(bleServiceConnection);
            uartInterface = null;
        } catch (final IllegalArgumentException e) {
            // do nothing, we were not connected to the sensor
        }
    }

    private void setUARTInterfaceCallback() {
        this.uartInterface.setUARTInterfaceCallback(this);
    }


    /* Callback from UARTService*/
    @Override
    public void characteristicReceivedFromDevice() {
        this.CTHealthKitInterface.getPedometerStepCountFromBluetoothClient();
    }

    /* Callback from CTHealthKit Interface*/
    @Override
    public void sendTotalStepCount(int stepCount) {
        this.CTDashboardCallback.displayStepCount(stepCount);
        Log.e("CT Dashboard Presenter", "StepCount from HealthKit: " + stepCount);
    }
}
