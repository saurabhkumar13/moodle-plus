package thefallen.moodleplus;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import thefallen.moodleplus.identicons.SymmetricIdenticon;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ElementHolder> {

    public static class ElementHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        TextView description;
        TextView time;
        SymmetricIdenticon identicon;
        ElementHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            identicon = (SymmetricIdenticon)itemView.findViewById(R.id.identicon);
        }
    }

    List<thread> elements;

    RVAdapter(List<thread> elements){
        this.elements = elements;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    @Override
    public ElementHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.thread_element, viewGroup, false);
        ElementHolder eh = new ElementHolder(v);
        return eh;
    }

    @Override
    public void onBindViewHolder(ElementHolder elementHolder, int i) {
        elementHolder.title.setText(elements.get(i).title);
        elementHolder.description.setText(elements.get(i).description);
        elementHolder.identicon.show(elements.get(i).user_id);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }
}
