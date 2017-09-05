package ru.mew_hpm.gpontools_v3.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "eltexv3manager", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table sshServers ("
                + "id integer primary key autoincrement,"
                + "userText text,"
                + "host text,"
                + "port text,"
                + "username text,"
                + "password text,"
                + "initDir text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
