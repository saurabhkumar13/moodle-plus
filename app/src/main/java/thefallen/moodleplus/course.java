package thefallen.moodleplus;

import org.json.JSONException;
import org.json.JSONObject;

public class course {
    String courseCode,name,description,ltp;
    int credits,year,sem;
    public course(JSONObject response)
    {
        try {
            JSONObject jsonObject = response.getJSONObject("course");
            courseCode = jsonObject.getString("code").toUpperCase();
            name = jsonObject.getString("name");
            description = jsonObject.getString("description");
            ltp = jsonObject.getString("l_t_p");
            credits = jsonObject.getInt("credits");
            year = response.getInt("year");
            sem =  response.getInt("sem");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

public course(JSONObject jsonObject,int immediate)
    {
        try {
            courseCode = jsonObject.getString("code").toUpperCase();
            name = jsonObject.getString("name");
            description = jsonObject.getString("description");
            ltp = jsonObject.getString("l_t_p");
            credits = jsonObject.getInt("credits");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getSem() {
        return sem;
    }

    public int getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public int getCredits() {
        return credits;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getLtp() {
        return ltp;
    }

    public String getName() {
        return name;
    }

}
