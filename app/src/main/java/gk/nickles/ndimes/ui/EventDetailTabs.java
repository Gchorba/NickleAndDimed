package gk.nickles.ndimes.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import java.util.List;

import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.ui.adapter.EventDetailPagerAdapter;
import gk.nickles.ndimes.ui.fragment.DebtsFragment;
import gk.nickles.ndimes.ui.fragment.ExpensesFragment;
import gk.nickles.ndimes.ui.fragment.ParticipantsFragment;
import gk.nickles.splitty.R;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static roboguice.RoboGuice.getInjector;

public class EventDetailTabs {

    @Inject
    private Context context;

    private Event event;

    private List<String> labels;

    public EventDetailTabs init(Event event) {
        checkNotNull(event);
        this.event = event;

        labels = asList(
                context.getString(R.string.overview),
                context.getString(R.string.expenses),
                context.getString(R.string.participants));

        return this;
    }

    public int getTabPosition(String label) {
        return labels.indexOf(label);
    }

    public Event getEvent() {
        return event;
    }

    public String getLabel(int position) {
        return labels.get(position);
    }

    public Fragment getFragment(int position, EventDetailPagerAdapter pagerAdapter) {
        checkNotNull(event);
        switch (position) {
            case 0:
                return getInjector(context).getInstance(DebtsFragment.class).init(event, pagerAdapter);
            case 1:
                return getInjector(context).getInstance(ExpensesFragment.class).init(event, pagerAdapter);
            case 2:
                return getInjector(context).getInstance(ParticipantsFragment.class).init(event, pagerAdapter);
            default:
                throw new IllegalArgumentException("position");
        }
    }

    public int numTabs() {
        return 3;
    }
}
