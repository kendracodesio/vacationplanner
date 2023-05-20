package com.example.vacationplanner.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VacationList extends AppCompatActivity {

    VacationRepository vacationRepository;
    List<Vacation> vacationList;
    VL_RecyclerViewAdapter vl_recyclerViewAdapter;
    RecyclerView mVacationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        mVacationRecyclerView = findViewById(R.id.mVacationList);
        vacationList = new ArrayList<>();
        vacationRepository = new VacationRepository(getApplication());
        //Initialize the RecyclerView Adapter
        vl_recyclerViewAdapter = new VL_RecyclerViewAdapter(VacationList.this, vacationList, new VL_RecyclerViewAdapter.VacationItemClickListener() {
            //gets the vacationId so details can be shown in the DetailedVacation activity
            @Override
            public void vacationItemClick(Vacation vacation) {
                Intent intent = new Intent(VacationList.this, DetailedVacation.class);
                Log.d("VacationPlanner", "Vacation Id: to pass to DetailedVacation: " + vacation.getId());
                intent.putExtra("vacation_id", vacation.getId());
                startActivity(intent);
            }

        });

        //Set the adapter to the RecyclerView
        mVacationRecyclerView.setAdapter(vl_recyclerViewAdapter);
        mVacationRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        vacationRepository.getAllVacations().observe(this, new Observer<List<Vacation>>() {
            @Override
            public void onChanged(@Nullable final List<Vacation> vacations) {
                //update the vacations in the adapter
                vl_recyclerViewAdapter.setVacations(vacations);
            }
        });

        //floating action button to add vacation to list
        FloatingActionButton addVacationButton = findViewById(R.id.addVacationButton);
        addVacationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationList.this, AddVacation.class);
                startActivity(intent);
            }
        });

    }

}