package thefallen.moodleplus.ThreadHelper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Comparator;

import thefallen.moodleplus.identicons.SymmetricIdenticon;

/**
 * Created by root on 2/11/16.
 */
public class thread {
    int user_id,course_id,id;
    String description, title,course;
    Timestamp createdAt=null, updatedAt=null;
    String timestring=null;
    public thread(JSONObject jsonObject,String course)
    {
        try {
            user_id = jsonObject.getInt("user_id");
            course_id = jsonObject.getInt("registered_course_id");
            id = jsonObject.getInt("id");
            description = jsonObject.getString("description");
            title = jsonObject.getString("title");
            timestring = jsonObject.getString("created_at");
            createdAt = Timestamp.valueOf(jsonObject.getString("created_at"));
            updatedAt = Timestamp.valueOf(jsonObject.getString("updated_at"));
            this.course = course.substring(0,3).toUpperCase()+" "+course.substring(3,6);
            Log.e("mew",course);
        }catch (JSONException e){
            description=title="";
            id=user_id=course_id=0;
        }
    }

    public String getCourse() {
        return course;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public Timestamp getUpdatedAt(){
        return updatedAt;
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

    public String getTime(){
        String time = timestring.substring(0,10);
        time = time.substring(8,10)+"/"+time.substring(5,7)+"/"+time.substring(0,4);
        return time;
    }
    // overrides toString method when printing in logs for debugging
    public String toString() {
        return "[ userid=" + user_id +"course= "+course + ", courseid=" + course_id + ", id=" + id + ", title=" + title + ", description=" + description + ", createdat=" + createdAt + ", updatedat=" + updatedAt + "]";
    }

}
