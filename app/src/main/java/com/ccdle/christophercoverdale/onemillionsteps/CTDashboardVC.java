package com.ccdle.christophercoverdale.onemillionsteps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;
import com.ccdle.christophercoverdale.onemillionsteps.CTScanner.BluetoothDevicesDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class CTDashboardVC extends Fragment implements CTDashboardInterface.CTDashboardCallback {

    private RelativeLayout rootLayout;
    private ViewGroup.LayoutParams rootLayoutParams;
    private CTDashboardInterface CTDashboardInterface;

    public CTDashboardVC() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initializeRootLayout();
        initializeCTDashboardInterface();

        return this.rootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.CTDashboardInterface.initializeHealthKit();
    }

    private void initializeRootLayout() {
        this.rootLayout = new RelativeLayout(getActivity());
        this.rootLayout.setId(R.id.mainVC_rootLayout);
        this.rootLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.rootLayout.setLayoutParams(rootLayoutParams);
    }
    private void initializeCTDashboardInterface() {
        PackageModel packageModel = new PackageModel(getContext(), getActivity());
        this.CTDashboardInterface = new CTDashboardPresenter(packageModel);
        this.CTDashboardInterface.setCTDashboardCallback(this);
    }

    /*CTDashboard Callback*/
    @Override
    public void showInternetConnectionError() {
        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showListOfBluetoothDevices() {
        BluetoothDevicesDialogFragment bluetoothDevicesDialogFragment = new BluetoothDevicesDialogFragment();
        bluetoothDevicesDialogFragment.show(getFragmentManager(), "tag");
    }

}

