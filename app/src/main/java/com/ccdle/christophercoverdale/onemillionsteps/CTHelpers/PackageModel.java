package com.ccdle.christophercoverdale.onemillionsteps.CTHelpers;

import android.app.Activity;
import android.content.Context;

/**
 * Created by USER on 5/7/2017.
 */

public class PackageModel {

    private Activity activity;
    private Context context;

    public PackageModel(Context context, Activity activity) {
        this.activity = activity;
        this.context  = context;
    }

    public Activity getActivity() { return this.activity;}
    public Context  getContext()  { return this.context; }


}
