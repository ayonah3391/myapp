package com.example.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "database";
    private static final int DB_VERSION = 1;
    public int nCount;
    private static final String TABLE_NAME = "questions";
    private static final String ID_COL = "id";
    private static final String QTEXT_COL = "question";
    private static final String ATEXT_COL = "answer";
    private static final String SETNAME_COL = "setName";
    private static final String CATEGORY_COL = "category";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " TEXT, "
                + QTEXT_COL + " TEXT,"
                + ATEXT_COL + " TEXT,"
                + SETNAME_COL + " TEXT,"
                + CATEGORY_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query

        System.out.println("ON CREATE IS BEING CALLED");
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // onUpgrade(db, DB_VERSION, DB_VERSION);
        db.execSQL(query);
    }

    public void addNewQuestion(String id, String question, String answer, String setName, String category) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_NAME + "(id, question, answer, setName, category) VALUES('" + id + "','" + question + "','" + answer + "','" + setName + "','"+ category + "')";
        // String sql = "INSERT INTO " + TABLE_NAME + "(id, question, answer, setName, category) VALUES('4', 'Q', 'A', 'SET', 'CAT');";
        // System.out.println(sql);
        // after adding all values we are passing
        // content values to our table.
        db.execSQL(sql);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    public void randomQuestion() {
        nCount = 0;
        ArrayList<String> alist;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + ";", null);
        if(cursor.moveToFirst()) {
            do {
                nCount = cursor.getInt(0);
            } while (cursor.moveToNext());
            // moves cursor to next
        }

        cursor.close();

        // length is nCount
        System.out.println(nCount);

        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
