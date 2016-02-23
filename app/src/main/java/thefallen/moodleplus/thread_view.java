package thefallen.moodleplus;

import java.sql.Timestamp;

public class thread_view {
    int user_id;
    String comment;
    Timestamp createdAt;
    String user_name;

    public thread_view(int user_id, String comment, Timestamp createdAt, String user_name)
    {
            this.user_id = user_id;
            this.comment= comment;
            this.createdAt = createdAt;
            this.user_name = user_name;
    }

    public int getUser_id() {
        return user_id;
    }
    public String getCreatedAt() {
        return TimeHelper.timeFromNow(createdAt,1," ago");
    }

    public String getComment() {
        return comment;
    }

    public String getUser_name() {
        return user_name;
    }

}
