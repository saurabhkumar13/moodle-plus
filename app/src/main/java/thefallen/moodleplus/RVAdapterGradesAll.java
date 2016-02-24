package thefallen.moodleplus;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
    Sets up the Recycler View for the display of Grades for all courses
 */


public class RVAdapterGradesAll extends RecyclerView.Adapter<RVAdapterGradesAll.ElementHolder> {

    public static class ElementHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView marks;
        TextView absMarks;
        TextView title;
        TextView time;
        //        SymmetricIdenticon identicon;
        ElementHolder(View itemView,int itemID) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            cv = (CardView)itemView.findViewById(R.id.cv);
            if(itemID==2) {
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

    ArrayList<ArrayList<grade>> elements;
    Context context;
    RVAdapterGradesAll(ArrayList<ArrayList<grade>> elements){
        this.elements = elements;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int i) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        for(int k=0;k<elements.size()&&i>=0;k++) {if(i>elements.get(k).size()) i-=elements.get(k).size()+1; else break;}
        if(i==0) return 0;
        else return 2;
    }

    @Override
    public ElementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        if (i==2)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grade_element, viewGroup, false);
        else
        {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_header, viewGroup, false);
        }
        ElementHolder eh = new ElementHolder(v,i);
        context = viewGroup.getContext();
        return eh;
    }

    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        int k=0;
        for(;k<elements.size()&&i>=0;k++) {if(i>elements.get(k).size()) i-=elements.get(k).size()+1;else break;}
        if( i!=0) {
            grade gr = elements.get(k).get(i-1);
            elementHolder.title.setText(gr.title);
            elementHolder.marks.setText(gr.score + " / " + gr.outOf);
            elementHolder.absMarks.setText(String.format("%.2f", gr.score * gr.weightage / gr.outOf) + " %");
        }
        else
        {
            elementHolder.cv.setCardElevation(10);
            course header = elements.get(k).get(0).courseDetails;
            elementHolder.time.setText("");
            elementHolder.title.setText(header.getCourseCode()+" : "+header.getName());
            elementHolder.absMarks.setText(header.getDescription());
            elementHolder.marks.setText("Course Structure " + header.getLtp() + " with " + header.getCredits() + " credits");
        }
    }

    @Override
    public int getItemCount() {
        int l=0;
        for(int i=0;i<elements.size();i++) l+=elements.get(i).size();
        return l;
    }
}
