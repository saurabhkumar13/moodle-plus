package thefallen.moodleplus;

import java.sql.Timestamp;
/*
    Helper class to help with the calculations regarding time parameters required.
    Functions : timeFromNow,timeToString
*/

public class TimeHelper {

    /*
        name : timeFromNow
        function : gives the Time difference b/w now and say a deadline as a string
        input : current timestamp : Timestamp,order whether the difference is taken from a previous time or future time: int
        output : String
     */

    public static String timeFromNow(Timestamp timestamp,int order,String extra)
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
        return res + extra;
    }

    /*
        name: timeToString
        function : makes the current time into a string so that it can be displayed
        input : current timestamp : Timestamp
        output : String
     */

    public static String timeToString(Timestamp time)
    {
        String res="";
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        res += time.getDate() + " " + months[time.getMonth()];
        return res;
    }

}
