package com.aditya.travelr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aditya.travelr.R;
import com.aditya.travelr.pojo.HistoryCard;
import com.aditya.travelr.view.HistoryCardViewHolder;

import java.util.ArrayList;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class AdapterHistory extends RecyclerView.Adapter<HistoryCardViewHolder> {

    private ArrayList<HistoryCard> historyCards = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public AdapterHistory(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setHistoryCards(ArrayList<HistoryCard> historyCards){
        this.historyCards = historyCards;
        notifyItemRangeChanged(0, historyCards.size());
    }
    @Override
    public HistoryCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.historycard, parent, false);
        HistoryCardViewHolder historyCardViewHolder = new HistoryCardViewHolder(view);
        return historyCardViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryCardViewHolder holder, int position) {
        HistoryCard historyCard = historyCards.get(position);
        holder.getTv_nickname().setText(historyCard.getNickname());
        holder.getTv_description().setText(historyCard.getDescription());
        holder.getTv_lat().setText("LAT: "+(Double.toString(historyCard.getLat())).substring(0, 6));
        holder.getTv_lon().setText("LON: "+(Double.toString(historyCard.getLon())).substring(0,6));
    }

    @Override
    public int getItemCount() {
        return historyCards.size();
    }
}
