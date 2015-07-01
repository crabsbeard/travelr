package com.aditya.travelr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.aditya.travelr.adapter.AdapterHistory;
import com.aditya.travelr.database.TravelRDatabaseAdapter;
import com.aditya.travelr.itemclick.RecyclerItemClickListener;
import com.aditya.travelr.pojo.HistoryCard;

import java.util.ArrayList;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class HistoryActivity extends AppCompatActivity {

    RecyclerView rv_history;
    AdapterHistory adapterHistory;
    ArrayList<HistoryCard> arrayList = new ArrayList<>();
    TravelRDatabaseAdapter databaseHelper;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        textView = (TextView) findViewById(R.id.tv_addressTitle);
        textView.setText("Tap For More Options");
        databaseHelper = new TravelRDatabaseAdapter(this);
        rv_history = (RecyclerView) findViewById(R.id.rv_history);
        rv_history.setLayoutManager(new LinearLayoutManager(this));
        setTouchListner();
        adapterHistory = new AdapterHistory(this);
        arrayList = databaseHelper.fetchAllData();
        adapterHistory.setHistoryCards(arrayList);
        rv_history.setAdapter(adapterHistory);
    }

    private void setTouchListner() {
        rv_history.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                shareData(position);
            }
        }));
    }

    private void shareData(int position) {
        HistoryCard historyCard = arrayList.get(position);
        String message = "Hi,\n"+"I was here: \n"
                + historyCard.getNickname()
                +"\n"+historyCard.getReverse()
                +"\n"+"Lat: "+Double.toString(historyCard.getLat())
                +"\n"+"Lon: "+Double.toString(historyCard.getLon());
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sending you my visited location.\n");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Share"));
    }

}

