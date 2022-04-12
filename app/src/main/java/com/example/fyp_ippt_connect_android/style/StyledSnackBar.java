package com.example.fyp_ippt_connect_android.style;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.fyp_ippt_connect_android.R;
import com.google.android.material.snackbar.Snackbar;

public class StyledSnackBar {

    public static void styleSnackBar(Snackbar snackbar, Context context) {
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);     // Manually changing colors till theme styling is available for snackbars
        textView.setTextColor(ContextCompat.getColor(context, R.color.infotext));
    }
}
