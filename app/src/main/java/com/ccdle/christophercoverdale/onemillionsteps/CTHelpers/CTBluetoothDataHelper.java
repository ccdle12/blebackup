package com.ccdle.christophercoverdale.onemillionsteps.CTHelpers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by USER on 4/28/2017.
 */

public class CTBluetoothDataHelper {

    private static final String DEBUG_TAG = "Fitness Helper" ;

    private CTBluetoothDataHelper() {}

    /*
   * Method to perform regex and then return as int
   * */
    public static int getStepCountAsInt(BluetoothGattCharacteristic characteristic) throws UnsupportedEncodingException {

        String stringOfCharacteristic = stringValueOfCharacteristic(characteristic);
        Log.e(DEBUG_TAG, "String step count: " + stringOfCharacteristic);
        if (!stringOfCharacteristic.matches("^[0-9]*$")) { return 0; }

        Matcher matchPattern = Pattern.compile( "[^0].*" )
                .matcher(stringOfCharacteristic);

        return matchPattern.find() ? Integer.valueOf(matchPattern.group(0)) : 0;
    }

    /*Method to take characteristic, get byte value, return as string*/
    private static String stringValueOfCharacteristic(BluetoothGattCharacteristic characteristic) throws UnsupportedEncodingException {
        byte[] valueOfCharacteristic = characteristic.getValue();
        return new String (valueOfCharacteristic, "UTF-8");
    }
}
