package com.ccdle.christophercoverdale.onemillionsteps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ccdle.christophercoverdale.onemillionsteps.UART.uart.UARTControlFragment;

public class MainActivity extends AppCompatActivity {

    com.ccdle.christophercoverdale.onemillionsteps.CTDashboardVC CTDashboardVC;
    UARTControlFragment uartControlFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.uartControlFragment = new UARTControlFragment();

        if (this.CTDashboardVC == null) {
            this.CTDashboardVC = new CTDashboardVC();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_activity_container, this.uartControlFragment)
                    .commit();
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
