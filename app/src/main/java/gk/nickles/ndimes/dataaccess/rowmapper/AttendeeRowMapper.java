package gk.nickles.ndimes.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import gk.nickles.ndimes.model.Attendee;

import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.TABLE;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.PARTICIPANT;
import static gk.nickles.ndimes.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class AttendeeRowMapper implements RowMapper<Attendee> {
    @Override
    public Attendee map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int expenseIdx = cursor.getColumnIndex(EXPENSE);
        int participantIdx = cursor.getColumnIndex(PARTICIPANT);

        UUID id = UUID.fromString(cursor.getString(idIdx));
        UUID expense = UUID.fromString(cursor.getString(expenseIdx));
        UUID participant = UUID.fromString(cursor.getString(participantIdx));

        return new Attendee(id, expense, participant);
    }

    @Override
    public ContentValues getValues(Attendee attendee) {
        ContentValues values = new ContentValues();
        values.put(ID, attendee.getId().toString());
        values.put(EXPENSE, attendee.getExpense().toString());
        values.put(PARTICIPANT, attendee.getParticipant().toString());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
