package gk.nickles.ndimes.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gk.nickles.ndimes.dataaccess.rowmapper.UserRowMapper;
import gk.nickles.ndimes.model.User;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class UserStore extends BaseStore<User> {

    @Inject
    public UserStore(UserRowMapper mapper, GenericStore<User> genericStore) {
        super(mapper, genericStore);
    }

    public User getUserWithName(String name) {
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        List<User> users = genericStore.getModelsByQuery(where);

        if (users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }

    public List<User> getUsersWithNameLike(String name) {
        checkNotNull(name);

        Map<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        return genericStore.getModelsByQueryWithLike(where);
    }
}
