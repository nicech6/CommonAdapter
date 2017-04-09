package com.cuihai.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.cuihai.library.GCommonRVAdapter;
import com.cuihai.library.GRecyclerView;
import com.cuihai.library.GViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GRecyclerView recyclerView;
    GCommonRVAdapter<String> adapter;
    List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 100; i++) {
            list.add("GRecyclerView" + i);
        }
        recyclerView = (GRecyclerView) findViewById(R.id.rv);
        adapter = new GCommonRVAdapter<String>(this, R.layout.item_rv, list) {
            @Override
            public void convert(GViewHolder gViewHolder, String s, int position) {
                gViewHolder.setText(R.id.tv, list.get(position).toString());
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
