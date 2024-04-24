package com.example.androidnote.RoomPersistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface  NoteDao {
    @Insert
    void insert(NoteEntity note);

    @Query("SELECT * FROM NoteEntity WHERE date = :date")
    NoteEntity getByDate(String date);

    @Update
    void update(NoteEntity note);

    @Delete
    void delete(NoteEntity note);
}
