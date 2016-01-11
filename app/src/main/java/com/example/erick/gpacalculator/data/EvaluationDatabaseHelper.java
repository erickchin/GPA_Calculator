package com.example.erick.gpacalculator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by erick on 2016-01-07.
 */
public class EvaluationDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "evaluations.db";
    public static final String TABLE_NAME = "evaluations_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "CODE";
    public static final String COL_4 = "MARK";
    public static final String COL_5 = "WEIGHT";


    public EvaluationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlString = "CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2 + " TEXT," + COL_3 + " TEXT," + COL_4 +" DOUBLE,"+ COL_5 + " DOUBLE)";
        db.execSQL(sqlString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertEvaluation(String name, String code, double mark, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, code);
        contentValues.put(COL_4, mark);
        contentValues.put(COL_5, weight);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public Integer deleteData(String name, String code, String mark, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_2 + " = ? AND " + COL_3 + " = ? AND " + COL_4 + " = ? AND " + COL_5 + " = ?", new String[] {name, code, mark, weight});
    }

    public void deleteAllCourseCode(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_3 + " = '" + code + "'");
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
