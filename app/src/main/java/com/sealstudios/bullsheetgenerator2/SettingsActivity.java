package com.sealstudios.bullsheetgenerator2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import androidx.appcompat.app.AlertDialog;

import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.utils.Constants;

public class SettingsActivity extends PreferenceActivity {

    public SwitchPreference ujmSwitch, totaljobsSwitch, indeedSwitch;
    private String TAG = "SttngAct";
    Boolean ujmSrc, indeedSrc, totaljobsSrc;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_main);
        Preference deletebutton = findPreference(getString(R.string.pref_delete));
        final Preference aboutbutton = findPreference("about");
        Preference sharebutton = findPreference("share");
        ujmSwitch = (SwitchPreference) findPreference(getString(R.string.ujmsettingkey));
        totaljobsSwitch = (SwitchPreference) findPreference(getString(R.string.totaljobssettingkey));
        indeedSwitch = (SwitchPreference) findPreference(getString(R.string.indeedsettingkey));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ujmSrc = prefs.getBoolean(Constants.FIND_A_JOB, true);//set to false for debugging
        indeedSrc = prefs.getBoolean(Constants.INDEED, true);
        totaljobsSrc = prefs.getBoolean(Constants.TOTAL_JOBS, true);
        ujmSwitch.setChecked(ujmSrc);
        indeedSwitch.setChecked(indeedSrc);
        totaljobsSwitch.setChecked(totaljobsSrc);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(Constants.FIND_A_JOB)) {
                    boolean ujmBool = sharedPreferences.getBoolean(Constants.FIND_A_JOB, true);
                    ujmSwitch.setChecked(ujmBool);
                }
                if (key.equals(Constants.TOTAL_JOBS)) {
                    boolean totaljobsBool = sharedPreferences.getBoolean(Constants.TOTAL_JOBS, true);
                    totaljobsSwitch.setChecked(totaljobsBool);
                }
                if (key.equals(Constants.INDEED)) {
                    boolean indeedBool = sharedPreferences.getBoolean(Constants.INDEED, true);
                    indeedSwitch.setChecked(indeedBool);
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        deletebutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
                builder1.setMessage("Are you sure you want to delete all saved data");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                JobListRepository jobListRepository = new JobListRepository(SettingsActivity.this);
                                jobListRepository.deleteAllJobList();
                            }
                        });
                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                return true;
            }
        });
        sharebutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()

        {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                int applicationNameId = getApplicationInfo().labelRes;
                final String appPackageName = getPackageName();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(applicationNameId));
                String text = "Try Bull Sheet Generator ";
                String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
                i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
                startActivity(Intent.createChooser(i, "Share link:"));

                return true;
            }
        });
        aboutbutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()

        {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, About.class);
                startActivity(i);
                return true;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
