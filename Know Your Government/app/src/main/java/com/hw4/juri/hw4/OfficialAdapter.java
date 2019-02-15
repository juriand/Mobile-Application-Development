package com.hw4.juri.hw4;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private List<Official> officialList;
    private MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity ma) {
        this.officialList = officialList;
        mainAct = ma;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        itemView.setOnClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int i) {
        Official official = officialList.get(i);
        holder.office.setText(official.getOffice());
        holder.name.setText(official.getName()+" ("+official.getParty()+")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
