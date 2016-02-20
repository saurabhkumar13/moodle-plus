package thefallen.moodleplus.ThreadHelper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Comparator;

/**
 * Created by root on 2/11/16.
 */
public class thread {
    int user_id,course_id,id;
    String description, title,course;
    Timestamp createdAt=null, updatedAt=null;
    public thread(JSONObject jsonObject,String course)
    {
        try {
            user_id = jsonObject.getInt("user_id");
            course_id = jsonObject.getInt("registered_course_id");
            id = jsonObject.getInt("id");
            description = jsonObject.getString("description");
            title = jsonObject.getString("title");
            createdAt = Timestamp.valueOf(jsonObject.getString("created_at"));
            updatedAt = Timestamp.valueOf(jsonObject.getString("updated_at"));
            this.course = course.substring(0,3).toUpperCase()+" "+course.substring(3,6);
            Log.e("mew",course);
        }catch (JSONException e){
            description=title="";
            id=user_id=course_id=0;
        }
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getCourse() {
        return course;
    }

    public int getThread_id(){return id;}

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // overrides toString method when printing in logs for debugging
    public String toString() {
        return "[ userid=" + user_id +"course= "+course + ", courseid=" + course_id + ", id=" + id + ", title=" + title + ", description=" + description + ", createdat=" + createdAt + ", updatedat=" + updatedAt + "]";
    }

}
