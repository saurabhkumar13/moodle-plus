package thefallen.moodleplus;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by mayank on 10/01/16.
 * Helper class to help with the calculations regarding layout parameters on the screen.
 * Functions : dpToPx, getHeight
 */
public class DisplayHelper {

    //converts dp into pixels
    //http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android

    public static int dpToPx(int dp, Context mContext) {

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    //Returns height of the current display
    public static int getHeight(Context mContext){

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
