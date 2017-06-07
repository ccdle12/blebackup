package com.ccdle.christophercoverdale.onemillionsteps;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by USER on 5/15/2017.
 */
public class MainActivityTest {

    Activity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, true);


    @Before
    public void setUp() {
        this.mainActivity = activityTestRule.getActivity();
    }

    @Test
    public void testIfMainActivityIsLaunched() {
        activityTestRule.launchActivity(new Intent());
        Assert.assertTrue(this.mainActivity.isTaskRoot());
    }
}