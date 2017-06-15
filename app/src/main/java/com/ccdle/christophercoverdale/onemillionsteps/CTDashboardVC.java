package com.ccdle.christophercoverdale.onemillionsteps;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;
import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.BleProfileService;
import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.BleProfileServiceReadyActivity;
import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.UARTService;

import java.util.UUID;

public class CTDashboardVC extends BleProfileServiceReadyActivity<UARTService.UARTBinder> implements com.ccdle.christophercoverdale.onemillionsteps.CTDashboardInterface.CTDashboardCallback {

	private PackageModel packageModel;
	CTDashboardInterface CTDashboardInterface;

	private RelativeLayout rootLayout;
	private ViewGroup.LayoutParams rootLayoutParams;

	private TextView stepCountView;
	private Button launchScanForBluetoothDevicesButton;
	private Button sendCommandsButton;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		setUpVC();

		this.CTDashboardInterface.initializeRealm();
		this.CTDashboardInterface.initializeHealthKit();
		this.CTDashboardInterface.bindServiceToBluetoothDevice();

		/* CT Blackbox 1 */
		this.CTDashboardInterface.launchScanForBluetoothDevices();

		return this.rootLayout;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.CTDashboardInterface.sendCommandsToTheDevice();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.CTDashboardInterface.unbindServiceToBluetoothDevice();
	}


	/* CTDashboardPresenter Callbacks */
	@Override
	public void onDeviceSelectedCallback(BluetoothDevice device, String name) {
		this.onDeviceSelected(device, name);
	}

	@Override
	public void displayScanForBluetoothDevices(DialogFragment dialogFragment) {
		dialogFragment.show(getActivity().getSupportFragmentManager(), "scan_fragment");
	}

	@Override
	public void displayStepCount(int stepCount) {
		Log.e("CTDashboardVC", "Received new stepcount");
		this.updateStepCountView(stepCount);
	}

	private void updateStepCountView(final int stepCount) {
		this.rootLayout.post(new Runnable() {
			@Override
			public void run() {
				stepCountView.setText("Step Count: " + stepCount);
			}
		});
	}


	/* Cotham Technologies - Programmatic Layout */
	private void setUpVC() {
		initializePackageModel();
		initializeRootLayout();
		initializeStepCountView();
		initializeButtonToScanForBluetoothDevices();
		initializeButtonToSendCommands();
		addAllViewsToRootLayout();
		initializeUARTControlFragmentInterface();
		setListenerOnLaunchScanForBluetoothDevices();
		setListenerOnButtonToSendCommands();
	}

	private void initializePackageModel() {
		this.packageModel = new PackageModel(getContext(), getActivity());
	}

	private void initializeRootLayout() {
		this.rootLayout = new RelativeLayout(getActivity());
		this.rootLayout.setId(R.id.mainVC_rootLayout);
		this.rootLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.rootLayout.setLayoutParams(rootLayoutParams);
	}

	private void initializeStepCountView() {
		this.stepCountView = new TextView(getContext());
		this.stepCountView.setId(R.id.step_count_view);
		this.stepCountView.setText("Step Count: ");
		this.stepCountView.setTextSize(25);
		RelativeLayout.LayoutParams stepCountViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		stepCountViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.stepCountView.setLayoutParams(stepCountViewLayoutParams);

	}

	private void initializeButtonToScanForBluetoothDevices() {
		this.launchScanForBluetoothDevicesButton = new Button(getContext());
		this.launchScanForBluetoothDevicesButton.setId(R.id.launch_scan_for_bluetooth_devices);
		RelativeLayout.LayoutParams launchScanForBluetoothDevicesLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		launchScanForBluetoothDevicesLayoutParams.addRule(RelativeLayout.BELOW, R.id.step_count_view);
		launchScanForBluetoothDevicesLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.launchScanForBluetoothDevicesButton.setText("Scan for Bluetooth Devices");
		this.launchScanForBluetoothDevicesButton.setLayoutParams(launchScanForBluetoothDevicesLayoutParams);

	}

	private void initializeButtonToSendCommands() {
		this.sendCommandsButton = new Button(getContext());
		this.sendCommandsButton.setId(R.id.send_commands);
		RelativeLayout.LayoutParams sendCommandsLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		sendCommandsLayoutParams.addRule(RelativeLayout.BELOW, R.id.launch_scan_for_bluetooth_devices);
		sendCommandsLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.sendCommandsButton.setText("Send Commands");
		this.sendCommandsButton.setLayoutParams(sendCommandsLayoutParams);

	}

	private void addAllViewsToRootLayout() {
		this.rootLayout.addView(this.stepCountView);
		//this.rootLayout.addView(this.launchScanForBluetoothDevicesButton);
		//this.rootLayout.addView(this.sendCommandsButton);
	}

	private void setListenerOnLaunchScanForBluetoothDevices() {
		this.launchScanForBluetoothDevicesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CTDashboardInterface.launchScanForBluetoothDevices();
			}
		});
	}

	private void setListenerOnButtonToSendCommands() {
		this.sendCommandsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CTDashboardInterface.sendCommandsToTheDevice();
			}
		});
	}

	private void initializeUARTControlFragmentInterface() {
		this.CTDashboardInterface = new CTDashboardPresenter(this.packageModel);
		this.CTDashboardInterface.setCTDashboardCallback(this);
	}























	/*
	* Cotham Technologies
	* BleProfileServiceReadyActivity methods
	*
	*/
	@Override
	protected void onServiceBinded(UARTService.UARTBinder binder) {}

	@Override
	protected void onServiceUnbinded() {}

	@Override
	protected Class<? extends BleProfileService> getServiceClass() {
		return UARTService.class;
	}

	@Override
	protected UARTService.UARTBinder getService() {
		return super.getService();
	}

	@Override
	protected void onInitialize(Bundle savedInstanceState) {
		super.onInitialize(savedInstanceState);
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {}

	@Override
	protected void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
	}
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {}

	@Override
	protected boolean onOptionsItemSelected(int itemId) {
		return super.onOptionsItemSelected(itemId);
	}

	@Override
	public void onConnectClicked(View view) {
		super.onConnectClicked(view);
	}

	@Override
	public void onConnectClicked() {
		super.onConnectClicked();
	}

	@Override
	protected int getLoggerProfileTitle() {
		return super.getLoggerProfileTitle();
	}

	@Override
	protected Uri getLocalAuthorityLogger() {
		return super.getLocalAuthorityLogger();
	}

	@Override
	public void onDeviceSelected(BluetoothDevice device, String name) {
		super.onDeviceSelected(device, name);
	}

	@Override
	public void onDialogCanceled() {
		super.onDialogCanceled();
	}

	@Override
	public void onDeviceConnecting(BluetoothDevice device) {
		super.onDeviceConnecting(device);
	}

	@Override
	public void onDeviceConnected(BluetoothDevice device) {
		super.onDeviceConnected(device);

	}

	@Override
	public void onDeviceDisconnecting(BluetoothDevice device) {
		super.onDeviceDisconnecting(device);


	}

	@Override
	public void onDeviceDisconnected(BluetoothDevice device) {
		super.onDeviceDisconnected(device);
	}

	@Override
	public void onLinklossOccur(BluetoothDevice device) {
		super.onLinklossOccur(device);
	}

	@Override
	public void onServicesDiscovered(BluetoothDevice device, boolean optionalServicesFound) {
		super.onServicesDiscovered(device, optionalServicesFound);
	}

	@Override
	public void onDeviceReady(BluetoothDevice device) {
		super.onDeviceReady(device);

		this.CTDashboardInterface.sendCommandsToTheDevice();
	}

	@Override
	public void onBondingRequired(BluetoothDevice device) {
		super.onBondingRequired(device);
	}

	@Override
	public void onBonded(BluetoothDevice device) {
		super.onBonded(device);
	}

	@Override
	public void onBatteryValueReceived(BluetoothDevice device, int value) {
		super.onBatteryValueReceived(device, value);
	}

	@Override
	public void onError(BluetoothDevice device, String message, int errorCode) {
		super.onError(device, message, errorCode);
	}

	@Override
	public void onDeviceNotSupported(BluetoothDevice device) {
		super.onDeviceNotSupported(device);
	}

	@Override
	protected boolean isDeviceConnected() {
		return super.isDeviceConnected();
	}

	@Override
	protected String getDeviceName() {
		return super.getDeviceName();
	}

	@Override
	protected void setDefaultUI() {

	}

	@Override
	protected int getDefaultDeviceName() {
		return 0;
	}

	@Override
	protected int getAboutTextId() {
		return 0;
	}

	@Override
	protected UUID getFilterUUID() {
		return null;
	}

	@Override
	protected boolean isBroadcastForThisDevice(Intent intent) {
		return super.isBroadcastForThisDevice(intent);
	}

	@Override
	protected boolean isBLEEnabled() {
		return super.isBLEEnabled();
	}

	@Override
	protected void showBLEDialog() {
		super.showBLEDialog();
	}

}
