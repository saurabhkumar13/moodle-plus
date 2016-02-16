package thefallen.moodleplus;

import org.json.JSONException;
import org.json.JSONObject;

public class grade {
    String title;
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
}
