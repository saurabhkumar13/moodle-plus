package thefallen.moodleplus;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class grade {
    String title;
    int course_id;
    double score,outOf,weightage;
    course courseDetails;
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
    public grade(JSONObject grade,JSONObject courseJSON)
    {
        try {
            score = grade.getDouble("score");
            outOf = grade.getDouble("out_of");
            weightage = grade.getDouble("weightage");
            course_id = grade.getInt("registered_course_id");
            title = grade.getString("name");
            courseDetails = new course(courseJSON,0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
