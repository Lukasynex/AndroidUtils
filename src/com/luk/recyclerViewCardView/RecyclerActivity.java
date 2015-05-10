package com.luk.recyclerViewCardView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

//in android studio:
//make sure to add dependicies to gradle:
//compile 'com.android.support:cardview-v7:21.0.+'
//compile 'com.android.support:recyclerview-v7:21.0.+'

public class RecyclerActivity extends Activity {
    private static String[] textViewsData1;
    private static String[] textViewsData2;
    private Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        context = this;

        textViewsData1 = new String[]{"Janusz","Przemek","Piotr","Karol","Mariusz","Clint","Józek", "Paweł"};
        textViewsData2 = new String[]{"Tracz","Wipler","Łuszcz","Wojtyła","Max Kolonko","Eastwood","Klucha","Kukiz"};

        final RecyclerView myRecycledItems = (RecyclerView) findViewById(R.id.recycler_main_view);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(context);
        myRecycledItems.setLayoutManager(myLayoutManager);


        TemplateListAdapter listAdapter = new TemplateListAdapter(textViewsData1, textViewsData2);
        myRecycledItems.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.ViewHolder> {
        //private static final String TAG = TripOptionsListAdapter.class.getSimpleName();

        private String[] name1;
        private String[] name2;
//        private View.OnClickListener[] listeners;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public View v;
            public TextView text1,text2;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                this.v = v;
            }

            @Override
            public void onClick(View v) {
//            Log.d(TAG, "onClick() : " + getPosition());
//            try {
//                onTripPlanChosen(getPosition());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public TemplateListAdapter(String[] content1, String[] content2) {
            name1 = content1;
            name2 = content2;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TemplateListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_tile, parent, false);


            ViewHolder vh = new ViewHolder(v);
            vh.text1 = (TextView) v.findViewById(R.id.left_item);
            vh.text2 = (TextView) v.findViewById(R.id.right_item);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text1.setText(name1[position]);
            holder.text2.setText(name2[position]);
        }

        @Override
        public int getItemCount() {
            return name1.length;
        }
    }
}
