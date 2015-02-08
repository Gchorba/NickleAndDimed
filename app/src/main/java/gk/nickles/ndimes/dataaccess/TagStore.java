package gk.nickles.ndimes.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gk.nickles.ndimes.dataaccess.rowmapper.TagRowMapper;
import gk.nickles.ndimes.model.Tag;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.NAME;
import static com.google.inject.Key.get;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class TagStore extends BaseStore<Tag> {

    @Inject
    public TagStore(TagRowMapper mapper, GenericStore<Tag> genericStore) {
        super(mapper, genericStore);
    }

    public Tag getTagWithName(String name) {
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        List<Tag> tags = genericStore.getModelsByQuery(where);
        if (tags.size() == 0) {
            return null;
        } else {
            return tags.get(0);
        }
    }

    public List<Tag> getTagsWithNameLike(String name){
        checkNotNull(name);

        HashMap<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        return genericStore.getModelsByQueryWithLike(where);
    }
}
