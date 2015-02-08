package gk.nickles.ndimes.ui.actions;

import com.google.inject.Inject;

import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.ui.EventDetails;

public class SettingsAction implements EventDetailsAction{

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        activityStarter.startSettings(activity);
        return true;
    }
}
