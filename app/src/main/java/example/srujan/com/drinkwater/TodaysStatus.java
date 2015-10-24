package example.srujan.com.drinkwater;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TodaysStatus extends ActionBarActivity {

    private Toolbar toolbar;
    ProgressBar progressBar;
    FileInputStream fis;
    List<DailyDetails> thisWeekData;

    int waterTarget;
    int waterConsumed;
    int consumptioRate;
    int expectedToday;
    int lastWeekAverage;
    int improvement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_status);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);

        TextView textView;
        List<InformationToday> data = new ArrayList<>();
        try{
            fis = openFileInput("arrayListToday");
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (List<InformationToday>) ois.readObject();
            ois.close();
            fis.close();
        }catch (Exception e){
            Toast.makeText(this,"Catch reading data",Toast.LENGTH_SHORT).show();
        }

        waterConsumed = MainActivity.getTotalWaterConsumed(data);
        textView = (TextView) findViewById(R.id.waterConsumed);
        textView.setText(String.valueOf(waterConsumed));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("default_target",true)){
            waterTarget = preferences.getInt("defaultTargetValue",5000);
        }
        else{
            waterTarget = preferences.getInt("customTargetValue",5000);
        }
        textView = (TextView) findViewById(R.id.waterTarget);
        textView.setText("" + waterTarget);

//        Calendar calendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        consumptioRate = (int)(waterConsumed / hour);
        textView = (TextView) findViewById(R.id.consumptionRate);
        textView.setText("" + consumptioRate);

        expectedToday = consumptioRate*24;
        textView = (TextView) findViewById(R.id.expectedToday);
        textView.setText("" + expectedToday);

        try {
            fis = openFileInput("pastWeekData");
            ObjectInputStream ois = new ObjectInputStream(fis);
            //List<DailyDetails> d = new ArrayList<>();
            thisWeekData = (List<DailyDetails>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG A", "Catch at reading past week data.");
        }
        lastWeekAverage = 0;
        for(int i = 0; i < 7 ; i++){
            lastWeekAverage += thisWeekData.get(thisWeekData.size() - 7 + i ).volume;
        }
        lastWeekAverage /= 7;
        textView = (TextView) findViewById(R.id.lastWeekAvg);
        textView.setText("" + lastWeekAverage);

        textView = (TextView) findViewById(R.id.improvement);
        try {
            improvement = (int) (((expectedToday - lastWeekAverage) / (float)lastWeekAverage) * 100);
            textView.setText("" + improvement + "%");
        }catch (Exception e){
            textView.setText("-");
        }
        if (thisWeekData.get(0).calendar == thisWeekData.get(1).calendar) {
            textView.setText("Data insufficient");
//            Toast.makeText(this,thisWeekData.get(0).calendar.get(Calendar.MINUTE) + "," + thisWeekData.get(1).calendar.get(Calendar.MINUTE),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todays_status, menu);


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
}
