package thefallen.moodleplus;

/**
 * Created by saurabh on 11/2/16.
 */
public class APIdetails {
    static private String host = "http://10.0.0.16:8080";
    public static String login()
    {
        return host+"/default/login.json";
    }
    public static String coursesList()
    {
        return host+"/courses/list.json";
    }
    public static String coursesThreads()
    {
        return host+"/courses/course.json";
    }
}
