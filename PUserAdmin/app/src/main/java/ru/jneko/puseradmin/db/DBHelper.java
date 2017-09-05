package ru.jneko.puseradmin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "PUserAdminDB1", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table ONTs (id integer primary key autoincrement, ontsn VARCHAR(20), slot integer, channel integer);");
        sqLiteDatabase.execSQL("create table ONTValues (id integer primary key autoincrement, xkey text, xvalue text, ontsn VARCHAR(20));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
