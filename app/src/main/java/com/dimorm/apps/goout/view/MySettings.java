package com.dimorm.apps.goout.view;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.model.DatabaseSQL;

public class MySettings extends PreferenceActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference preference = findPreference("del_fav");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DatabaseSQL databaseSQL = new DatabaseSQL(MySettings.this);
                databaseSQL.getWritableDatabase().delete("favorites",null,null);
                Toast.makeText(MySettings.this, "Favorites was successfully deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        ListPreference listPreference = (ListPreference)findPreference("units");
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(newValue.equals("KM")){
                    MainActivity.DISTANCE_IN_KM = true;
                }
                else{
                    MainActivity.DISTANCE_IN_KM = false;
                }
                return true;
            }
        });






    }
}




