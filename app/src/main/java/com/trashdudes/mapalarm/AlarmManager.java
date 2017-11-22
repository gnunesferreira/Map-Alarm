package com.trashdudes.mapalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by guilhermen on 10/25/17.
 */

public class AlarmManager {

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;

    public AlarmManager(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
        this.sqLiteDatabase = this.databaseHelper.getReadableDatabase();

//        AlarmModel alarmModel = new AlarmModel(2, -22.562315, -47.420833, 1000.0, "Alarme de teste");
//        this.insertAlarm(alarmModel);
    }

    public Cursor getSelectCursor() {
        String query = "SELECT * FROM Pontos";
        Cursor cursor = this.sqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public Cursor getSelectCursor(Integer id) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Pontos WHERE _id = ?",
                new String[]{"" + id});
        return cursor;
    }

    public long insertAlarm(AlarmModel alarm){
        ContentValues values = new ContentValues();

        values.put("_id", alarm.getId());
        values.put("latitude", alarm.getLatitude());
        values.put("longitude", alarm.getLongitude());
        values.put("radius", alarm.getRadius());
        values.put("notes", alarm.getNotes());

        long result = sqLiteDatabase.insert("Pontos", null, values);
        return result;
    }

    public int updateAlarm(AlarmModel alarm) {

        ContentValues values = new ContentValues();
        values.put("latitude", alarm.getLatitude());
        values.put("longitude", alarm.getLongitude());
        values.put("radius", alarm.getRadius());
        values.put("notes", alarm.getNotes());

        int result = sqLiteDatabase.update("Pontos", values, "_id=" + alarm.getId(), null);
        return result;
    }
}
