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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    int primaryProgress;
    int secondaryProgress = 60;
    int interval = 30;
    static int waterConsumed = 0;
    static int expectedConsumption;
    int waterTarget = 10000;
    FileInputStream fis;
    FileOutputStream fos;

    ProgressBar progressBar;
    TextView progressNumber;
//    TextView percentage;
    Float xOfPercentage;
    Calendar now = Calendar.getInstance();
    Calendar lastAppOpenedDate = Calendar.getInstance();
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getBaseContext().setTheme(R.style.CustomThemePink);
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean b = preferences.getBoolean("isAppRunningFirstTime",true);
        if (b){
            firstLaunchInit();
        }

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Long L =  preferences.getLong("lastAppOpenedDateLong",now.getTimeInMillis());
        lastAppOpenedDate.setTimeInMillis(L);

        if (!(lastAppOpenedDate.get(Calendar.MINUTE) == now.get(Calendar.MINUTE))) {
            newDay();
        }else {
            //Nothing.
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("lastAppOpenedDateLong", now.getTimeInMillis());
        editor.apply();

//        toolbar.setBackgroundColor(getResources().get0Color(R.color.primaryColorPinkTheme));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);

        progressNumber = (TextView) findViewById(R.id.progressNumber);
//        percentage = (TextView) findViewById(R.id.progressNumber);

        xOfPercentage = progressBar.getX();

        //For the Grapgh fragment.
        try {
            fis = openFileInput("pastWeekData");
            ObjectInputStream ois = new ObjectInputStream(fis);
            //List<DailyDetails> d = new ArrayList<>();
            Graph.thisWeekData = (List<DailyDetails>) ois.readObject();
            ois.close();
            fis.close();
//            Toast.makeText(this,"In test code size of array list is: " + thisWeekData.size(),Toast.LENGTH_SHORT).show();
//            Toast.makeText(this,"Read pastweekData, size = " + thisWeekData.size(),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG A","Catch at reading past week data.");
//            Toast.makeText(this,"Catch at past week data",Toast.LENGTH_SHORT).show();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.graphContainer, new Graph.PlaceholderFragment()).commit();

        LineChartView chart = (LineChartView) findViewById(R.id.graphView);
    }

    private void firstLaunchInit() {
        List<DailyDetails> tempList= new ArrayList<>();
        DailyDetails d = new DailyDetails();
        d.volume = 0;
        d.calendar = Calendar.getInstance();
//        Toast.makeText(this,"Time set in millis: "+d.calendar.getTimeInMillis(),Toast.LENGTH_SHORT).show();
        d.target = 0;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("default_target",true)) {
            preferences.getInt("defaultTargetValue",0);
        }
        else {
            preferences.getInt("customTargetValue",0);
        }
//        Long L = Long.valueOf(0);
//        d.calendar.setTimeInMillis(L);

        for(int i = 0; i < 7; i++){
            tempList.add(0,d);
        }
        try{
            fos = openFileOutput("pastWeekData",Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tempList);
            oos.close();
            fos.close();
            Toast.makeText(this,"FirstLaunchInit try with templist size: " + tempList.size(),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Unexpected Catch" + tempList.size(),Toast.LENGTH_SHORT).show();
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isAppRunningFirstTime",false);
        editor.apply();
    }

    private void newDay() {
        Toast.makeText(this,"New day.",Toast.LENGTH_SHORT).show();

//        int waterConsumed = 0;

        //Get total water consumed.
        List<InformationToday> data = new ArrayList<>();
        try{
            fis = openFileInput("arrayListToday");
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (List<InformationToday>) ois.readObject();
            ois.close();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        waterConsumed = getTotalWaterConsumed(data);

        //Save today's data to file.
        List<DailyDetails> tempDD = new ArrayList<>();
        try{
            fis = openFileInput("pastWeekData");
            ObjectInputStream ois = new ObjectInputStream(fis);
            tempDD = (List<DailyDetails>) ois.readObject();
            ois.close();
            fis.close();
            Toast.makeText(this,"PastWeekData found.",Toast.LENGTH_SHORT).show();
            //TODO change minute to day.
            while(lastAppOpenedDate.get(Calendar.MINUTE) != now.get(Calendar.MINUTE)) {
                DailyDetails dailyDetails = new DailyDetails();
                dailyDetails.volume = waterConsumed;
                preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(preferences.getBoolean("default_target",true))
                    dailyDetails.target = preferences.getInt("defaultTargetValue",4000);
                else
                    dailyDetails.target = preferences.getInt("customTargetValue",4000);
                dailyDetails.calendar = lastAppOpenedDate;
//                Toast.makeText(this,"lastOpenedDate: " + lastAppOpenedDate.get(Calendar.MINUTE),Toast.LENGTH_SHORT).show();
                tempDD.add(tempDD.size(), dailyDetails);
                if(tempDD.size() > 30 || tempDD.get(0).calendar == tempDD.get(1).calendar)
                    tempDD.remove(0);
                //TODO change minute to day
                lastAppOpenedDate.add(Calendar.MINUTE, 1);
                waterConsumed = 0;
            }
            fos = openFileOutput("arrayListToday",MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            List <InformationToday> dataToBeRemoved = new ArrayList<>();
            oos.writeObject(dataToBeRemoved);
            oos.close();
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"pastWeekData not found. Gotta save it.",Toast.LENGTH_SHORT).show();

            //TODO To remove.
            //INIT last 7 days data.
            for(int i = 0; i < 7; i++)
            {
                DailyDetails d = new DailyDetails();
                d.volume = 0;
                tempDD.add(d);
            }
        }

        try{
            fos = openFileOutput("pastWeekData", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tempDD);
            oos.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
//            Toast.makeText(this,"Exception generated in saving tempDD",Toast.LENGTH_SHORT).show();
        }
        //Check if thisWeekData was saved properly
        try{
            fis = openFileInput("pastWeekData");
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<DailyDetails> d = new ArrayList<>();
            d = (List<DailyDetails>) ois.readObject();
            ois.close();
            fis.close();
//            Toast.makeText(this,"In test code (MainActivity) size of array list is: " + d.size(),Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void initStats() throws Exception {
        List<InformationToday> data = new ArrayList<>();
        try{
            fis = openFileInput("arrayListToday");
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (List<InformationToday>) ois.readObject();
            ois.close();
            fis.close();
        }catch (Exception e){
        }
        TextView textView;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("default_target",true)){
            waterTarget = preferences.getInt("defaultTargetValue",5000);
        }
        else{
            waterTarget = preferences.getInt("customTargetValue",5000);
        }

        waterConsumed = getTotalWaterConsumed(data);
        textView = (TextView) findViewById(R.id.waterConsumed);
        textView.setText(String.valueOf(waterConsumed));
        primaryProgress = (int) (100*(waterConsumed/(float)waterTarget));

        expectedConsumption = MainActivity.getExpectedConsumtion(data,waterConsumed);
        secondaryProgress = (int) (100*(expectedConsumption/(float)waterTarget));
        textView = (TextView) findViewById(R.id.expectedConsumption);
        textView.setText(String.valueOf(expectedConsumption));

        textView = (TextView) findViewById(R.id.consumptionRate);
        textView.setText(String.valueOf((int)expectedConsumption/24));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeProgressBar();
                changeProgressBar2();
            }
        },700);
    }

    private static int getExpectedConsumtion(List<InformationToday> data,int waterConsumed) {
        int expCon = 0;
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        expCon = ((waterConsumed/hour)*24);
        return expCon;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            initStats();
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(this,"caught in exception",Toast.LENGTH_SHORT).show();
        }
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


    private void changeProgressBar()
    {
        if (progressBar.getProgress() < primaryProgress)
            progressBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    progressBar.setProgress(progressBar.getProgress() + 1); // increase the value of the progress bar.
                    progressNumber.setText("" + progressBar.getProgress() + "%");
                    if (progressBar.getProgress()<90)
                        progressNumber.setX(24 + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
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
                        progressNumber.setX(24 + progressBar.getProgress() * progressBar.getWidth() / (float) 100.0);
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

    public static int getTotalWaterConsumed(List<InformationToday> data) {
        int TotalWaterConsumed = 0;
//        List<InformationToday> data = new ArrayList<>();
        try{
            for(int i = 0; i < data.size();i++){
                TotalWaterConsumed += data.get(i).value;
            }
        }catch (Exception e){
//            Toast.makeText(this,"initStats catch caught.",Toast.LENGTH_SHORT).show();
        }
        return TotalWaterConsumed;
    }

    public void onClick(View view) {
        if(view.getId() == R.id.card_view_graph || view.getId() == R.id.graphView || view.getId() == R.id.graphContainer){
            Intent intent = new Intent(this, Graph.class);
            startActivity(intent);
        }
        if(view.getId() == R.id.card_view1){
            Intent intent = new Intent(this,TodaysStatus.class);
            startActivity(intent);
        }

    }
}
