package com.example.androidnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm một dòng mới vào cơ sở dữ liệu
    public void addData(String note, String date) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NOTE, note);
        values.put(DBHelper.COLUMN_DATE, date);

        database.insert(DBHelper.TABLE_NAME, null, values);
    }

    public void addOrUpdateData(Context context, String note, String date) {
        // Check if a note already exists for the given date
        NoteModel existingNote = getNoteByDate(date);

        if (existingNote != null) {
            // If a note exists, update its content
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_NOTE, note);

            String selection = DBHelper.COLUMN_DATE + " = ?";
            String[] selectionArgs = { date };

            database.update(
                    DBHelper.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );

            Toast.makeText(context, "Đã cap nhat ghi chú", Toast.LENGTH_LONG).show();

        } else {
            // If no note exists, add a new one
            addData(note, date);
            Toast.makeText(context, "Đã luu ghi chú", Toast.LENGTH_LONG).show();
        }
    }


    // Lấy tất cả dữ liệu từ cơ sở dữ liệu
    public List<NoteModel> getAllData() {
        List<NoteModel> dataList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NOTE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DATE));

            dataList.add(new NoteModel(id, name, date));
            cursor.moveToNext();
        }
        cursor.close();
        return dataList;
    }

    public NoteModel getNoteByDate(String date) {
        NoteModel note = null;
        String selection = DBHelper.COLUMN_DATE + " = ?";
        String[] selectionArgs = { date };

        Cursor cursor = database.query(
                DBHelper.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NOTE));
            note = new NoteModel(id, name, date);
            cursor.close();
        }

        return note;
    }
}
