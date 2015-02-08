package gk.nickles.ndimes.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gk.nickles.ndimes.dataaccess.rowmapper.ExpenseRowMapper;
import gk.nickles.ndimes.model.Expense;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.OWNER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpenseStore extends BaseStore<Expense> {

    @Inject
    public ExpenseStore(ExpenseRowMapper mapper, GenericStore<Expense> genericStore) {
        super(mapper, genericStore);
    }

    public List<Expense> getExpensesOfEvent(UUID eventId) {
        checkNotNull(eventId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        return genericStore.getModelsByQuery(where);
    }

    public List<Expense> getExpensesOfEvent(UUID eventId, UUID ownerId) {
        checkNotNull(eventId);
        checkNotNull(ownerId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        where.put(OWNER, ownerId.toString());
        return genericStore.getModelsByQuery(where);
    }

    public void removeAll(UUID eventId) {
        checkNotNull(eventId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        removeAll(where);
    }

    public void removeAll(UUID eventId, UUID ownerId) {
        checkNotNull(eventId);
        checkNotNull(ownerId);

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId.toString());
        where.put(OWNER, ownerId.toString());
        removeAll(where);
    }
}