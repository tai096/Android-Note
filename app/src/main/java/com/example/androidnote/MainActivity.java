package com.example.androidnote;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;
    EditText noteEditText;
    Button saveButton;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveButton);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(calendarView.getDate()));
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        date = convertDateToString(dayOfMonth, month, year);

        calendarView.setOnDateChangeListener(((view, _year, _month, _dayOfMonth) -> {
            date = convertDateToString(_dayOfMonth, _month, _year);
        }));

        saveButton.setOnClickListener(view -> {
            String textNote = noteEditText.getText().toString();
            saveTxtNoteToFile(textNote, date);
        });
    }

    private String convertDateToString(int dayOfMonth, int month, int year) {
        String stringDate = String.format("%02d_%02d_%04d", dayOfMonth, month + 1, year);
        return stringDate;
    }

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

}