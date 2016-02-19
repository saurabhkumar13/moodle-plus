package thefallen.moodleplus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class grade {
    String title,course_desc;
    int course_id;
    double score,outOf,weightage;
    public grade(JSONObject jsonObject)
    {
        try {
            score = jsonObject.getDouble("score");
            outOf = jsonObject.getDouble("out_of");
            weightage = jsonObject.getDouble("weightage");
            course_id = jsonObject.getInt("registered_course_id");
            title = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public grade(JSONObject grade,JSONObject course)
    {
        try {
            score = grade.getDouble("score");
            outOf = grade.getDouble("out_of");
            weightage = grade.getDouble("weightage");
            course_id = grade.getInt("registered_course_id");
            title = grade.getString("name");
            course_desc = course.getString("code").toUpperCase()+": "+course.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String toString()
    {
        return course_desc+" "+course_id+" "+title;
    }
}
