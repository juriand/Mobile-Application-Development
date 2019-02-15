package com.hw2.juri.hw2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder{
    public TextView title;
    public TextView date;
    public TextView text;

    public NoteViewHolder(View view) {
        super(view);

        title = view.findViewById(R.id.title);
        date = view.findViewById(R.id.lastdate);
        text = view.findViewById(R.id.text);
    }
}
