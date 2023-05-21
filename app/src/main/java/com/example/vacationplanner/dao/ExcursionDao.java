package com.example.vacationplanner.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vacationplanner.entities.Excursion;

@Dao
public interface ExcursionDao {

    @Insert
    void insertExcursion(Excursion excursion);

    @Update
    void updateExcursion(Excursion excursion);

    @Delete
    void deleteExcursion(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE id = :excursionId")
    LiveData<Excursion> getExcursionById(int excursionId);
}
