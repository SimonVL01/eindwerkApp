package com.example.vdabcursist.photofilteradvisor;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ProductViewActivity extends AppCompatActivity {

    private ListView mListView;
    private final List mList = new ArrayList();
    private final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_product_view, mList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        mListView = (ListView) findViewById(R.id.itemsListView);
        String[] products = new String[] {
                "Pantene skintone ultraglossy", "Dove Dark Skincare", "Pantene fabulous make-up kit",
                "Dove Darkshade XL", "Pantene natural font-de-teint", "Pantene inner Goddess dark edition"
        };

        for (String p : products) {
            mList.add(p);
        }

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {

                            @Override
                            public void run() {
                                mList.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }
}
