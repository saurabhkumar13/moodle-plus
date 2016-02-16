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
        //        SymmetricIdenticon identicon;
        ElementHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.title);
//            course = (TextView)itemView.findViewById(R.id.course);
            marks = (TextView)itemView.findViewById(R.id.marks);
            absMarks = (TextView)itemView.findViewById(R.id.absMarks);
//            identicon = (SymmetricIdenticon)itemView.findViewById(R.id.identicon);
        }
    }

    List<grade> elements;
    Context context;
    RVAdapterGrades(List<grade> elements){
        this.elements = elements;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    @Override
    public ElementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grade_element, viewGroup, false);
        ElementHolder eh = new ElementHolder(v);
        context = viewGroup.getContext();
        return eh;
    }

    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        elementHolder.title.setText(elements.get(i).title);
        elementHolder.marks.setText(elements.get(i).score+" / "+elements.get(i).outOf);
        elementHolder.absMarks.setText(String.format( "%.2f", elements.get(i).score*elements.get(i).weightage/elements.get(i).outOf)+" %");
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
