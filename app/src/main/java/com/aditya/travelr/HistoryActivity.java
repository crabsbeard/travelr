package com.aditya.travelr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.aditya.travelr.adapter.AdapterHistory;
import com.aditya.travelr.database.TravelRDatabaseAdapter;
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
        adapterHistory = new AdapterHistory(this);
        arrayList = databaseHelper.fetchAllData();
        adapterHistory.setHistoryCards(arrayList);
        rv_history.setAdapter(adapterHistory);
    }
}
