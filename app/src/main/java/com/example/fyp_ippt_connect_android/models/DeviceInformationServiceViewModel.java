package com.example.fyp_ippt_connect_android.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.fyp_ippt_connect_android.ble.peripheral.DeviceInformationPeripheralService;

public class DeviceInformationServiceViewModel extends AndroidViewModel {

    public DeviceInformationServiceViewModel(@NonNull Application application) {
        super(application);
    }

    public DeviceInformationPeripheralService getDeviceInfomationPeripheralService() {
        return PeripheralModeManager.getInstance().getDeviceInfomationPeripheralService();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        // Save current characteristic values
        PeripheralModeManager.getInstance().getDeviceInfomationPeripheralService().saveValues(getApplication());
    }
}
