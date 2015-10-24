package example.srujan.com.drinkwater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.Toast;

import example.srujan.com.drinkwater.NumberPicker.NumberPickerPreferenceWaterTarget;
import example.srujan.com.drinkwater.SubSettings.SubSettings1;

/**
 * Created by xisberto on 08/11/14.
 */
public class Settings extends ActionBarActivity {
//    private Toolbar mToolBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.setTheme(R.style.CustomThemePink);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setCollapsible(true);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColorPinkTheme));
        getFragmentManager().beginTransaction().
                add(R.id.content_frame, new SettingsFragment())
                .commit();

        /*prepareLayout();

        mToolBar.setTitle("Header_general");
        addPreferencesFromResource(R.xml.preference);*/
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Back pressed",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        int i = s.getInt("custom_target",0);
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("customTargetValue",i*1000);
        editor.apply();
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            SwitchPreferenceCompat s = (SwitchPreferenceCompat) findPreference("default_target");
            NumberPickerPreferenceWaterTarget s2 = (NumberPickerPreferenceWaterTarget) findPreference("custom_target");
            int defaultTarget;
            int customTarget;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            defaultTarget = preferences.getInt("defaultTargetValue", 0);
            customTarget = preferences.getInt("customTargetValue", 0);
            if (s.isChecked()) {
                s.setSummary("Default target: " + defaultTarget + " mL/DAY");
                s2.setSummary("Disable default target to set custom target.");
            } else {
                s.setSummary("Touch to use Default target.");
                s2.setSummary("Custom target: " + customTarget / 1000 + " L");
            }
            s2.setOnPreferenceChangeListener();

            for (int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++) {
                PreferenceCategory lol = (PreferenceCategory) getPreferenceScreen().getPreference(x);
                for (int y = 0; y < lol.getPreferenceCount(); y++) {
                    Preference pref = lol.getPreference(y);
                    pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            if (preference.getKey().equals("dnd_at_night")) {
//                                Toast.makeText(getActivity(),"dnd at night",Toast.LENGTH_SHORT).show();
                                Snackbar.make(getView(), "This is a snackbar", Snackbar.LENGTH_LONG).show();
                            }
//                            Log.d("TAG A", "dnd at night");
                            return true;
                        }

                    });
                }
            }
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey().equals("subpreference_1")) {
                Intent intent = new Intent(getActivity(), SubSettings1.class);
                startActivity(intent);
            }


            return false;
        }
    }
}