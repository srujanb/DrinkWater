package example.srujan.com.drinkwater;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class DrinkWater extends ActionBarActivity {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private InfoTodayAdapter adapter;
    FileOutputStream fos;
    FileInputStream fis;
    List<InformationToday> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_water);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        setFabValues();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_today);
        try {
            adapter = new InfoTodayAdapter(this,getData());
        } catch (IOException e) {
            e.printStackTrace();
            data = new ArrayList<>();
            adapter = new InfoTodayAdapter(this,data);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(recyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                //Toast.makeText(DrinkWater.this, "canDismiss called", Toast.LENGTH_SHORT).show();
                                com.getbase.floatingactionbutton.FloatingActionsMenu f1 = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
                                f1.collapseImmediately();
                                com.getbase.floatingactionbutton.FloatingActionsMenu f2 = (FloatingActionsMenu) findViewById(R.id.multiple_actions2);
                                f2.collapseImmediately();
                                return false;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                adapter.data2.remove(position);
                                //Toast.makeText(DrinkWater.this, "onDismiss called", Toast.LENGTH_SHORT).show();
                                ObjectOutputStream oos;
                                try {
                                    fos = openFileOutput("arrayListToday",Context.MODE_PRIVATE);
                                    oos = new ObjectOutputStream(fos);
                                    oos.writeObject(data);
                                    oos.close();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(DrinkWater.this, "Oops exception generated", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

        recyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        recyclerView.setOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        recyclerView.addOnItemTouchListener(new SwipeableItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else { // R.id.txt_data
                            //Toast.makeText(DrinkWater.this, "Position " + position, Toast.LENGTH_SHORT).show();
                        }
                    }
                }));

    }

    private void setFabValues() {
        //TODO

        //com.getbase.floatingactionbutton.FloatingActionButton button = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fifty_button);
        //button.setTitle("sruj");
    }

    public List<InformationToday> getData() throws IOException, ClassNotFoundException {
        fis = openFileInput("arrayListToday");
        ObjectInputStream ois = new ObjectInputStream(fis);
        data = (List<InformationToday>) ois.readObject();
        ois.close();
        fis.close();
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drink_water, menu);
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

    public void addWater(View view) throws IOException {
        Calendar c = Calendar.getInstance();
        InformationToday i = new InformationToday();
        if(view.getId() == R.id.fifty_button){
            //i.volume = "50";
            i.value = 50;
        }
        else if(view.getId() == R.id.onehundred_button){
            //i.volume = "100";
            i.value = 100;
        }
        else if(view.getId() == R.id.twohundred_button){
            //i.volume = "200"; // 100 50 450 2300 350
            i.value = 200;
        }
        else if(view.getId() == R.id.twohundredFifty_button){
            //i.volume = "250";
            i.value = 250;
        }
        else if(view.getId() == R.id.threehundred_button){
            //i.volume = "300";
            i.value = 300;
        }
        else{
            //i.volume = "500";
            i.value = 500;
        }
        i.hours = c.get(Calendar.HOUR_OF_DAY);
        i.minutes = c.get(Calendar.MINUTE);
        if(data.size()!=0) {
            InformationToday last;
            last = data.get(0);
            if((last.hours == i.hours && last.minutes == i.minutes) || (last.hours == i.hours && (last.minutes + 1) == i.minutes)){
                data.remove(0);
                adapter.notifyItemRemoved(0);
                i.value += last.value;
                data.add(0,i);
            }
            else
                data.add(0, i);
        }
        else {
            data.add(0, i);
            //Toast.makeText(this,"data.added",Toast.LENGTH_SHORT).show();
        }
        adapter.notifyItemInserted(0);

        recyclerView.scrollToPosition(0);

        fos = openFileOutput("arrayListToday",Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(data);
        oos.close();
        fos.close();

        com.getbase.floatingactionbutton.FloatingActionsMenu f;
        f = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        f.collapseImmediately();
        f = (FloatingActionsMenu) findViewById(R.id.multiple_actions2);
        f.collapseImmediately();

    }

    public void FABOnClick(View v) {
        com.getbase.floatingactionbutton.FloatingActionsMenu fo;
        com.getbase.floatingactionbutton.FloatingActionsMenu fc;
        int s = v.getId();
        if(v.getId() == R.id.multiple_actions) {
            fc = (FloatingActionsMenu) findViewById(R.id.multiple_actions2);
            fo = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
            Toast.makeText(this,"multiple_action",Toast.LENGTH_SHORT).show();
        }
        else {
            fc = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
            fo = (FloatingActionsMenu) findViewById(R.id.multiple_actions2);
            Toast.makeText(this,"multiple_action2 - "+ s,Toast.LENGTH_SHORT).show();
        }
        fc.collapseImmediately();
        fo.expand();
    }
}
