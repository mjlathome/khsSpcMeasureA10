package com.khs.spcmeasure.ui;

// 25 Mar 2020 AndroidX
// import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.Objects;

/**
 * Created by Mark on 07/04/2015.
 * used to define system preferences
 */

// 25 Mar 2020 AndroidX - was Activity now AppCompatActivity
public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "SettingsActivity";

    // preference keys
    public static final String KEY_PREF_SHOW_NOTIFICATIONS      = "key_pref_show_notifications";
    public static final String KEY_PREF_IN_CONTROL_AUTO_MOVE    = "key_pref_in_control_auto_move";
    public static final String KEY_PREF_IN_CONTROL_DELAY        = "key_pref_in_control_delay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show the Up button in the action bar.
        // 26 Mar 2020 - AndroidX
        // added null try/catch and Objects.requireNonNull.
        // now uses getSupportActionBar() was getActionBar()
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            throw new NullPointerException(this.toString()
                    + " getSupportActionBar() was NULL");
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                Log.d(TAG, "Home");
                finish();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
