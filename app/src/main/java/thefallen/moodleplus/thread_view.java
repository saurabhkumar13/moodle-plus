package thefallen.moodleplus;

public class thread_view {
    int user_id;
    String comment;
    String createdAt;
    String user_name;

    public thread_view(int user_id, String comment, String createdAt, String user_name)
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
        return createdAt;
    }

    public String getComment() {
        return comment;
    }

    public String getUser_name() {
        return user_name;
    }

}
