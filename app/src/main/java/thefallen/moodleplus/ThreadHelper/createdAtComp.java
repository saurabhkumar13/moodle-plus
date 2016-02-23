package thefallen.moodleplus.ThreadHelper;
import java.util.Comparator;

/*
    comparator to compare created at timestamp
 */

public class createdAtComp implements Comparator<thread> {
    int order=1;
    public createdAtComp(int i){if(i==-1) order=-1;}
    @Override
    public int compare(thread a,thread b) {
        return order*a.createdAt.compareTo((b).getCreatedAt());
    }
 }