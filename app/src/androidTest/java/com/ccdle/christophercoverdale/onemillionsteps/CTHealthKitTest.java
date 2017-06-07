package com.ccdle.christophercoverdale.onemillionsteps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.ccdle.christophercoverdale.onemillionsteps.CTHelpers.PackageModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by USER on 5/7/2017.
 */
public class CTHealthKitTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    private Context testContext;
    private PackageModel mPackageModelHelper;
    private com.ccdle.christophercoverdale.onemillionsteps.CTHealthKit CTHealthKit;
    com.ccdle.christophercoverdale.onemillionsteps.CTDashboardVC CTDashboardVC;

    @Before
    public void setUpHealthKit() {

        this.CTDashboardVC = new CTDashboardVC();
        this.activityTestRule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_container, this.CTDashboardVC)
                .commit();

//        this.activityTestRule.launchActivity(new Intent());

        this.testContext  = InstrumentationRegistry.getTargetContext();
        this.mPackageModelHelper = new PackageModel(this.testContext, activityTestRule.getActivity());
//        this.CTHealthKit    = new CTHealthKit(this.mPackageModelHelper, );
    }

    @Test
    public void testIfContextIsNotNull() {
        Assert.assertNotNull(this.testContext);
    }


}