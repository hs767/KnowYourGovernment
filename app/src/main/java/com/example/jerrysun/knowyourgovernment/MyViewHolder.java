package com.example.jerrysun.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;

/**
 * Created by jerrysun on 4/29/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView office;
    public TextView official;

    public MyViewHolder(View view) {
        super(view);
        this.office = (TextView) view.findViewById(R.id.office);
        this.official = (TextView) view.findViewById(R.id.official);
    }
}
