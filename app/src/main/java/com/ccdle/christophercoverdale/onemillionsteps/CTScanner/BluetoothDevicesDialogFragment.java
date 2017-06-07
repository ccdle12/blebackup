package com.ccdle.christophercoverdale.onemillionsteps.CTScanner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;

/**
 * Created by USER on 5/8/2017.
 */

public class BluetoothDevicesDialogFragment extends DialogFragment implements BluetoothDeviceFragmentInterface.BluetoothDeviceFragmentInterfaceCallbacks {

    private PackageModel packageModel;
    private Dialog dialog;
    BluetoothDeviceFragmentInterface bleDeviceFragmentInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.dialog = super.onCreateDialog(savedInstanceState);
        this.dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        this.packageModel = new PackageModel(getContext(), getActivity());
        this.bleDeviceFragmentInterface = new BluetoothDevicePresenter(this.packageModel, this);
        this.bleDeviceFragmentInterface.initializeBluetoothAdapter();

        return this.dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*Setting the size of the Dialog*/
        this.dialog = getDialog();
        if (this.dialog != null) {
            this.dialog.getWindow().setLayout(900, 900);
        }

        this.bleDeviceFragmentInterface.enableBluetooth();
        this.bleDeviceFragmentInterface.startScan();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onStop() {
        super.onStop();
        this.bleDeviceFragmentInterface.stopScan();
    }


    @Override
    public void dismissDialog() {
        this.dialog.dismiss();
    }
}


