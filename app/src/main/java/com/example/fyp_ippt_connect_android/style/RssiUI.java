package com.example.fyp_ippt_connect_android.style;

import com.example.fyp_ippt_connect_android.R;

public class RssiUI {
    public static int getDrawableIdForRssi(int rssi) {
        int index;
        if (rssi == 127 || rssi <= -84) {       // 127 reserved for RSSI not available
            index = 0;
        } else if (rssi <= -72) {
            index = 1;
        } else if (rssi <= -60) {
            index = 2;
        } else if (rssi <= -48) {
            index = 3;
        } else {
            index = 4;
        }

        final int[] kSignalDrawables = {
                R.drawable.signalstrength0,
                R.drawable.signalstrength1,
                R.drawable.signalstrength2,
                R.drawable.signalstrength3,
                R.drawable.signalstrength4};
        return kSignalDrawables[index];
    }

}
