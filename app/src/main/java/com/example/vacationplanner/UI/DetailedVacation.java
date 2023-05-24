package com.example.vacationplanner.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedVacation extends AppCompatActivity {

    VacationRepository vacationRepository;
    int vacationId;
    Vacation vacation;
    List<Excursion> excursionList;
    EL_RecyclerViewAdapter el_recyclerViewAdapter;
    RecyclerView excursionListRecyclerView;
    int alarmStart = 1;
    int alarmEnd = 2;
    int notificationId;
    private boolean startNotificationEnabled = false;
    private boolean endNotificationEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_vacation);

        vacationRepository = new VacationRepository(getApplication());
        excursionList = new ArrayList<>();

        Intent intent = getIntent();
        int vacationId = intent.getIntExtra("vacation_id", -1);

        Button addExcursionBtn = findViewById(R.id.addExcursionBtn);
        TextView vacationTitle = findViewById(R.id.vacationTitle);
        TextView vacationDetails = findViewById(R.id.vacationDetails);

        if (vacationId != -1) {

            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    this.vacation = vacation;
                    this.vacationId = vacation.getId();
                    vacationTitle.setText(vacation.getTitle());
                    vacationDetails.setText(formatVacationDetails(vacation));
                }
            });
        }
        excursionListRecyclerView = findViewById(R.id.excursionList);
        //Initialize the RecyclerView Adapter
        //gets the excursionId when excursion item is clicked so details can be shown in the DetailedExcursion activity
        el_recyclerViewAdapter = new EL_RecyclerViewAdapter(DetailedVacation.this, excursionList, excursion -> {
            Intent showExcursionDetailsIntent = new Intent(DetailedVacation.this, DetailedExcursion.class);
            Log.d("DetailedVacation", "Excursion Id: to pass to DetailedExcursion: " + excursion.getId());
            showExcursionDetailsIntent.putExtra("excursion_id", excursion.getId());
            startActivity(showExcursionDetailsIntent);
        });

        //Set the adapter to the RecyclerView
        excursionListRecyclerView.setAdapter(el_recyclerViewAdapter);
        excursionListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //method call to setup swipe to delete for excursion items in recycler view
        setupRecyclerViewSwipeToDelete(excursionListRecyclerView);


        vacationRepository.getExcursionsByVacation(vacationId).observe(this, excursions -> {
            if (excursions != null) {
                this.excursionList = excursions;
                //update the excursions in the adapter
                el_recyclerViewAdapter.setExcursion(excursions);
            }
        });

        //button to add excursion to list
        addExcursionBtn.setOnClickListener(v -> {
            Intent addExcursionIntent = new Intent(DetailedVacation.this, AddExcursion.class);
            addExcursionIntent.putExtra("vacation_id", vacationId);
            startActivity(addExcursionIntent);
        });

        //setting shared preferences
        SharedPreferences sharedPref = getSharedPreferences("notification_settings", Context.MODE_PRIVATE);
        startNotificationEnabled = sharedPref.getBoolean("startNotificationEnabled_" + vacationId, false);
        endNotificationEnabled = sharedPref.getBoolean("endNotificationEnabled_" + vacationId, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vacation_detail_menu, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem startNotificationItem = menu.findItem(R.id.notify_start);
        MenuItem endNotificationItem = menu.findItem(R.id.notify_end);
        if (startNotificationEnabled) {
            startNotificationItem.setTitle("Notify Start Date: On");
        } else {
            startNotificationItem.setTitle("Notify Start Date: Off");
        }

        if (endNotificationEnabled) {
            endNotificationItem.setTitle("Notify End Date: On");
        } else {
            endNotificationItem.setTitle("Notify End Date: Off");
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getSharedPreferences("notification_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("startNotificationEnabled_" + vacationId, startNotificationEnabled);
        editor.putBoolean("endNotificationEnabled_" + vacationId, endNotificationEnabled);
        editor.apply();

        if (item.getItemId() == R.id.action_edit) {
            Intent editIntent = new Intent(DetailedVacation.this, EditVacation.class);
            editIntent.putExtra("vacation_id", vacationId);
            startActivity(editIntent);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            shareVacation();
            return true;
        } else if (item.getItemId() == R.id.notify_start) {
            notificationId = (vacationId * 10) + alarmStart;
            String startDate = vacation.getStartDate().toString();
            if (startNotificationEnabled) {
                cancelAlarm(notificationId);
                startNotificationEnabled = false;
            } else {
                setAlarm(startDate, alarmStart, notificationId);
                startNotificationEnabled = true;
            }
            invalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.notify_end) {
            notificationId = (vacationId * 10) + alarmEnd;
            String endDate = vacation.getEndDate().toString();
            if (endNotificationEnabled) {
                cancelAlarm(notificationId);
                endNotificationEnabled = false;
            } else {
                setAlarm(endDate, alarmEnd, notificationId);
                endNotificationEnabled = true;
            }
            invalidateOptionsMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    private void shareVacation() {
        StringBuilder vacationDetails = new StringBuilder(vacation.getTitle());
        vacationDetails.append("\n").append(formatVacationDetails(vacation));

        if (excursionList != null && !excursionList.isEmpty()) {
            vacationDetails.append("\n\nExcursions:\n");
            for (Excursion excursion : excursionList) {
                vacationDetails.append("- ").append(excursion.getExcursionTitle()).append("\n");
            }
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share Vacation Details");
        startActivity(shareIntent);
    }


    private void setAlarm(String dateToNotify, int alarmType, int notificationId) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date myDate = null;
        try {
            myDate = sdf.parse(dateToNotify);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long triggerDate = myDate.getTime();
        Intent notifyStartIntent = new Intent(DetailedVacation.this, AlarmReceiver.class);
        notifyStartIntent.putExtra("vacation_id", vacationId);
        notifyStartIntent.putExtra("alarm_type", alarmType);
        notifyStartIntent.putExtra("date", dateToNotify);
        notifyStartIntent.putExtra("vacation_title", vacation.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailedVacation.this, notificationId, notifyStartIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerDate, pendingIntent);
        Toast.makeText(DetailedVacation.this, "Notification enabled", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(int notificationId) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailedVacation.this, notificationId, alarmIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Toast.makeText(DetailedVacation.this, "Notification cancelled", Toast.LENGTH_SHORT).show();
        }

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
                Excursion excursionToDelete = el_recyclerViewAdapter.getExcursionAtPosition(position);
                //Create an AlertDialog to ask for confirmation

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedVacation.this);
                builder.setMessage("Are you sure you want to delete this excursion?")
                        .setPositiveButton("Yes", (dialog, id) -> {
                            //If the user confirms, perform the delete
                            vacationRepository.deleteExcursion(excursionToDelete);
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            //If the user cancels, don't delete and reset the swipe
                            el_recyclerViewAdapter.notifyItemChanged(position); //Reset swipe
                        });
                //Create the AlertDialog object and show it
                builder.create().show();
            }
        };

        //Create ItemTouchHelper and attach it to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private String formatVacationDetails(@NotNull Vacation vacation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startDate = dateFormat.format(vacation.getStartDate());
        String endDate = dateFormat.format(vacation.getEndDate());
        return "Dates: " + startDate + " - " + endDate + "\n Lodging: " + vacation.getLodgingInfo();
    }
}

