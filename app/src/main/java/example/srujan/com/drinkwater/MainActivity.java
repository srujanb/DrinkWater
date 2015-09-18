package example.srujan.com.drinkwater;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
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
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    int primaryProgress = 40;
    int secondaryProgress = 60;
    int interval = 30;

    int waterConsumed = 0;
    int waterTarget = 10000;
    FileInputStream fis;

    ProgressBar progressBar;
    TextView progressNumber;
    TextView percentage;
    Float xOfPercentage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getBaseContext().setTheme(R.style.CustomThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //initStats();
//        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColorPinkTheme));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);

        progressNumber = (TextView) findViewById(R.id.progressNumber);
        percentage = (TextView) findViewById(R.id.progressNumber);
        xOfPercentage = progressBar.getX();

        //changeProgressBar();
        //changeProgressBar2();


    }

    private void initStats() {
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
            Toast.makeText(this,"initStats catch caught.",Toast.LENGTH_SHORT).show();;
        }
        primaryProgress = (int) (100*(waterConsumed/(float)waterTarget));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initStats();
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
        Intent intent = new Intent(this,LineChartActivity.class);
        startActivity(intent);
    }
}
