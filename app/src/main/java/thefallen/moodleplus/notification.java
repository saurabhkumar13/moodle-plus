package thefallen.moodleplus;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/*
    Extract information required to show a notification from the JSON object
 */

public class notification {
    public int user_id,id,thread_id;
    public String description,course_code,name;
    public boolean not_seen;
    public Timestamp createdAt=null;


    public notification(JSONObject noti)
    {
        try {
            user_id = noti.getInt("user_id");
            id = noti.getInt("id");
            description = noti.getString("description");
            format(description);
            if(noti.getInt("is_seen")==0) not_seen=true;
            createdAt = Timestamp.valueOf(noti.getString("created_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void format(String desc)
    {
        try {
            desc=desc.substring(desc.indexOf('>')+1);
            name = desc.substring(0, desc.indexOf('<'));
            desc = desc.substring(desc.indexOf('>') + 1);
            thread_id=Integer.valueOf(desc.substring(desc.indexOf("d/")+2,desc.indexOf("'>")));
            desc = desc.substring(0, desc.indexOf('<')) + desc.substring(desc.indexOf('>') + 1);
            desc = desc.substring(0, desc.indexOf('<')) + desc.substring(desc.indexOf('>') + 1);
            course_code =(desc.substring(desc.indexOf('>')+1,desc.indexOf("</")));
            course_code=course_code.toUpperCase();
            desc = "Posted a new thread";
            description = desc;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
