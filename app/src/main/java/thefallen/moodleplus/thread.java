package thefallen.moodleplus;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by root on 2/11/16.
 */
public class thread implements Comparable {
    int user_id,course_id,id;
    String description, title;
    Timestamp createdAt=null, updatedAt=null;
    public thread(JSONObject jsonObject)
    {
        try {
            user_id = jsonObject.getInt("user_id");
            course_id = jsonObject.getInt("registered_course_id");
            id = jsonObject.getInt("id");
            description = jsonObject.getString("description");
            title = jsonObject.getString("title");
            createdAt = Timestamp.valueOf(jsonObject.getString("created_at"));
            updatedAt = Timestamp.valueOf(jsonObject.getString("updated_at"));
        }catch (JSONException e){
            description=title="";
            id=user_id=course_id=0;
        }
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int compareTo(Object b) {
        return -this.updatedAt.compareTo(((thread) b).getUpdatedAt());
    }

    public String toString() {
        return "[ userid=" + user_id + ", courseid=" + course_id + ", id=" + id + ", title=" + title + ", description=" + description + ", createdat=" + createdAt + ", updatedat=" + updatedAt + "]";
    }

}
