package thefallen.moodleplus;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by root on 2/16/16.
 */
public class assignmentListItem implements Comparable{
    String title,description;
    Timestamp createdAt=null,deadline=null;
    public assignmentListItem(JSONObject jsonObject)
    {
        try {
            description = jsonObject.getString("description");
            title = jsonObject.getString("name");
            createdAt = Timestamp.valueOf(jsonObject.getString("created_at"));
            deadline = Timestamp.valueOf(jsonObject.getString("deadline"));
        }catch (JSONException e){
            description=title="";
        }
    }
    @Override
    public int compareTo(Object t)
    {
        return compare(this, (assignmentListItem) t);
    }
    public int compare(assignmentListItem a,assignmentListItem b)
    {
        if(System.currentTimeMillis() - b.deadline.getTime()<0)
            return a.deadline.compareTo(b.deadline);
        else return -a.deadline.compareTo(b.deadline);
    }
}
