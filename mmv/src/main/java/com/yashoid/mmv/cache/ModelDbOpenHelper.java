package com.yashoid.mmv.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ModelDbOpenHelper extends SQLiteOpenHelper implements ModelCacheColumns {

    private static final String NAME = "com_yashoid_mmv_cache";
    private static final int VERSION = 1;

    private static ModelDbOpenHelper mInstance = null;

    protected static ModelDbOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ModelDbOpenHelper(context);
        }

        return mInstance;
    }

    private ModelDbOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder createPhrase = new StringBuilder();

        createPhrase.append("CREATE TABLE ").append(TABLE_NAME).append("( ");
        createPhrase.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        createPhrase.append(FEATURES).append(" TEXT )");

        db.execSQL(createPhrase.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
