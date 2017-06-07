/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ccdle.christophercoverdale.onemillionsteps.UART.uart;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ccdle.christophercoverdale.onemillionsteps.R;

import java.util.UUID;

public class UARTControlFragment extends BleProfileServiceReadyActivity<UARTService.UARTBinder> implements UARTInterface, ScannerFragment.OnDeviceSelectedListener {

	/*
	* Cotham Technologies - Instance variables
	* */
	public static final String DEBUG_TAG = "Cotham Technologies";
	public static String PACKAGE_NAME;

	private Button sendCommand;


	/** The service UART interface that may be used to sendCommandToRXCharacteristic data to the target. */
	private UARTInterface uartInterface;

	private UARTService.UARTBinder serviceBinder;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_feature_uart_control, container, false);
		PACKAGE_NAME = getActivity().getPackageName();
		Log.e(DEBUG_TAG, "control fragment inflated");

		sendCommand = (Button) view.findViewById(R.id.send_commands);
		this.sendCommand.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.e(DEBUG_TAG, "On send clicked");
				onSendClicked();
			}
		});

		final ScannerFragment dialog = ScannerFragment.getInstance(this);
		dialog.show(getActivity().getSupportFragmentManager(), "scan_fragment");


		return view;
	}

	/*Button to send commands*/
	private void onSendClicked() {
		uartInterface.send();
	}

	/*Binding to the ble service to send commands to the device*/
	private ServiceConnection bleServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName name, final IBinder service) {
			final UARTService.UARTBinder bleService = (UARTService.UARTBinder) service;
			uartInterface = bleService;
		}

		@Override
		public void onServiceDisconnected(final ComponentName name) {
			uartInterface = null;
		}
	};
	public void bindServiceToBleService() {

		/*
		 * If the service has not been started before the following lines will not start it. However, if it's running, the Activity will be binded to it
		 * and notified via bleServiceConnection.
		 */
		final Intent service = new Intent(getActivity(), UARTService.class);
		getActivity().bindService(service, bleServiceConnection, 0); // we pass 0 as a flag so the service will not be created if not exists
	}
	public void unbindServiceToBleService() {
		try {
			getActivity().unbindService(bleServiceConnection);
			uartInterface = null;
		} catch (final IllegalArgumentException e) {
			// do nothing, we were not connected to the sensor
		}
	}
	/*Binding to the ble service to send commands to the device*/


	/*Fragment Lifecycle*/
	@Override
	public void onStart() {
		super.onStart();

		bindServiceToBleService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(DEBUG_TAG, "This fragment is onDestroy");
	}

	@Override
	protected void onServiceBinded(UARTService.UARTBinder binder) {

	}

	@Override
	protected void onServiceUnbinded() {

	}

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
	protected void onCreateView(Bundle savedInstanceState) {

	}

	@Override
	protected void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindServiceToBleService();
		Log.d(DEBUG_TAG, "This fragment is onStop");
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(DEBUG_TAG, "This fragment is onPause");
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

	@Override
	public void send() {

	}
}
