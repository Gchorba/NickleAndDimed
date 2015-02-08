package gk.nickles.ndimes.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import gk.nickles.ndimes.model.Model;

public interface RowMapper<M extends Model> {

    public M map(Cursor cursor);
    public ContentValues getValues(M model);
    public String getTableName();
}
