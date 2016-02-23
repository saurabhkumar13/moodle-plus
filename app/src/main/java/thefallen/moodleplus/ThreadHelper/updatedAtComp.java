package thefallen.moodleplus.ThreadHelper;
import java.util.Comparator;

/*
    Comparator to compare updated at Timestamp
 */

public class updatedAtComp implements Comparator<thread> {
    int order=1;
    public updatedAtComp(int i){if(i==-1) order=-1;}
    @Override
    public int compare(thread a,thread b) {
        return order*a.updatedAt.compareTo((b).getUpdatedAt());
    }
 }