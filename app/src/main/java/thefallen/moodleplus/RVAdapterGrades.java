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

public class RVAdapterGrades extends RecyclerView.Adapter<RVAdapterGrades.ElementHolder> {

    public static class ElementHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView marks;
        TextView absMarks;
        TextView title;
        TextView time;
        ElementHolder(View itemView,int type) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.title);

            if(type!=0){
                marks = (TextView)itemView.findViewById(R.id.marks);
                absMarks = (TextView)itemView.findViewById(R.id.absMarks);
            }
            else {
                time = (TextView)itemView.findViewById(R.id.time);
                marks = (TextView)itemView.findViewById(R.id.createdOn);
                absMarks = (TextView)itemView.findViewById(R.id.description);
            }
        }
    }

    List<grade> elements;
    course header;
    Context context;
    RVAdapterGrades(List<grade> elements,course header){
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
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grade_element, viewGroup, false);
        ElementHolder eh = new ElementHolder(v,i);
        context = viewGroup.getContext();
        return eh;
    }

    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        if(i==0)
        {
            elementHolder.time.setText(header.getYear()+" Semester "+header.getSem());
            elementHolder.title.setText(header.getCourseCode()+" : "+header.getName());
            elementHolder.absMarks.setText(header.getDescription());
            elementHolder.marks.setText("Course Structure " + header.getLtp() + " with " + header.getCredits() + " credits");
        }
        else {
            elementHolder.title.setText(elements.get(i-1).title);
            elementHolder.marks.setText(elements.get(i-1).score + " / " + elements.get(i-1).outOf);
            elementHolder.absMarks.setText(String.format("%.2f", elements.get(i-1).score * elements.get(i-1).weightage / elements.get(i-1).outOf) + " %");
//        elementHolder.identicon.show(elements.get(i).user_id);
//        elementHolder.course.setText(elements.get(i).course_code);
            elementHolder.cv.setTranslationX(-DisplayHelper.getWidth(context));
            elementHolder.cv.animate()
                    .setStartDelay(i * 50)
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
