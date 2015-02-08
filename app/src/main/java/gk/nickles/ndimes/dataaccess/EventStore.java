package gk.nickles.ndimes.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gk.nickles.ndimes.dataaccess.rowmapper.EventRowMapper;
import gk.nickles.ndimes.model.Event;

import static com.google.inject.Key.get;

@Singleton
public class EventStore extends BaseStore<Event> {

    @Inject
    public EventStore(EventRowMapper mapper, GenericStore<Event> genericStore) {
        super(mapper, genericStore);
    }
}
