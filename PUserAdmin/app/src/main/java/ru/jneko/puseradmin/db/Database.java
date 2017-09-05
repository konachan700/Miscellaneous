package ru.jneko.puseradmin.db;

import android.content.Context;

public class Database {
    private static Database db = null;
    private DBHelper dbHelper;

    private Database(Context c) {
        dbHelper = new DBHelper(c);
    }







    public static void init(Context c) {
        if (db == null) db = new Database(c);
    }
}
