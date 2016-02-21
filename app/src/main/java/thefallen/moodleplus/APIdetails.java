package thefallen.moodleplus;

/*
    This class has a list of functions that return the API url that is appropriately defined by their name.
    Purpose of this is to make our life simpler while calling APIs also make the code there more meaningful
    plus make it super simple to make changes to the API or the host which essentially is going to happen
 */
public class APIdetails {
    static private String host = "http://10.0.0.3:8000";
    public static String login()
    {
        return host+"/default/login.json";
    }
    public static String logout()
    {
        return host+"/default/logout.json";
    }
    public static String coursesList()
    {
        return host+"/courses/list.json";
    }
    public static String coursesThreads(String courseCode)
    {
        return host+"/courses/course.json/"+courseCode+"/threads";
    }
    public static String assignmentSubmission(int assignment_id)
    {
        return host+"/courses/assignment/"+assignment_id;
    }
    public static String notifications()
    {
        return host+"/default/notifications.json";
    }
    public static String grades()
    {
        return host+"/default/grades.json";
    }
    public static String assignments(String courseCode)
    {
        return host+"/courses/course.json/"+courseCode+"/assignments";
    }
    public static String assignment(int assignment_id)
    {
        return host+"/courses/assignment.json/"+assignment_id;
    }
    public static String courseGrade(String courseCode)
    {
        return host+"/courses/course.json/"+courseCode+"/grades";
    }
    public static String thread(int id)
    {
        return host+"/threads/thread.json/"+id;
    }
    public static String newThread()
    {
        return host+"/threads/new.json";
    }
    public static String comment()
    {
        return host+"/threads/post_comment.json";
    }
}
