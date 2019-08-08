package com.yashoid.mmv.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModelDatabase implements ModelCacheColumns {

    private static final String TAG = "ModelDatabase";

    private ModelDbOpenHelper mDbOpenHelper;
    private ColumnNameMap mColumnNameMap;

    protected ModelDatabase(Context context) {
        mDbOpenHelper = ModelDbOpenHelper.getInstance(context);
        mColumnNameMap = ColumnNameMap.getInstance(context);
    }

    protected void addModel(HashMap<String, String> identifiers, Map<String, Object> features) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        ContentValues cv = new ContentValues(identifiers.size() + 1);

        for (Map.Entry<String, String> identifier: identifiers.entrySet()) {
            String columnName = getColumnName(identifier.getKey(), db);
            cv.put(columnName, identifier.getValue());
        }

        cv.put(FEATURES, ValueMapper.fromValue(features).toString());


        db.insert(TABLE_NAME, null, cv);
    }

    private String getColumnName(String identifier, SQLiteDatabase db) {
        String columnName = mColumnNameMap.getColumnName(identifier);

        if (columnName == null) {
            columnName = mColumnNameMap.defineNewColumn(identifier);

            addColumn(columnName, db);
        }

        return columnName;
    }

    private void addColumn(String columnName, SQLiteDatabase db) {
        String sql = new StringBuilder()
                .append("ALTER TABLE ")
                .append(TABLE_NAME)
                .append(" ADD ")
                .append(columnName)
                .append(" TEXT")
                .toString();

        db.execSQL(sql);
    }

    protected void updateModel(HashMap<String, String> identifiers, Map<String, Object> features) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        SelectionInfo selectionInfo = new SelectionInfo(identifiers, db);

        if (selectionInfo.selection == null) {
            return;
        }

        ContentValues cv = new ContentValues(1);
        cv.put(FEATURES, ValueMapper.fromValue(features).toString());

        db.update(TABLE_NAME, cv, selectionInfo.selection, selectionInfo.selectionArgs);
    }

    protected boolean modelExists(HashMap<String, String> identifiers) {
        Cursor cursor = getCursor(identifiers);

        if (cursor == null) {
            return false;
        }

        cursor.close();

        return true;
    }

    protected void deleteModel(HashMap<String, String> identifiers) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        SelectionInfo selectionInfo = new SelectionInfo(identifiers, db);

        if (selectionInfo.selection == null) {
            return;
        }

        db.delete(TABLE_NAME, selectionInfo.selection, selectionInfo.selectionArgs);
    }

    protected HashMap<String, Object> queryModel(HashMap<String, String> identifiers) {
        if ("3".equals(identifiers.get("id"))) {
            Log.d("AAA", "About to get Ramin!");
        }
        Cursor cursor = getCursor(identifiers);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        JSONObject jFeatures;

        try {
            jFeatures = new JSONObject(cursor.getString(cursor.getColumnIndex(FEATURES)));
        } catch (JSONException e) {
            throw new RuntimeException("Features is not in JSON format.");
        }

        HashMap<String, Object> model = null;

        try {
            model = convertModel(identifiers, jFeatures);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to convert query result to model.", e);
        }

        cursor.close();

        return model;
    }

    private Cursor getCursor(HashMap<String, String> identifiers) {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        SelectionInfo selectionInfo = new SelectionInfo(identifiers, db);

        String selection = selectionInfo.selection;
        String[] selectionArgs = selectionInfo.selectionArgs;

        if (selection == null) {
            return null;
        }

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null, "1");

        if (cursor.getCount() == 0) {
            cursor.close();

            return null;
        }

        return cursor;
    }

    private HashMap<String, Object> convertModel(HashMap<String, String> identifiers, JSONObject jFeatures) throws JSONException {
        HashMap<String, Object> model = new HashMap<>(identifiers.size() + jFeatures.length());

        for (Map.Entry<String, String> identifier: identifiers.entrySet()) {
            model.put(identifier.getKey(), convertValue(identifier.getValue()));
        }

        Iterator<String> keys = jFeatures.keys();

        while (keys.hasNext()) {
            String key = keys.next();

            try {
                model.put(key, convertValue(jFeatures.get(key)));
            } catch (JSONException e) { }
        }

        return model;
    }

    private Object convertValue(Object rawValue) throws JSONException {
        return ValueMapper.toValue(rawValue);
    }

    private class SelectionInfo {

        private String selection = null;
        private String[] selectionArgs = null;

        protected SelectionInfo(HashMap<String, String> identifiers, SQLiteDatabase db) {
            Iterator<Map.Entry<String, String>> identifierIterator = identifiers.entrySet().iterator();

            StringBuilder selectionBuilder = new StringBuilder();
            List<String> selectionArgsList = new ArrayList<>(identifiers.size());

            while (identifierIterator.hasNext()) {
                Map.Entry<String, String> identifier = identifierIterator.next();

                String columnName = getColumnName(identifier.getKey(), db);

                if (columnName == null) {
                    return;
                }

                selectionBuilder.append(columnName).append("=? AND ");

                selectionArgsList.add(identifier.getValue());
            }

            selectionBuilder.append("1>0");

            selection = selectionBuilder.toString();
            selectionArgs = selectionArgsList.toArray(new String[0]);
        }

    }

}
