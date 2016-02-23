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
            description = (TextView)itemView.findViewById(R.id.description);
//            identicon = (SymmetricIdenticon)itemView.findViewById(R.id.identicon);
        }
    }

    List<assignmentListItem> elements;
    course header;
    Context context;
    RVAdapterAssignments(List<assignmentListItem> elements,course header){
        this.elements = elements;
        this.header = header;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int i) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(i==0) return 0;
        else return 2;
    }


    @Override
    public ElementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v;
        if(i==0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_header, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assignment_element, viewGroup, false);
        ElementHolder eh = new ElementHolder(v);
        context = viewGroup.getContext();
        return eh;
    }

    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        if(i==0)
        {
            elementHolder.time.setText(header.getYear()+" Semester "+header.getSem());
            elementHolder.title.setText(header.getCourseCode()+" : "+header.getName());
            elementHolder.description.setText(header.getDescription());
            elementHolder.createdOn.setText("Course Structure "+header.getLtp()+" with "+header.getCredits()+" credits");
        }
        else
        {
            String time = TimeHelper.timeFromNow(elements.get(i-1).deadline, -1," left");
            if(!time.equals(""))
                elementHolder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hourglass_empty_blue_grey_500_36dp,0,0,0);
            elementHolder.time.setText(time);
            elementHolder.description.setText(elements.get(i-1).description);
            elementHolder.title.setText(elements.get(i-1).title);
            elementHolder.createdOn.setText("Posted on "+TimeHelper.timeToString(elements.get(i-1).createdAt));
//        elementHolder.identicon.show(elements.get(i).user_id);
//        elementHolder.course.setText(elements.get(i).course_code);
            elementHolder.cv.setTranslationX(-DisplayHelper.getWidth(context));
            elementHolder.cv.animate()
                    .setStartDelay(i * 100)
                    .translationXBy(DisplayHelper.getWidth(context))
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(600)
                    .start();
        }

    }

    @Override
    public int getItemCount() {
        return elements.size()+1;
    }
}
