package com.dimorm.apps.goout.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.model.DatabaseSQL;

/**
 * Created by Dima on 5/5/2017.
 */

public class MySettings extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);


        SwitchPreference switchPreferenceDis = (SwitchPreference) findPreference("switch_dis");
            switchPreferenceDis.setChecked(MainActivity.DISTANCE_IN_KM);
            switchPreferenceDis.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    MainActivity.DISTANCE_IN_KM = (boolean) newValue;
                    preference.getEditor().putBoolean("switch_dis",MainActivity.DISTANCE_IN_KM).commit();
                    Toast.makeText(MySettings.this, MainActivity.DISTANCE_IN_KM + " ", Toast.LENGTH_SHORT).show();
                    return true;

                }
            });






        Preference preference = (Preference) findPreference("del_fav");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DatabaseSQL databaseSQL = new DatabaseSQL(MySettings.this);
                databaseSQL.getWritableDatabase().delete("favorites",null,null);
                Toast.makeText(MySettings.this, "Favorites was successfully deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }}




