package com.example.jerrysun.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jerrysun on 4/30/17.
 */

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Official> officialList;
    private MainActivity mainActivity;

    public OfficialAdapter(List<Official> offcialList, MainActivity mainActivity) {
        this.officialList = offcialList;
        this.mainActivity = mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.office.setText(official.getOffice());
        holder.official.setText(official.getName() + " (" + official.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
