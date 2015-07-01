package com.aditya.travelr.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aditya.travelr.R;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class HistoryCardViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_nickname;
    private TextView tv_description;
    private TextView tv_reverse;
    private TextView tv_lat;
    private TextView tv_lon;

    public HistoryCardViewHolder(View itemView) {
        super(itemView);
        tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
        tv_description = (TextView) itemView.findViewById(R.id.tv_desc);
        tv_reverse = (TextView) itemView.findViewById(R.id.tv_reverse);
        tv_lat = (TextView) itemView.findViewById(R.id.tv_lat);
        tv_lon = (TextView) itemView.findViewById(R.id.tv_lon);
    }

    public TextView getTv_reverse() {
        return tv_reverse;
    }

    public TextView getTv_nickname() {
        return tv_nickname;
    }

    public TextView getTv_description() {
        return tv_description;
    }

    public TextView getTv_lat() {
        return tv_lat;
    }

    public TextView getTv_lon() {
        return tv_lon;
    }
}
