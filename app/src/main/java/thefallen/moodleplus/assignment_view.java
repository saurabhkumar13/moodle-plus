package thefallen.moodleplus;

import java.sql.Timestamp;

/*
    This class appropriates the extracted values from the JSON object and stores them in variables for further usage
 */
public class assignment_view {

    int assgn_id;
    String name;
    Timestamp createdAt;
    String link;

    public assignment_view(int id, String name, Timestamp createdAt,String filelink)
    {
        this.assgn_id = id;
        this.name= name;
        this.createdAt = createdAt;
        this.link = filelink;

    }

    public int getAssgn_id() {
        return assgn_id;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
