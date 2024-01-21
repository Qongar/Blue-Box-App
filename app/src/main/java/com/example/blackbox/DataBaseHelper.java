package com.example.blackbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "block.db";
    private Context context;



    /*
    *************************************************************************** SubCategories Table
    */
    private static final String SUBCATEGORY_TABLE_NAME = "SubCategory_Table";
    private static final String SUBCATEGORY_ID = "SubCategory_ID";
    private static final String SUBCATEGORY_NAME = "SubCategory_Name";
    /*
    *************************************************************************** Categories Table
    */
    private static final String CATEGORY_TABLE_NAME = "Category_Table";
    private static final String CATEGORY_ID = "Category_ID";
    private static final String CATEGORY_NAME = "Category_Name";
    private static final String CATEGORY_IMG_INDEX = "Category_ImageIndex";
    private static final String CATEGORY_ORDER = "Category_Order";
    private static final String CATEGORY_SUBCATEGORIES = SUBCATEGORY_ID;
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
    private static final String ACCOUNT_CATEGORY = CATEGORY_ID;


    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query= "CREATE TABLE " + ACCOUNT_TABLE_NAME + " (" +
                ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_USERNAME + " TEXT, " +
                ACCOUNT_PASSWORD + " TEXT, " +
                COLUMN_IMG_INDEX + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ORDER + " INTEGER" +
                ")";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME);
        onCreate(db);
    }

    void addAcc(CardModel cardModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ACCOUNT_USERNAME, cardModel.getAcc());
        cv.put(ACCOUNT_PASSWORD, cardModel.getPass());
        cv.put(COLUMN_IMG_INDEX, cardModel.getImg());
        cv.put(COLUMN_TITLE, cardModel.getTitle());
        cv.put(COLUMN_ORDER, 1);

        // if 0 passed it will increase order of every data in db
        fixOrder(0);
        long result = db.insert(ACCOUNT_TABLE_NAME, null, cv);
        if(result == -1)
            Toast.makeText(context, "DB ERROR", Toast.LENGTH_SHORT).show();
    }

    // sort by DESC, decrease order by 1 until cursor comes to the cardModel that needs to be deleted, then stop
    void deleteAcc(CardModel cardModel){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " ORDER BY " + COLUMN_ORDER + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
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

    void updateAcc(CardModel cardModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MAIL_ID + "='" + cardModel.getId() +"'";

        cv.put(ACCOUNT_USERNAME, cardModel.getAcc());
        cv.put(ACCOUNT_PASSWORD, cardModel.getPass());  //needs to be encrypted
        //cv.put(COLUMN_IMG_INDEX, cardModel.getImg()); //ToDo:works?
        cv.put(COLUMN_TITLE, cardModel.getTitle());
        fixOrder(0);
        cv.put(COLUMN_ORDER, 1);

        //Cursor cursor = db.rawQuery()
        long result = db.update(ACCOUNT_TABLE_NAME, cv, ACCOUNT_ID +"="+cardModel.getId(), null);
        if(result == -1)
            Toast.makeText(context, "UPDATE ERROR", Toast.LENGTH_SHORT).show();
    }

    // increase order in DB, db sorted by Order
    void fixOrder(int order) {

        // sorted by ASC
        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " ORDER BY " + COLUMN_ORDER + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
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

    // get one column, return encrypted password
    public String getPassword(int id){
        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " + ACCOUNT_ID + "='" + id +"'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        String encrypted = "CURSOR FAILED";
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    encrypted = cursor.getString(2);
                }while (cursor.moveToNext());
            }
        }else{
            // db empty dont do anything
        }
        cursor.close();
        db.close();
        return encrypted;
    }

    // get all data from database
    public ArrayList<CardModel> getAllCards(){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

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
        return new ArrayList<>();
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        File dir = new File(mcoContext.getFilesDir(), "mydir");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void s()
    {
        //FileOutputStream fOut = openFileOutput("file name",Context.MODE_PRIVATE);
    }

    // get all data from database by Category
    public ArrayList<CardModel> getByCategory(int catIndex){

        String queryString = "SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " + COLUMN_IMG_INDEX + "='" + catIndex + "'";
        SQLiteDatabase db = this.getReadableDatabase();

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
}
