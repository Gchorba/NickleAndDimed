package gk.nickles.ndimes.ui;

import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.inject.Inject;

import gk.nickles.ndimes.services.SharedPreferenceService;
import gk.nickles.splitty.R;
import roboguice.activity.RoboPreferenceActivity;

public class BillSplitterSettings extends RoboPreferenceActivity {

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setTitle(R.string.settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferenceService.registerTrackingEnabledListener(new SharedPreferenceService.TrackingEnabledListener() {
            @Override
            public void onTrackingEnabledChanged(boolean trackingEnabled) {
                GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(!trackingEnabled);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferenceService.unregisterTrackingEnabledListener();
    }
}
