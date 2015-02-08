package gk.nickles.ndimes.ui.actions;

import android.widget.Toast;

import com.google.inject.Inject;

import java.util.UUID;

import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.services.SharedPreferenceService;
import gk.nickles.ndimes.ui.EventDetails;
import gk.nickles.splitty.R;

public class EditEventAction implements EventDetailsAction {
    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();

        UUID userId = sharedPreferenceService.getUserId();

        if (userId.equals(event.getOwnerId())) {
            activityStarter.startEditEvent(activity, event);
        }
        else {
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.access_denied_event), Toast.LENGTH_LONG);
            toast.show();
        }

        return true;
    }
}
