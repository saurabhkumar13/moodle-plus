package thefallen.moodleplus;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.List;

public class RVAdapterAssignments extends RecyclerView.Adapter<RVAdapterAssignments.ElementHolder> {

    public static class ElementHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        TextView createdOn;
        TextView time;
        TextView description;
        //        SymmetricIdenticon identicon;
        ElementHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            time = (TextView)itemView.findViewById(R.id.time);
            title = (TextView)itemView.findViewById(R.id.title);
            createdOn = (TextView)itemView.findViewById(R.id.createdOn);
//            course = (TextView)itemView.findViewById(R.id.course);
            description = (TextView)itemView.findViewById(R.id.description);
//            identicon = (SymmetricIdenticon)itemView.findViewById(R.id.identicon);
        }
    }

    List<assignmentListItem> elements;
    Context context;
    RVAdapterAssignments(List<assignmentListItem> elements){
        this.elements = elements;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    @Override
    public ElementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assignment_element, viewGroup, false);
        ElementHolder eh = new ElementHolder(v);
        context = viewGroup.getContext();
        return eh;
    }

    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        elementHolder.time.setText(DisplayHelper.timeFromNow(elements.get(i).deadline,-1));
        elementHolder.description.setText(elements.get(i).description);
        elementHolder.title.setText(elements.get(i).title);
        elementHolder.createdOn.setText("Posted on "+DisplayHelper.timeToString(elements.get(i).createdAt));
//        elementHolder.identicon.show(elements.get(i).user_id);
//        elementHolder.course.setText(elements.get(i).course_code);
        elementHolder.cv.setTranslationX(-DisplayHelper.getWidth(context));
        elementHolder.cv.animate()
                .setStartDelay(i * 200)
                .translationXBy(DisplayHelper.getWidth(context))
                .setInterpolator(new OvershootInterpolator())
                .setDuration(600)
                .start();

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }
}
