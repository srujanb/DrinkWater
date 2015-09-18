package example.srujan.com.drinkwater;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
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


    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            SwitchPreferenceCompat s = (SwitchPreferenceCompat) findPreference("default_target");
            s.setChecked(true);
            int l = 4;
            s.setSummary("" + s.getSummary() + l + "L/DAY");

            for(int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++){
                PreferenceCategory lol = (PreferenceCategory) getPreferenceScreen().getPreference(x);
                for(int y = 0; y < lol.getPreferenceCount(); y++){
                    Preference pref = lol.getPreference(y);
                    pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            if (preference.getKey().equals("dnd_at_night"))
                            {
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