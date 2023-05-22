package com.example.vacationplanner.UI;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
        //gets the vacationId when vacation item is clicked so details can be shown in the DetailedVacation activity
        vl_recyclerViewAdapter = new VL_RecyclerViewAdapter(VacationList.this, vacationList, vacation -> {
            Intent intent = new Intent(VacationList.this, DetailedVacation.class);
            Log.d("VacationPlanner", "Vacation Id: to pass to DetailedVacation: " + vacation.getId());
            intent.putExtra("vacation_id", vacation.getId());
            startActivity(intent);
        });

        //Set the adapter to the RecyclerView
        mVacationRecyclerView.setAdapter(vl_recyclerViewAdapter);
        mVacationRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        vacationRepository.getAllVacations().observe(this, vacations -> {
            //update the vacations in the adapter
            vl_recyclerViewAdapter.setVacations(vacations);
        });

        //floating action button to add vacation to list
        FloatingActionButton addVacationButton = findViewById(R.id.addVacationButton);
        addVacationButton.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, AddVacation.class);
            startActivity(intent);
        });

        //method call to setup swipe to delete for vacation items in recycler view
        setupRecyclerViewSwipeToDelete(mVacationRecyclerView);

    }

    private void setupRecyclerViewSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //Get the position of the item to be deleted
                int position = viewHolder.getAdapterPosition();
                Vacation vacationToDelete = vl_recyclerViewAdapter.getVacationAtPosition(position);
                //Create an AlertDialog to ask for confirmation

                AlertDialog.Builder builder = new AlertDialog.Builder(VacationList.this);
                builder.setMessage("Are you sure you want to delete this vacation?")
                        .setPositiveButton("Yes", (dialog, id) -> {
                            //If the user confirms, perform the delete
                            vacationRepository.deleteVacation(vacationToDelete, isDeleted -> {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isDeleted) {
                                            //Notify adapter of item removed
                                            vl_recyclerViewAdapter.notifyItemRemoved(position);
                                            Toast.makeText(VacationList.this, "Vacation deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //If the delete fails, notify the user and don't remove the item from the adapter
                                            Toast.makeText(VacationList.this, "Cannot delete vacation with existing excursions", Toast.LENGTH_LONG).show();
                                            vl_recyclerViewAdapter.notifyItemChanged(position); //Reset swipe
                                        }
                                    }
                                });

                            });
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            //If the user cancels, don't delete and reset the swipe
                            vl_recyclerViewAdapter.notifyItemChanged(position); //Reset swipe
                        });
                //Create the AlertDialog object and show it
                builder.create().show();
            }
        };

        //Create ItemTouchHelper and attach it to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


}

