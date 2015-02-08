package gk.nickles.ndimes.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import gk.nickles.ndimes.model.User;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.ID;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.TABLE;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(NAME);


        UUID id = UUID.fromString(cursor.getString(idIdx));
        String name = cursor.getString(nameIdx);

        return new User(id, name);
    }

    @Override
    public ContentValues getValues(User user) {
        ContentValues values = new ContentValues();
        values.put(ID, user.getId().toString());
        values.put(NAME, user.getName());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
