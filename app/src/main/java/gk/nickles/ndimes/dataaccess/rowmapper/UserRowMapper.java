package gk.nickles.ndimes.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import gk.nickles.ndimes.model.User;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.ID;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.PHONE;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.TABLE;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.EMAIL;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(NAME);
        int emailIdx = cursor.getColumnIndex(EMAIL);
        int phoneIdx = cursor.getColumnIndex(PHONE);

        UUID id = UUID.fromString(cursor.getString(idIdx));
        String name = cursor.getString(nameIdx);
        String email = cursor.getString(emailIdx);
        String phone = cursor.getString(phoneIdx);

        return new User(id, name, email, phone);
    }

    @Override
    public ContentValues getValues(User user) {
        ContentValues values = new ContentValues();
        values.put(ID, user.getId().toString());
        values.put(NAME, user.getName());
        values.put(PHONE, user.getPhone());
        values.put(EMAIL, user.getEmail());
        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
