package com.example.vacationplanner.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDao {

    @Insert
    void insertVacation(Vacation vacation);

    @Update
    void updateVacation(Vacation vacation);

    @Delete
    void deleteVacation(Vacation vacation);

    @Query("SELECT * FROM vacations")
    LiveData<List<Vacation>> getAllVacations();

    @Query("SELECT * FROM vacations WHERE id = :vacationId")
    LiveData<Vacation> getVacationById(int vacationId);

    @Query("SELECT COUNT(*) FROM excursions WHERE vacation_id = :vacationId")
    int getExcursionCountForVacation(int vacationId);

    @Query("SELECT * FROM excursions WHERE vacation_id= :vacationId")
    LiveData<List<Excursion>> getExcursionsByVacation(int vacationId);
}
