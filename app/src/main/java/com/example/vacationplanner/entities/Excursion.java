package com.example.vacationplanner.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "excursions",
        foreignKeys = @ForeignKey(entity = Vacation.class,
                                parentColumns = "id",
                                childColumns = "vacation_id",
                                onDelete = ForeignKey.RESTRICT
))

public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "vacation_id")
    private int vacationId;


    public Excursion(int id, int vacationId) {
        this.id = id;
        this.vacationId = vacationId;
    }


    public Excursion() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }



}
