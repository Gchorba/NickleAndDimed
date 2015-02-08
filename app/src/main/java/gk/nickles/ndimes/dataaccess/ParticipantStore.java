package gk.nickles.ndimes.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gk.nickles.ndimes.dataaccess.rowmapper.ParticipantRowMapper;
import gk.nickles.ndimes.model.Participant;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkState;

public class ParticipantStore extends BaseStore<Participant> {

    @Inject
    private UserStore userStore;

    @Inject
    public ParticipantStore(ParticipantRowMapper mapper, GenericStore<Participant> genericStore) {
        super(mapper, genericStore);/**/
    }

    public List<Participant> getParticipants(UUID eventId) {
        checkNotNull(eventId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        List<Participant> participants = genericStore.getModelsByQuery(where);

        return participants;
    }

    public List<Participant> getParticipantsForUsers(UUID userId) {
        checkNotNull(userId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(USER, userId.toString());

        return genericStore.getModelsByQuery(where);
    }

    public Participant getParticipant(UUID eventId, UUID userId) {
        checkNotNull(eventId);
        checkNotNull(userId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        where.put(USER, userId.toString());
        List<Participant> participants = genericStore.getModelsByQuery(where);

        checkState(participants.size() <= 1);

        return participants.size() > 0 ? participants.get(0) : null;
    }

    public void removeAll(UUID eventId) {
        checkNotNull(eventId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        genericStore.removeAll(where);
    }

    public void removeBy(UUID eventId, UUID userId) {
        checkNotNull(eventId);
        checkNotNull(userId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        where.put(USER, userId.toString());
        removeAll(where);
    }
}
