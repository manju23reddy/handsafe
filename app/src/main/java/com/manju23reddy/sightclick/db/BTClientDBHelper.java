package com.manju23reddy.sightclick.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MReddy3 on 2/5/2018.
 */

public class BTClientDBHelper extends SQLiteOpenHelper {


    public BTClientDBHelper(Context context){
        super(context, BTClientDBContract.DATABASE_NAME, null,
                BTClientDBContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String create_schema : BTClientDBContract.getCreateTableSchemas()){
            db.execSQL(create_schema);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
