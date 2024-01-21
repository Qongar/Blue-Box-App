package com.example.blackbox;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

public class SubCategory {

    /*
     *************************************************************************** SubCategories Table
     */
    public static final String SUBCATEGORY_TABLE_NAME = "SubCategory_Table";
    public static final String SUBCATEGORY_ID = "SubCategory_ID";
    public static final String SUBCATEGORY_NAME = "SubCategory_Name";

    public static void CreateTable(SQLiteDatabase db)
    {
        String query= "CREATE TABLE " + SUBCATEGORY_TABLE_NAME + " (" +
                SUBCATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SUBCATEGORY_NAME + " TEXT " +
                ")";

        db.execSQL(query);

        FillDatabaseDefaultValues(db);
    }

    public static void UpdateTable(SQLiteDatabase db, Context context)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SUBCATEGORY_TABLE_NAME);
        CreateTable(db);
    }

    private static void FillDatabaseDefaultValues(SQLiteDatabase db)
    {
        ContentValues cv = new ContentValues();

        for(int i = 0; i < 5; i++)
        {
            cv.put(SUBCATEGORY_NAME, "G-mail");

            //if 0 passed it will increase order of every data in db
            long result = db.insert(SUBCATEGORY_TABLE_NAME, null, cv);
        }
    }
}
