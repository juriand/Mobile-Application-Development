package com.hw2.juri.hw2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {
    private static final String TAG = "NoteAdapter";
    private List<Note> noteList;
    private MainActivity mainAct;

    public NoteAdapter(List<Note> noteList, MainActivity ma) {
        this.noteList = noteList;
        mainAct = ma;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int i) {
        Note note = noteList.get(i);
        holder.title.setText(note.getTitle());
        holder.date.setText(note.getLastSave());

        String text = note.getText().toString();
        if(text.length() > 80){
            text = text.substring(0,80) + "...";
        }
        holder.text.setText(text);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
