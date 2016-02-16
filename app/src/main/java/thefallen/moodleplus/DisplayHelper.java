package thefallen.moodleplus;

import android.content.Context;
import android.util.DisplayMetrics;

import java.sql.Timestamp;

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
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    //Returns height of the current display
    public static int getHeight(Context mContext){

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
    public static int getWidth(Context mContext){

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static String timeFromNow(Timestamp timestamp,int order)
    {
        String res;
        int secs = (int) (order*(System.currentTimeMillis() - timestamp.getTime()) / 1000);
        if(secs<0) return "";
        int min = secs / 60;
        secs = secs % 60;
        int hours =  min / 60;
        int days = hours / 24;
        if(days > 7) return timeToString(timestamp);
        if (days > 1) res = days + " days " + hours % 24 + " hours";
        else if(days == 1) res = days + " day " + hours % 24 + " hours";
        else if (hours != 0) res = hours + " hours";
        else if (min > 10)
            res = min + " mins";
        else if (min != 0)
            res = min + " mins " + secs + " secs";
        else res = secs + " secs";
        if (order == 1) return res + " ago";
        else return res + " left";
    }
    public static String timeToString(Timestamp time)
    {
        String res="";
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        res += time.getDate() + " " + months[time.getMonth()]+" 20"+time.getYear()%100;
        return "Posted on "+res;
    }
}

