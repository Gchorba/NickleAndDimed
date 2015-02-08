package gk.nickles.ndimes.ui.actions;

import com.google.inject.Inject;

import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.ui.EventDetails;
import gk.nickles.splitty.R;

public class AddExpenseAction implements EventDetailsAction {

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();
        if(event != null) {
            // Ensure we go to the Expenses tab when we return
            activity.setCurrentTab(activity.getTabPosition(activity.getString(R.string.expenses)));
            activityStarter.startAddExpense(activity, event);
        }
        return true;
    }
}
