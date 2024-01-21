package com.example.blackbox;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class DataBaseHelperSqlCipher extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE_NAME = "block.db";

    /*
     **************************************************************************** Accounts Table
     */
    private static final String ACCOUNT_TABLE_NAME = "Account_Table";
    private static final String ACCOUNT_ID = "Account_ID";
    private static final String ACCOUNT_USERNAME = "Account_Username";
    private static final String ACCOUNT_PASSWORD = "Account_Password";
    private static final String COLUMN_IMG_INDEX = "CATEGORY_INDEX";
    private static final String COLUMN_TITLE = "CATEGORY_TITLE";
    private static final String COLUMN_ORDER = "CATEGORY_ORDER";

    public DataBaseHelperSqlCipher(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        String query= "CREATE TABLE " + ACCOUNT_TABLE_NAME + " (" +
                ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_USERNAME + " TEXT, " +
                ACCOUNT_PASSWORD + " TEXT, " +
                COLUMN_IMG_INDEX + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ORDER + " INTEGER" +
                ")";

        db.execSQL(query);
        FillDatabaseDefaultValues(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        onCreate(db);
    }

    public boolean CheckPassword(){
        try
        {
            SQLiteDatabase db = this.getWritableDatabase(MainActivity.UserPassword);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void AddAccount(CardModel cardModel) {
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        ContentValues cv = new ContentValues();

        cv.put(ACCOUNT_USERNAME, cardModel.getAcc());
        cv.put(ACCOUNT_PASSWORD, cardModel.getPass());
        cv.put(COLUMN_IMG_INDEX, cardModel.getImg());
        cv.put(COLUMN_TITLE, cardModel.getTitle());
        cv.put(COLUMN_ORDER, 1);

        // if 0 passed it will increase order of every data in db
        FixOrder(0);
        long result = db.insert(ACCOUNT_TABLE_NAME, null, cv);
        if(result == -1)
            Toast.makeText(context, "DB ERROR", Toast.LENGTH_SHORT).show();

        db.close();
        //dbHelper.close();
    }

    // sort by DESC, decrease order by 1 until cursor comes to the cardModel that needs to be deleted, then stop
    public void DeleteAccount(CardModel cardModel){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " ORDER BY " + COLUMN_ORDER + " DESC";

        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        net.sqlcipher.Cursor cursor = db.rawQuery(queryString, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getInt(5) == cardModel.getOrder()){
                        long result = db.delete(ACCOUNT_TABLE_NAME, ACCOUNT_ID +"="+cardModel.getId(), null);
                        if(result == -1)
                            Toast.makeText(context, "(DB)FAILED TO DELETE", Toast.LENGTH_SHORT).show();
                        cursor.close();
                        db.close();
                        return;
                    }else{
                        //ToDO: might try to update id too, don't know if it's possible tho
                        String queryString2 = "UPDATE " + ACCOUNT_TABLE_NAME + " SET " + COLUMN_ORDER + "='" + (cursor.getInt(5)-1) + "' WHERE " + ACCOUNT_ID + "='" + cursor.getInt(0) + "'";
                        db.execSQL(queryString2);
                    }
                } while (cursor.moveToNext());
            } else {
                // db empty dont do anything
            }
        }
        cursor.close();
        db.close();
    }

    public void UpdateAccount(CardModel cardModel) {
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        ContentValues cv = new ContentValues();

        //String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MAIL_ID + "='" + cardModel.getId() +"'";

        cv.put(ACCOUNT_USERNAME, cardModel.getAcc());
        cv.put(ACCOUNT_PASSWORD, cardModel.getPass());  //needs to be encrypted
        //cv.put(COLUMN_IMG_INDEX, cardModel.getImg()); //ToDo:works?
        cv.put(COLUMN_TITLE, cardModel.getTitle());
        FixOrder(0);
        cv.put(COLUMN_ORDER, 1);

        //Cursor cursor = db.rawQuery()
        long result = db.update(ACCOUNT_TABLE_NAME, cv, ACCOUNT_ID +"="+cardModel.getId(), null);
        if(result == -1)
            Toast.makeText(context, "UPDATE ERROR", Toast.LENGTH_SHORT).show();
        db.close();
    }

    // increase order in DB, db sorted by Order
    public void FixOrder(int order) {

        // sorted by ASC
        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " ORDER BY " + COLUMN_ORDER + " ASC";
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        net.sqlcipher.Cursor cursor = db.rawQuery(queryString, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String queryString2 = "UPDATE " + ACCOUNT_TABLE_NAME + " SET " + COLUMN_ORDER + "='" + (cursor.getInt(5)+1) + "' WHERE " + ACCOUNT_ID + "='" + cursor.getInt(0) + "'";
                    db.execSQL(queryString2);
                    if(cursor.getInt(5) == order){
                        db.execSQL("UPDATE " + ACCOUNT_TABLE_NAME + " SET " + COLUMN_ORDER + "='" + 1 + "' WHERE " + ACCOUNT_ID + "='" + cursor.getInt(0) + "'");
                        cursor.close();
                        return;
                    }
                } while (cursor.moveToNext());
            } else {
                // db empty dont do anything
            }
        }

        cursor.close();
        db.close();
    }

    // get all data from database
    public ArrayList<CardModel> GetAllCards(){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME;

        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        ArrayList<CardModel> cards = new ArrayList<>();

        net.sqlcipher.Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst())
        {
            do {
                int cardID = cursor.getInt(0);
                String cardAccount = cursor.getString(1);
                String cardPassword = cursor.getString(2);
                Integer cardImage = Integer.parseInt(cursor.getString(3));
                String cardTitle = cursor.getString(4);
                Integer cardOrder = cursor.getInt(5);

                CardModel cardModel = new CardModel();
                cardModel.setId(cardID);
                cardModel.setAcc(cardAccount);
                cardModel.setPass(cardPassword);
                cardModel.setImg(cardImage);
                cardModel.setTitle(cardTitle);
                cardModel.setOrder(cardOrder);
                cards.add(cardModel);

            }while (cursor.moveToNext());
        }
        else{
            // db empty dont do anything
        }

        cursor.close();
        db.close();
        return cards;
    }

    // get all data from database by Category
    public ArrayList<CardModel> GetByCategory(int catIndex){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " + COLUMN_IMG_INDEX + "='" + catIndex + "'";
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        db.rawExecSQL("PRAGMA cipher_memory_security = OFF");

        ArrayList<CardModel> cards = new ArrayList<>();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst())
        {
            do {
                int cardID = cursor.getInt(0);
                String cardAccount = cursor.getString(1);
                String cardPassword = cursor.getString(2);
                Integer cardImage = Integer.parseInt(cursor.getString(3));
                String cardTitle = cursor.getString(4);
                Integer cardOrder = cursor.getInt(5);

                CardModel cardModel = new CardModel();
                cardModel.setId(cardID);
                cardModel.setAcc(cardAccount);
                cardModel.setPass(cardPassword);
                cardModel.setImg(cardImage);
                cardModel.setTitle(cardTitle);
                cardModel.setOrder(cardOrder);
                cards.add(cardModel);

            }while (cursor.moveToNext());
        }
        else{
            // db empty dont do anything
        }

        cursor.close();
        db.close();
        return cards;
    }

    private void FillDatabaseDefaultValues(SQLiteDatabase db)
    {
        ContentValues cv = new ContentValues();

        cv.put(ACCOUNT_USERNAME, "Username");
        cv.put(ACCOUNT_PASSWORD, "Password");
        cv.put(COLUMN_IMG_INDEX, 0);
        cv.put(COLUMN_TITLE, "Google");
        cv.put(COLUMN_ORDER, 1);

        // if 0 passed it will increase order of every data in db
        long result = db.insert(ACCOUNT_TABLE_NAME, null, cv);
        if(result == -1)
            Toast.makeText(context, "DB ERROR", Toast.LENGTH_SHORT).show();

        //db.close();
    }
}

/*
package com.example.blackbox;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBaseHelperSqlCipher extends SQLiteOpenHelper {

    public static DataBaseHelperSqlCipher instance;
    public Context context;

    private static final String DATABASE_NAME = "block.db";

    public DataBaseHelperSqlCipher(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        if(instance == null)
            instance = this;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SubCategory.CreateTable(db);
        //CategoryTable.CreateTable(db);
        AccountTable.CreateTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //SubCategory.UpdateTable(db);
        //CategoryTable.UpdateTable(db);
        AccountTable.UpdateTable(db);
    }

    public boolean CheckPassword(){
        try
        {
            this.getWritableDatabase(MainActivity.UserPassword);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}

 */
