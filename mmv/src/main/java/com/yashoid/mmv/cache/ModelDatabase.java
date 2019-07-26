package com.yashoid.mmv.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModelDatabase implements ModelCacheColumns {

    private ModelDbOpenHelper mDbOpenHelper;
    private ColumnNameMap mColumnNameMap;

    protected ModelDatabase(Context context) {
        mDbOpenHelper = ModelDbOpenHelper.getInstance(context);
        mColumnNameMap = ColumnNameMap.getInstance(context);
    }

    protected void addModel(HashMap<String, String> identifiers, Map<String, Object> features) {
        ContentValues cv = new ContentValues(identifiers.size() + 1);

        for (Map.Entry<String, String> identifier: identifiers.entrySet()) {
            cv.put(mColumnNameMap.getColumnName(identifier.getKey()), identifier.getValue());
        }

        cv.put(FEATURES, ValueMapper.fromValue(features).toString());

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        db.insert(TABLE_NAME, null, cv);
    }

    protected void updateModel(HashMap<String, String> identifiers, Map<String, Object> features) {
        SelectionInfo selectionInfo = new SelectionInfo(identifiers);

        if (selectionInfo.selection == null) {
            return;
        }

        ContentValues cv = new ContentValues(1);
        cv.put(FEATURES, ValueMapper.fromValue(features).toString());

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

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
        SelectionInfo selectionInfo = new SelectionInfo(identifiers);

        if (selectionInfo.selection == null) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        db.delete(TABLE_NAME, selectionInfo.selection, selectionInfo.selectionArgs);
    }

    protected HashMap<String, Object> queryModel(HashMap<String, String> identifiers) {
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

        HashMap<String, Object> model = convertModel(identifiers, jFeatures);

        cursor.close();

        return model;
    }

    private Cursor getCursor(HashMap<String, String> identifiers) {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        SelectionInfo selectionInfo = new SelectionInfo(identifiers);

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

    private HashMap<String, Object> convertModel(HashMap<String, String> identifiers, JSONObject jFeatures) {
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

    private Object convertValue(Object rawValue) {
        return ValueMapper.toValue(rawValue);
    }

    private class SelectionInfo {

        private String selection = null;
        private String[] selectionArgs = null;

        protected SelectionInfo(HashMap<String, String> identifiers) {
            Iterator<Map.Entry<String, String>> identifierIterator = identifiers.entrySet().iterator();

            StringBuilder selectionBuilder = new StringBuilder();
            List<String> selectionArgsList = new ArrayList<>(identifiers.size());

            while (identifierIterator.hasNext()) {
                Map.Entry<String, String> identifier = identifierIterator.next();

                String columnName = mColumnNameMap.getColumnName(identifier.getKey());

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
