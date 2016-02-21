package thefallen.moodleplus;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by mayank on 21/02/16.
 */
public class assignment_header {

    String assgn_title, file, description, course_code;
    int latedays,prof_id;
    Timestamp deadline;


    public assignment_header(JSONObject assignment,JSONObject registered,JSONObject course)
    {
        try {
            assgn_title = assignment.getString("name");
            course_code = course.getString("code").toUpperCase();
            latedays = assignment.getInt("late_days_allowed");
            prof_id = registered.getInt("professor");
            deadline = Timestamp.valueOf(assignment.getString("deadline"));
            description = assignment.getString("description");
            file = assignment.getString("file_");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getLatedays() {
        return latedays;
    }

    public int getProf_id() {
        return prof_id;
    }

    public String getAssgn_title() {
        return assgn_title;
    }

    public String getCourse_code() {
        return course_code;
    }

    public String getDescription() {
        return description;
    }

    public String getFile() {
        return file;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

}
