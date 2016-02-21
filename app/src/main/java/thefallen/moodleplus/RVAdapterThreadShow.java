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

import java.util.List;

import thefallen.moodleplus.identicons.SymmetricIdenticon;

public class RVAdapterThreadShow extends RecyclerView.Adapter<RVAdapterThreadShow.ElementHolder> {

    public static class ElementHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView time;
        TextView comment;
        SymmetricIdenticon identicon;

        ElementHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            name = (TextView)itemView.findViewById(R.id.title);
            time = (TextView)itemView.findViewById(R.id.time);
            comment = (TextView)itemView.findViewById(R.id.description);
            identicon = (SymmetricIdenticon)itemView.findViewById(R.id.identicon);
        }
    }

    List<thread_view> elements;
    Context context;
    RVAdapterThreadShow(List<thread_view> elements){
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
        if(i==0) return 0;
        else return 2;
    }

    @Override
    public ElementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        Log.e("threadHeader",i+"");
        if(i!=0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_element, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.thread_header, viewGroup, false);
        ElementHolder eh = new ElementHolder(v);
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
        elementHolder.name.setText(elements.get(i).getUser_name());
        elementHolder.comment.setText(elements.get(i).getComment());
        elementHolder.time.setText(elements.get(i).getCreatedAt());
        elementHolder.identicon.show(elements.get(i).getUser_id());
        elementHolder.cv.setTranslationX(-DisplayHelper.getWidth(context));
        elementHolder.cv.animate()
                .setStartDelay((i+1) * 100)
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
