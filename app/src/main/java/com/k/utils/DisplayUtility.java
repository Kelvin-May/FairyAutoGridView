package com.k.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.k.AppApplication;

/**
 * Created by robot on 15/1/27.
 */
public class DisplayUtility {

    static DisplayMetrics mMetrics = AppApplication.application.getResources().getDisplayMetrics();


    /**
     * Covert dp to px
     * @param dp
     * @param context
     * @return pixel
     */
    public static float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity();
        return px;
    }

    /**
     * Covert px to dp
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context){
        float dp = px / getDensity();
        return dp;
    }

    /**
     * Covert px to dp
     * @param px
     * @return dp
     */
    public static float convertPixelToDp(float px){
        return convertPixelToDp(px, null);
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     * @param
     * @return
     */
    public static float getDensity() {
        return mMetrics.density;
    }
    /**
     * dip 2 px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        // return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics())+0.5f);
        return (int) (dipValue * getDensity() + 0.5f);
    }

    /**
     * px 2 dip
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5f);
    }
}
