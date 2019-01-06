package com.example.nabeel.todoapplication.Utils;

import android.content.Context;
import android.graphics.Color;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by Nabeel on 3/26/2018.
 */

public class UtilsProgressbar {

    public static KProgressHUD showProgressDialog(Context context) {
        KProgressHUD progressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setBackgroundColor(Color.TRANSPARENT)
                .setDetailsLabel("Loading", Color.DKGRAY)
                .setDimAmount(0.5f);
        return progressHUD;
    }
}
