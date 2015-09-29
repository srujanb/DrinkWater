package example.srujan.com.drinkwater;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
//import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    int primaryProgress;
    int secondaryProgress = 60;
    int interval = 30;
    int waterConsumed = 0;
    int waterTarget = 10000;
    FileInputStream fis;
    FileOutputStream fos;

    ProgressBar progressBar;
    TextView progressNumber;
    TextView percentage;
    Float xOfPercentage;
    Calendar lastAppOpenedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getBaseContext().setTheme(R.style.CustomThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Calendar now = Calendar.getInstance();
                //new Date(System.currentTimeMillis());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences.Editor editor = preferences.edit();
        lastAppOpenedDate.setTimeInMillis(preferences.getLong("lastAppOpenedDateLong",now.getTimeInMillis()));


        if (!(lastAppOpenedDate.MINUTE == now.MINUTE))
            newDay();
//        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColorPinkTheme));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);

        progressNumber = (TextView) findViewById(R.id.progressNumber);
        percentage = (TextView) findViewById(R.id.progressNumber);
        xOfPercentage = progressBar.getX();

    }

    private void newDay() {
        List<DailyDetails> tempDD = new ArrayList<>();
        int volume;

        try {
            fis = openFileInput("pastWeekData");
            ObjectInputStream ois = new ObjectInputStream(fis);
            tempDD = (List<DailyDetails>) ois.readObject();

        }catch (Exception e){

        }
    }

    private void initStats() throws Exception {
        waterConsumed = 0;
        List<InformationToday> data = new ArrayList<>();
        try{
            fis = openFileInput("arrayListToday");
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (List<InformationToday>) ois.readObject();
            ois.close();
            fis.close();

            for(int i = 0; i < data.size();i++){
                waterConsumed += data.get(i).value;
            }
        }catch (Exception e){
            Toast.makeText(this,"initStats catch caught.",Toast.LENGTH_SHORT).show();
        }
        primaryProgress = (int) (100*(waterConsumed/(float)waterTarget));

        //TODO To remove.
        //INIT last 7 days data.
        List<DailyDetails> thisWeekData = new ArrayList<>();
        for(int i = 0; i < 7; i++)
        {
            DailyDetails d = new DailyDetails();
            d.volume = i*30;
            thisWeekData.add(d);
        }
        fos = openFileOutput("pastWeekData", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(thisWeekData);
        oos.close();
        fos.close();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            initStats();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"caught in exception",Toast.LENGTH_SHORT).show();
        }
        changeProgressBar();
        changeProgressBar2();
        //Toast.makeText(this,"main_act onResume called",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(MainActivity.this,Settings.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.launch_change_theme_activity){
            Intent intent = new Intent(MainActivity.this,ChangeTheme.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.drink_water){
            Intent intent = new Intent(MainActivity.this,DrinkWater.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void click(View view) {

    }

    private void changeProgressBar()
    {
        if (progressBar.getProgress() < primaryProgress)
            progressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    progressBar.setProgress(progressBar.getProgress() + 1); // increase the value of the progress bar.
                    progressNumber.setText("" + progressBar.getProgress() + "%");
                    if (progressBar.getProgress()<90)
                        percentage.setX(24 + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
                    changeProgressBar(); // call the function again, but after a delay.

//                    Log.d("TAG B", "" + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
                }
            }, interval);
        if (progressBar.getProgress() > primaryProgress)
            progressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progressBar.getProgress() - 1);
                    progressNumber.setText("" + progressBar.getProgress() + "%");
                    if (progressBar.getProgress()<90)
                        percentage.setX(24 + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
                    changeProgressBar(); // call the function again, but after a delay.
                }
            },interval);
    }
    private void changeProgressBar2() {
        if (progressBar.getSecondaryProgress() < secondaryProgress)
            progressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    progressBar.setSecondaryProgress(progressBar.getSecondaryProgress() + 1); // increase the value of the progress bar.
//                    progressNumber.setText("" + progressBar.getProgress() + "%");
//                    if (progressBar.getProgress()<90)
//                        percentage.setX(24 + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
                    changeProgressBar2(); // call the function again, but after a delay.

//                    Log.d("TAG B", "" + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
                }
            }, interval-5);
        if (progressBar.getSecondaryProgress() > secondaryProgress)
            progressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setSecondaryProgress(progressBar.getSecondaryProgress() - 1);
//                    progressNumber.setText("" + progressBar.getProgress() + "%");
//                    if (progressBar.getProgress()<90)
//                        percentage.setX(24 + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
                    changeProgressBar2(); // call the function again, but after a delay.
                }
            },interval+5);
    }

    public void onClick(View view) {
        percentage.setX(percentage.getX() + 10);
    }

    public void linechart(View view) {
        if(view.getId() == R.id.openchart) {
            Intent intent = new Intent(this, LineChartActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Graph.class);
            startActivity(intent);
        }
    }
}
