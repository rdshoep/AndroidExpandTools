package com.rdshoep.android.utils;
/*
 * @description
 *   Please write the PixelUtil module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/13/2016)
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

public class PixelUtil {
    public static int dpToPx(@NonNull Context context, int dp) {
        return Math.round(dp * getPixelScaleFactor(context));
    }

    public static int pxToDp(Context context, int px) {
        return Math.round(px / getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
