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

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.ccdle.christophercoverdale.onemillionsteps.CTBluetoothClient;
import com.ccdle.christophercoverdale.onemillionsteps.CTBluetoothClientInterface;

import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

public class UARTManager extends BleManager<UARTManagerCallbacks>  {

	private final String DEBUG_TAG = "UARTManager";

	/*Fast Go Prime Commands*/
	/*Cotham Technologies*/
	private final byte[] REQUEST_STEP_COUNT = "stepR".getBytes();

	/** Nordic UART Service UUID */
	private final static UUID UART_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
	/** RX characteristic UUID */
	private final static UUID UART_RX_CHARACTERISTIC_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
	/** TX characteristic UUID */
	private final static UUID UART_TX_CHARACTERISTIC_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
	/** The maximum packet size is 20 bytes. */
	private static final int MAX_PACKET_SIZE = 20;

	private BluetoothGattCharacteristic mRXCharacteristic, mTXCharacteristic;
	private byte[] mOutgoingBuffer;
	private int mBufferOffset;

	private CTBluetoothClientInterface CTBluetoothClientInterface;

	public UARTManager(final Context context) {
		super(context);
		this.CTBluetoothClientInterface = new CTBluetoothClient();
	}



	@Override
	protected BleManagerGattCallback getGattCallback() { return mGattCallback; }

	/**
	 * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving indication, etc
	 */
	private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

		/*
		* CT Blackbox 11
		* Add a request to a queue to enable notifications from a characteristic
		*
		*/
		@Override
		protected Deque<Request> initGatt(final BluetoothGatt gatt) {
			final LinkedList<Request> requests = new LinkedList<>();
			requests.add(Request.newEnableNotificationsRequest(mTXCharacteristic));
			return requests;
		}

		/*
		* CT Blackbox 10
		* This is the callback for identifying and instantiating the service and characterisitics we need to read and write to
		*
		*/
		@Override
		public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
			final BluetoothGattService service = gatt.getService(UART_SERVICE_UUID);
			if (service != null) {
				mRXCharacteristic = service.getCharacteristic(UART_RX_CHARACTERISTIC_UUID);
				mTXCharacteristic = service.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
			}

			boolean writeRequest = false;
			boolean writeCommand = false;
			if (mRXCharacteristic != null) {
				final int rxProperties = mRXCharacteristic.getProperties();
				writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
				writeCommand = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0;

				// Set the WRITE REQUEST type when the characteristic supports it. This will allow to sendCommandToRXCharacteristic long write (also if the characteristic support it).
				// In case there is no WRITE REQUEST property, this manager will divide texts longer then 20 bytes into up to 20 bytes chunks.
				if (writeRequest)
					mRXCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
			}

			return mRXCharacteristic != null && mTXCharacteristic != null && (writeRequest || writeCommand);
		}

		@Override
		protected void onDeviceDisconnected() {
			mRXCharacteristic = null;
			mTXCharacteristic = null;
		}


		@Override
		public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
			// When the whole buffer has been sent
			final byte[] buffer = mOutgoingBuffer;
			if (mBufferOffset == buffer.length) {
				try {
					final String data = new String(buffer, "UTF-8");
					mCallbacks.onDataSent(gatt.getDevice(), data);
				} catch (final UnsupportedEncodingException e) {
					// do nothing
				}
				mOutgoingBuffer = null;
			} else { // Otherwise...
				final int length = Math.min(buffer.length - mBufferOffset, MAX_PACKET_SIZE);
				enqueue(Request.newWriteRequest(mRXCharacteristic, buffer, mBufferOffset, length));
				mBufferOffset += length;
			}
		}

		@Override
		public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
			final String data = characteristic.getStringValue(0);

			CTBluetoothClientInterface.sendStepCountCharacteristic(characteristic);

			mCallbacks.onDataReceived(gatt.getDevice(), data);

		}
	};


	@Override
	protected boolean shouldAutoConnect() {
		// We want the connection to be kept
		return true;
	}

	/**
	 * Sends the given text to RX characteristic.
	 */
	public void sendCommandToRXCharacteristic() {
		// Are we connected?
		if (mRXCharacteristic == null)
			return;

		// An outgoing buffer may not be null if there is already another packet being sent. We do nothing in this case.
		if (mOutgoingBuffer == null) {
			final byte[] buffer = mOutgoingBuffer = REQUEST_STEP_COUNT;
			mBufferOffset = 0;

			// Depending on whether the characteristic has the WRITE REQUEST property or not, we will either sendCommandToRXCharacteristic it as it is (hoping the long write is implemented),
			// or divide it into up to 20 bytes chunks and sendCommandToRXCharacteristic them one by one.
			final boolean writeRequest = (mRXCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;

			if (!writeRequest) { // no WRITE REQUEST property
				final int length = Math.min(buffer.length, MAX_PACKET_SIZE);
				mBufferOffset += length;
				enqueue(Request.newWriteRequest(mRXCharacteristic, buffer, 0, length));
			} else { // there is WRITE REQUEST property, let's try Long Write
				mBufferOffset = buffer.length;
				enqueue(Request.newWriteRequest(mRXCharacteristic, buffer, 0, buffer.length));
			}
		}
	}

}
