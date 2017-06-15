package com.ccdle.christophercoverdale.onemillionsteps.CTHelpers;

import android.bluetooth.BluetoothGattCharacteristic;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by USER on 4/28/2017.
 */

public class CTBluetoothDataHelper {


    private CTBluetoothDataHelper() {}

    public static int getStepCountAsInt(BluetoothGattCharacteristic characteristic) throws UnsupportedEncodingException {
        String stepCountAsString = stringValueOfBluetoothCharacteristic(characteristic);

        if (!stepCountAsString.matches("^[0-9]*$")) { return 0; }

        Matcher readableIntValuePattern = Pattern.compile( "[^0].*" )
                .matcher(stepCountAsString);

        return readableIntValuePattern.find() ? Integer.valueOf(readableIntValuePattern.group(0)) : 0;
    }

    private static String stringValueOfBluetoothCharacteristic(BluetoothGattCharacteristic characteristic) throws UnsupportedEncodingException {
        byte[] valueOfCharacteristic = characteristic.getValue();
        return new String (valueOfCharacteristic, "UTF-8");
    }
}
