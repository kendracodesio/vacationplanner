package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailedExcursion extends AppCompatActivity {
    VacationRepository vacationRepository;
    int vacationId, excursionId;
    Vacation vacation;
    Excursion excursion;
    String alarmExcursion = "3";
    int notificationId;
    private boolean excursionNotifyEnabled = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_excursion);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int excursionId = intent.getIntExtra("excursion_id", -1);

        TextView vacationTitle = findViewById(R.id.vacationTitle3);
        TextView excursionTitle = findViewById(R.id.excursionTitle);
        TextView excursionDate = findViewById(R.id.excursionDate);
        Button editExcursionBtn = findViewById(R.id.editExcursionBtn);
        Button homeButton = findViewById(R.id.backButton);


        if (excursionId != -1) {
            vacationRepository.getExcursionById(excursionId).observe(this, excursion -> {
                if (excursion != null) {
                    this.excursion = excursion;
                    this.excursionId = excursion.getId();
                    this.vacationId = excursion.getVacationId();
                    excursionTitle.setText(excursion.getExcursionTitle());
                    excursionDate.setText(formatExcursionDate(excursion));
                }

                vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                    if (vacation != null) {
                        this.vacation = vacation;
                        vacationTitle.setText(vacation.getTitle());
                    }
                });

            });
        }

        editExcursionBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(DetailedExcursion.this, EditExcursion.class);
            editIntent.putExtra("excursion_id", excursionId);
            startActivity(editIntent);
        });

        homeButton.setOnClickListener(v -> {
            Intent backToAllVacationIntent = new Intent(DetailedExcursion.this, VacationList.class);
            startActivity(backToAllVacationIntent);
        });

        //setting shared preferences
        SharedPreferences sharedPref = getSharedPreferences("notification_settings", Context.MODE_PRIVATE);
        excursionNotifyEnabled = sharedPref.getBoolean("excursionNotifyEnabled_" + excursionId, false);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.excursion_detail_menu, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem excursionDateNotifyItem = menu.findItem(R.id.notify_excursion);
        if (excursionNotifyEnabled) {
            excursionDateNotifyItem.setTitle("Notify Excursion Date: On");
        } else {
            excursionDateNotifyItem.setTitle("Notify Excursion Date: Off");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getSharedPreferences("notification_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("excursionNotifyEnabled_" + excursionId, excursionNotifyEnabled);
        editor.apply();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.notify_excursion) {
            //creating unique notificationId for the excursion
            notificationId = excursionId;
            notificationId = Integer.parseInt(notificationId + alarmExcursion);
            String excursionNotifyDate = excursion.getExcursionDate().toString();
            if (excursionNotifyEnabled) {
                cancelAlarm(notificationId);
                excursionNotifyEnabled = false;
            } else {
                setAlarm(excursionNotifyDate, alarmExcursion, notificationId);
                excursionNotifyEnabled = true;
            }
            //recreate menu options to update changes
            invalidateOptionsMenu();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlarm(String dateToNotify, String alarmType, int notificationId) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date myDate = null;
        try {
            myDate = sdf.parse(dateToNotify);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long triggerDate = myDate.getTime();
        Intent setAlarmIntent = new Intent(DetailedExcursion.this, AlarmReceiver.class);
        setAlarmIntent.putExtra("excursion_id", excursionId);
        setAlarmIntent.putExtra("notification_id", notificationId);
        setAlarmIntent.putExtra("alarm_type", alarmType);
        setAlarmIntent.putExtra("date", dateToNotify);
        setAlarmIntent.putExtra("excursion_title", excursion.getExcursionTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailedExcursion.this, notificationId, setAlarmIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerDate, pendingIntent);
        Toast.makeText(DetailedExcursion.this, "Notification enabled", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(int notificationId) {
        Intent cancelAlarmIntent = new Intent(DetailedExcursion.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailedExcursion.this, notificationId, cancelAlarmIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Toast.makeText(DetailedExcursion.this, "Notification cancelled", Toast.LENGTH_SHORT).show();
        }

    }


    private String formatExcursionDate(@NotNull Excursion excursion) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(excursion.getExcursionDate());
    }
}