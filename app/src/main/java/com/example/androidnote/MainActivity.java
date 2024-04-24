package com.example.androidnote;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;
    EditText noteEditText;
    Button saveButton;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //open Database sqlite
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.open();

        calendarView = findViewById(R.id.calendarView);
        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveButton);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(calendarView.getDate()));
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        SharedPreferences pref = getPreferences(MODE_PRIVATE);

//        int savedYear = pref.getInt("year", 0);
//        if(savedYear!=0){
//            cal.set(Calendar.YEAR,savedYear);
//            cal.set(Calendar.MONTH,pref.getInt("month", 0));
//            cal.set(Calendar.DAY_OF_MONTH,pref.getInt("dayOfMonth", 0));
//            calendarView.setDate(cal.getTimeInMillis());
//        }


        date = convertDateToString(dayOfMonth, month, year);

        calendarView.setOnDateChangeListener(((view, _year, _month, _dayOfMonth) -> {
            date = convertDateToString(_dayOfMonth, _month, _year);
            noteEditText.setText("");

//            getDataFromFile(date);
            getDataFromSqlite(databaseManager, date);

//            pref.edit().putInt("year", _year).apply();
//            pref.edit().putInt("month", _month).apply();
//            pref.edit().putInt("dayOfMonth", _dayOfMonth).apply();
        }));

        saveButton.setOnClickListener(view -> {
            String textNote = noteEditText.getText().toString();

            if(textNote.isEmpty()){
                Toast.makeText(this, "Vui long nhap ghi chú", Toast.LENGTH_SHORT).show();
                return;
            }

//            saveTxtNoteToFile(textNote, date);

            addNoteToSqlite(databaseManager, textNote, date);

        });
    }

    private String convertDateToString(int dayOfMonth, int month, int year) {
        String stringDate = String.format("%02d_%02d_%04d", dayOfMonth, month + 1, year);
        return stringDate;
    }

    /// Using Local Storage ------------------------------------------------------------------
    private void saveTxtNoteToFile (String txtNote, String date) {
        try {
            // Ghi nội dung vào file
            FileOutputStream fos = openFileOutput(date, MODE_PRIVATE);
            fos.write(txtNote.getBytes());
            fos.close();
            Toast.makeText(this, "Đã lưu ghi chú", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu ghi chú", Toast.LENGTH_LONG).show();
        }
    }

    private void getDataFromFile (String stringDate){
        try {
            FileInputStream fis = openFileInput(stringDate);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fis.close();
            noteEditText.setText(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /// Using Sqlite Database ------------------------------------------------------------------
    private void addNoteToSqlite (DatabaseManager databaseManager,String txtNote, String stringDate) {
        databaseManager.addOrUpdateData(this, txtNote, stringDate);
    }

    private void getAllDataFromSqlite (DatabaseManager databaseManager){
        List<NoteModel> dataList = databaseManager.getAllData();
        for (NoteModel note : dataList) {
            Log.d("debug", "ID: " + note.id + ", Name: " + note.note + ", Date: " + note.date);
        }
    }

    private void getDataFromSqlite (DatabaseManager databaseManager, String date){
        NoteModel note = databaseManager.getNoteByDate(date);

        if (note != null) {
            // Note found, do something with it
            Log.d("debug", "Note found for date " + date + ": ID - " + note.id + ", Name - " + note.note + ", Date - " + note.date);
            // You can update UI elements or perform any other actions with the retrieved note
            noteEditText.setText(note.getNote());
        } else {
            // Note not found for the specified date
            Log.d("debug", "No note found for date " + date);
            // You may want to inform the user that there is no note for the selected date
        }

    }
}