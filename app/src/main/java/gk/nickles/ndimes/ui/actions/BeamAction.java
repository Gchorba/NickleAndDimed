package gk.nickles.ndimes.ui.actions;

import com.google.inject.Inject;

import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.ui.EventDetails;

public class BeamAction implements EventDetailsAction {

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();
        if(event != null) {
            activityStarter.startBeamEvent(activity, event);
        }
        return false;
    }
}
