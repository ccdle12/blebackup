package com.ccdle.christophercoverdale.onemillionsteps.CTScanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.util.Log;

import com.ccdle.christophercoverdale.onemillionsteps.CTHealthDataSingleton;
import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.CTBluetoothDataHelper;
import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * Created by USER on 5/8/2017.
 */

public class BluetoothDevicePresenter implements BluetoothAdapter.LeScanCallback, BluetoothDeviceFragmentInterface {

    private static final String DEBUG_TAG = "Ble Presenter";

    private PackageModel packageModel;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt connectedGatt;
    private BluetoothDeviceFragmentInterfaceCallbacks bleDeviceCallback;

    /*Device list will hold all the identified ble devices*/
//    private SparseArray<BluetoothDevice> devicesList;

    /*Fast Go Prime Service*/
    /**
     * Nordic UART Service UUID
     */
    private final static UUID UART_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * RX characteristic UUID
     */
    private final static UUID UART_RX_CHARACTERISTIC_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * TX characteristic UUID
     */
    private final static UUID UART_TX_CHARACTERISTIC_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    /**
     * Client Configuration Descriptor UUID
     */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    BluetoothDevicePresenter(PackageModel packageModel, BluetoothDeviceFragmentInterfaceCallbacks bleDeviceCallback) {
        this.packageModel = packageModel;
        this.bleDeviceCallback = bleDeviceCallback;
    }

    /*BluetoothDeviceFragment Interface*/
    @Override
    public void initializeBluetoothAdapter() {
        BluetoothManager manager = (BluetoothManager) this.packageModel.getContext().getSystemService(BLUETOOTH_SERVICE);
        this.bluetoothAdapter = manager.getAdapter();
        //this.devicesList = new SparseArray<BluetoothDevice>();

    }

    @Override
    public void enableBluetooth() {
        if (this.bluetoothAdapter == null || !this.bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.packageModel.getContext().startActivity(enableBluetoothIntent);
        }
    }

    @Override
    public void startScan() {
        this.bluetoothAdapter.startLeScan(this);
    }

    @Override
    public void stopScan() {
        this.bluetoothAdapter.stopLeScan(this);
    }

    /*Callback when device has been scanned*/
    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

        /*Temporary*/
        //this.devicesList.put(bluetoothDevice.hashCode(), bluetoothDevice);

        /*Temporary Solution*/
        if (bluetoothDevice.getAddress().toString().equals("EC:DF:68:2D:D1:3D")) {
            Log.e(DEBUG_TAG, "Device identified: " + bluetoothDevice.getAddress());
            this.connectedGatt = bluetoothDevice.connectGatt(this.packageModel.getContext(), false, this.gattCallback);
        }

        if (this.connectedGatt.connect()) {
            Log.e(DEBUG_TAG, "Device connected");
        }

        this.bleDeviceCallback.dismissDialog();
    }

    /*Callback after connecting to the bluetooth device*/
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /* State Machine Tracking */
        private int state = 0;

        private void reset() { this.state = 0; }

        private void advance() { this.state++; }

        /*
        * Handles changes in the connection state to the ble device.
        * If the connection is successful and connected, search for the services.
        */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(DEBUG_TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */
                gatt.discoverServices();
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*
                 * If at any point we disconnect, send a message to clear the weather values
                 * out of the UI
                 */
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                gatt.disconnect();
            }
        }


        /*
        * Send an enable command to each sensor by writing a configuration
        * characteristic.  This is specific to the SensorTag to keep power
        * low by disabling sensors you aren't using.
        */
        private void enableNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            byte[] writeCommand = "stepR".getBytes();
            switch (this.state) {
                case 0:
                    Log.d(DEBUG_TAG, "Enabling pedometer data");
                    characteristic = gatt.getService(UART_SERVICE_UUID)
                            .getCharacteristic(UART_RX_CHARACTERISTIC_UUID);
                    characteristic.setValue(writeCommand);
                    break;
                default:
                    Log.i(DEBUG_TAG, "All Sensors Enabled");
                    return;
            }

            gatt.writeCharacteristic(characteristic);
        }


        /*
        * The Gatt callback after successfully writing to a characteristic
        *
        */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value
            try {
                readNextSensor(gatt);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            setNotifyNextSensor(gatt);
        }


        /*
         * Read the data characteristic's value for each sensor explicitly
         */
        private void readNextSensor(BluetoothGatt gatt) throws UnsupportedEncodingException {
            BluetoothGattCharacteristic characteristic;
            switch (state) {
                case 0:
                    Log.d(DEBUG_TAG, "Reading pedometer data");
                    characteristic = gatt.getService(UART_SERVICE_UUID)
                            .getCharacteristic(UART_RX_CHARACTERISTIC_UUID);
                    break;
                default:
                    Log.i(DEBUG_TAG, "All Sensors Enabled");
                    return;
            }

            gatt.readCharacteristic(characteristic);
        }


        /*
         * Enable notification of changes on the data characteristic for each sensor
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */
        private void setNotifyNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (this.state) {
                case 0:
                    Log.d(DEBUG_TAG, "Set notify pedometer data");
                    characteristic = gatt.getService(UART_SERVICE_UUID)
                            .getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
                    break;
                default:
                    Log.i(DEBUG_TAG, "All Sensors Enabled");
                    return;
            }

            //Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            //Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }


        /*
        * The Gatt callback after successfully notifying a characteristic
        *
        */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //Once notifications are enabled, we move to the next sensor and start over with enable
            advance();
            enableNextSensor(gatt);
        }


        /*
        * The Gatt callback after discovering services
        *
        */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(DEBUG_TAG, "Services Discovered: " + status);
            /*
             * With services discovered, we are going to reset our state machine and start
             * working through the sensors we need to enable
             */
            reset();
            enableNextSensor(gatt);
        }


        /*
        * The Gatt callback after successfully reading a characteristic
        *
        */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After reading the initial value, next we enable notifications
            setNotifyNextSensor(gatt);
        }


        /*
        * The Gatt callback after a characteristic changes
        * This is where we pull the data from the device
        */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */

            try {
                CTHealthDataSingleton.cacheDeviceStepCount(characteristic);
                Log.e(DEBUG_TAG, "Step Count Changed: " + CTBluetoothDataHelper.getStepCountAsInt(characteristic));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(DEBUG_TAG, "Remote RSSI: " + rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };
}

