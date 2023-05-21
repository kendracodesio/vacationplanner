package com.example.vacationplanner.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "lodging_info")
    private String lodgingInfo;

    @ColumnInfo(name = "start_date")
    private Date startDate;


    @ColumnInfo(name = "end_date")
    private Date endDate;


    public Vacation(int id, String title, String lodgingInfo, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.lodgingInfo = lodgingInfo;
        this.startDate = startDate;
        this.endDate = endDate;

    }


    public Vacation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLodgingInfo() {
        return lodgingInfo;
    }

    public void setLodgingInfo(String lodgingInfo) {
        this.lodgingInfo = lodgingInfo;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


}
