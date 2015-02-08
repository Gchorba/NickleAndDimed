package gk.nickles.ndimes.ui.fragment;

import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.ui.adapter.EventDetailPagerAdapter;
import roboguice.fragment.RoboFragment;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class BaseEventDetailsFragment extends RoboFragment {

    private Event currentEvent;
    private EventDetailPagerAdapter pagerAdapter;

    public BaseEventDetailsFragment init(Event event, EventDetailPagerAdapter pagerAdapter){
        checkNotNull(event);
        checkNotNull(pagerAdapter);

        this.currentEvent = event;
        this.pagerAdapter = pagerAdapter;
        return this;
    }

    public void setCurrentEvent(Event event) {
        checkNotNull(event);

        this.currentEvent = event;
    }

    protected Event getCurrentEvent() {
        return currentEvent;
    }

    public void setPagerAdapter(EventDetailPagerAdapter adapter) {
        checkNotNull(pagerAdapter);

        this.pagerAdapter = adapter;
    }

    protected EventDetailPagerAdapter getPagerAdapter(){
        return this.pagerAdapter;
    }
}
