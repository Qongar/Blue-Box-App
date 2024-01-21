package com.example.blackbox;

import android.content.ContentValues;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

public class CategoryTable {

    /*
     *************************************************************************** Categories Table
     */
    public static final String CATEGORY_TABLE_NAME = "Category_Table";
    public static final String CATEGORY_ID = "Category_ID";
    public static final String CATEGORY_NAME = "Category_Name";
    public static final String CATEGORY_IMG_INDEX = "Category_ImageIndex";
    public static final String CATEGORY_ORDER = "Category_Order";
    public static final String FK_SUBCATEGORIES = "Category_fk_SubCategory";

    public static void CreateTable(SQLiteDatabase db)
    {
        String query= "CREATE TABLE " + CATEGORY_TABLE_NAME + " (" +
                CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATEGORY_NAME + " TEXT, " +
                CATEGORY_IMG_INDEX + " INTEGER, " +
                CATEGORY_ORDER + " INTEGER, " +
                SubCategory.SUBCATEGORY_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + SubCategory.SUBCATEGORY_ID + ") REFERENCES " + SubCategory.SUBCATEGORY_TABLE_NAME + "(" + SubCategory.SUBCATEGORY_ID + ")" +
                ")";

        db.execSQL(query);

        FillDatabaseDefaultValues(db);
    }

    public static void UpdateTable(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        CreateTable(db);
    }

    private static void FillDatabaseDefaultValues(SQLiteDatabase db)
    {
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_NAME, "G-mail");
        cv.put(CATEGORY_IMG_INDEX, 1);
        cv.put(CATEGORY_ORDER, 1);
        cv.put(SubCategory.SUBCATEGORY_ID, 99);

        // if 0 passed it will increase order of every data in db
        long result = db.insert(CATEGORY_TABLE_NAME, null, cv);
        //if(result == -1)
        //    Toast.makeText(DataBaseHelperSqlCipher.instance.context, "DB ERROR", Toast.LENGTH_SHORT).show();
    }
}
