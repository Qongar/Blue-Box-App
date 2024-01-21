package com.example.blackbox;


import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class AccountTable {

    /*
     **************************************************************************** Accounts Table
     */
    public static final String ACCOUNT_TABLE_NAME = "Account_Table";
    public static final String ACCOUNT_ID = "Account_ID";
    public static final String ACCOUNT_USERNAME = "Account_Username";
    public static final String ACCOUNT_PASSWORD = "Account_Password";
    public static final String COLUMN_IMG_INDEX = "CATEGORY_INDEX";
    public static final String COLUMN_TITLE = "CATEGORY_TITLE";
    public static final String COLUMN_ORDER = "CATEGORY_ORDER";

    public static void CreateTable(SQLiteDatabase db)
    {
        String query= "CREATE TABLE " + ACCOUNT_TABLE_NAME + " (" +
                ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_USERNAME + " TEXT, " +
                ACCOUNT_PASSWORD + " TEXT, " +
                COLUMN_IMG_INDEX + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ORDER + " INTEGER, " +
                CategoryTable.CATEGORY_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + CategoryTable.CATEGORY_ID + ") REFERENCES " + CategoryTable.CATEGORY_TABLE_NAME + "(" + CategoryTable.CATEGORY_ID + ")" +
                ")";

        db.execSQL(query);
    }

    public static void UpdateTable(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        CreateTable(db);
    }

    public static void AddAccount(CardModel cardModel, Context context) {
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        ContentValues cv = new ContentValues();

        cv.put(ACCOUNT_USERNAME, cardModel.getAcc());
        cv.put(ACCOUNT_PASSWORD, cardModel.getPass());
        cv.put(COLUMN_IMG_INDEX, cardModel.getImg());
        cv.put(COLUMN_TITLE, cardModel.getTitle());
        cv.put(COLUMN_ORDER, 1);

        // if 0 passed it will increase order of every data in db
        FixOrder(0, context);
        long result = db.insert(ACCOUNT_TABLE_NAME, null, cv);
        if(result == -1)
            Toast.makeText(context, "DB ERROR", Toast.LENGTH_SHORT).show();
    }

    // sort by DESC, decrease order by 1 until cursor comes to the cardModel that needs to be deleted, then stop
    public static void DeleteAccount(CardModel cardModel, Context context){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " ORDER BY " + COLUMN_ORDER + " DESC";

        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getInt(5) == cardModel.getOrder()){
                        long result = db.delete(ACCOUNT_TABLE_NAME, ACCOUNT_ID +"="+cardModel.getId(), null);
                        if(result == -1)
                            Toast.makeText(context, "(DB)FAILED TO DELETE", Toast.LENGTH_SHORT).show();
                        cursor.close();
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
    }

    public static void UpdateAccount(CardModel cardModel, Context context) {
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        ContentValues cv = new ContentValues();

        //String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MAIL_ID + "='" + cardModel.getId() +"'";

        cv.put(ACCOUNT_USERNAME, cardModel.getAcc());
        cv.put(ACCOUNT_PASSWORD, cardModel.getPass());  //needs to be encrypted
        //cv.put(COLUMN_IMG_INDEX, cardModel.getImg()); //ToDo:works?
        cv.put(COLUMN_TITLE, cardModel.getTitle());
        FixOrder(0, context);
        cv.put(COLUMN_ORDER, 1);

        //Cursor cursor = db.rawQuery()
        long result = db.update(ACCOUNT_TABLE_NAME, cv, ACCOUNT_ID +"="+cardModel.getId(), null);
        if(result == -1)
            Toast.makeText(context, "UPDATE ERROR", Toast.LENGTH_SHORT).show();
    }

    // increase order in DB, db sorted by Order
    public static void FixOrder(int order, Context context) {

        // sorted by ASC
        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " ORDER BY " + COLUMN_ORDER + " ASC";
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);
        Cursor cursor = db.rawQuery(queryString, null);
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
    }

    // get all data from database
    public static ArrayList<CardModel> GetAllCards(Context context){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME;

        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);

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
                String cardSalt = cursor.getString(6);
                String cardIv = cursor.getString(7);

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
    public static ArrayList<CardModel> GetByCategory(int catIndex, Context context){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " + COLUMN_IMG_INDEX + "='" + catIndex + "'";
        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase(MainActivity.UserPassword);

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
                String cardSalt = cursor.getString(6);
                String cardIv = cursor.getString(7);

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
}
