package com.ccdle.christophercoverdale.onemillionsteps.CTHelpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by USER on 5/7/2017.
 */

public class CTInternetConnectionHelper {

    private CTInternetConnectionHelper() {}

    public static boolean isConncetedToInternet(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }

        return true;

    }
}
