package com.ccdle.christophercoverdale.onemillionsteps;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//import org.junit.Assert;

/**
 * Created by USER on 5/6/2017.
 */

@RunWith(AndroidJUnit4.class)
public class CTDashboardVCInstrumentTest {

    com.ccdle.christophercoverdale.onemillionsteps.CTDashboardVC CTDashboardVC;
    CTDashboardInterface CTDashboardInterface;

    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Assert.assertEquals("com.ccdle.christophercoverdale.onemillionsteps", appContext.getPackageName());
    }

    @Before
    public void setFragment() {
        this.CTDashboardVC = new CTDashboardVC();
        this.activityTestRule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_activity_container, this.CTDashboardVC)
                .commit();

        this.activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void checkFragmentIsDisplayed() {
        Assert.assertTrue(this.CTDashboardVC.isVisible());
    }


    @Test
    public void checkIfContextIsSentToThePresenter() {
    }
}