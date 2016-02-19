package thefallen.moodleplus;

public class APIdetails {
    static private String host = "http://192.168.43.254:8000";
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
