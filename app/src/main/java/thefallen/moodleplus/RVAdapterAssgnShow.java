package thefallen.moodleplus;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.List;

import thefallen.moodleplus.identicons.SymmetricIdenticon;

/**
 * Created by mayank on 21/02/16.
 */
public class RVAdapterAssgnShow extends RecyclerView.Adapter<RVAdapterAssgnShow.ElementHolder>{

    public static class ElementHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView time;
        TextView description;
        SymmetricIdenticon identicon;
        TextView c_code;
        TextView download;
        TextView delete;


        ElementHolder(View itemView,int type) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            cv = (CardView) itemView.findViewById(R.id.cv);
            name = (TextView) itemView.findViewById(R.id.fileName);
            if (type == 0) {
                c_code = (TextView) itemView.findViewById(R.id.course);
                description = (TextView) itemView.findViewById(R.id.description);
                identicon = (SymmetricIdenticon) itemView.findViewById(R.id.identicon);
            }
            else
            {
                download = (TextView) itemView.findViewById(R.id.download);
                delete = (TextView) itemView.findViewById(R.id.delete);
            }
        }
    }

    List<assignment_view> elements;
    assignment_header ah;
    Context context;

    RVAdapterAssgnShow(assignment_header ah,List<assignment_view> elements){
        this.elements = elements;
        this.ah = ah;
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
        if(i!=0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.submissions_element, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assignment_header, viewGroup, false);
        ElementHolder eh = new ElementHolder(v,i);
        context = viewGroup.getContext();
        return eh;
    }
    @Override
    public void onViewDetachedFromWindow(ElementHolder elementHolder)
    {
        elementHolder.cv.clearAnimation();
    }
    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        if (i == 0)
        {
            String dstr;
            if(TimeHelper.timeFromNow(ah.getDeadline(),-1," left").equals(""))
            {
                dstr = "Assignment deadline was " + TimeHelper.timeToString(ah.getDeadline());
            }
            else {
                if(((-System.currentTimeMillis() + ah.deadline.getTime()) / (1000*60*60*24))>7)
                    dstr = "Assignment deadline is on "+TimeHelper.timeToString(ah.deadline);
                else{
                dstr = TimeHelper.timeFromNow(ah.getDeadline(), -1," left");
                if (ah.getLatedays() != 0)
                     dstr += "+ "+ah.getLatedays() + " days **";}
            }
            elementHolder.name.setText(ah.getAssgn_title());
            elementHolder.time.setText(dstr);
            elementHolder.identicon.show(ah.getProf_id());
            elementHolder.description.setText(ah.getDescription());
            elementHolder.c_code.setText(ah.getCourse_code());

        }
        else {
            elementHolder.name.setText(elements.get(i-1).getName());
            elementHolder.time.setText(TimeHelper.timeFromNow(elements.get(i - 1).getCreatedAt(),1," ago"));
        }
    }
    @Override
    public int getItemCount() {
        return elements.size()+1;
    }

}
