package com.khs.spcmeasure.library;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.AttrRes;


public class ColourUtils {

    // extract theme colour hex value see:
    // https://stackoverflow.com/questions/27611173/how-to-get-accent-color-programmatically
    public static String getThemeColorInHex(@NonNull Context context, @NonNull String colorName, @AttrRes int attribute) {
        TypedValue outValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getTheme().resolveAttribute(attribute, outValue, true);
        } else {
            // get color defined for AppCompat
            int appCompatAttribute = context.getResources().getIdentifier(colorName, "attr", context.getPackageName());
            context.getTheme().resolveAttribute(appCompatAttribute, outValue, true);
        }
        return String.format("#%06X", (0xFFFFFF & outValue.data));
    }
}
