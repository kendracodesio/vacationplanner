package com.example.vacationplanner.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.vacationplanner.dao.ExcursionDao;
import com.example.vacationplanner.dao.VacationDao;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class VacationPlannerDatabase extends RoomDatabase {
    public abstract VacationDao vacationDao();

    public abstract ExcursionDao excursionDao();

    public static volatile VacationPlannerDatabase instance;

    public static VacationPlannerDatabase getDatabase(Context context) {
        if (instance == null) {
            synchronized (VacationPlannerDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            VacationPlannerDatabase.class,
                            "vacation_planner_database.db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }
}
