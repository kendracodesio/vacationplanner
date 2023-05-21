package com.example.vacationplanner.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "excursions",
        foreignKeys = @ForeignKey(entity = Vacation.class,
                                parentColumns = "id",
                                childColumns = "vacation_id",
                                onDelete = ForeignKey.RESTRICT),
        indices = {@Index("vacation_id")}

)

public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "excursion_title")
    private String excursionTitle;

    @ColumnInfo(name = "excursion_date")
    private Date excursionDate;

    @ColumnInfo(name = "vacation_id")
    private int vacationId;


    public Excursion(int id, String excursionTitle, Date excursionDate, int vacationId) {
        this.id = id;
        this.excursionTitle = excursionTitle;
        this.excursionDate = excursionDate;
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


    public String getExcursionTitle() {
        return excursionTitle;
    }

    public void setExcursionTitle(String excursionTitle) {
        this.excursionTitle = excursionTitle;
    }

    public Date getExcursionDate() {
        return excursionDate;
    }

    public void setExcursionDate(Date excursionDate) {
        this.excursionDate = excursionDate;
    }

    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }



}
