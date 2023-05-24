package com.example.vacationplanner.UI;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID_A = "vacation_notifications";


    String message;

    int notificationId;
    Intent notificationIntent;
    String vacationTitle;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Fetch the info from the intent
        int vacationId = intent.getIntExtra("vacation_id", -1);
        int excursionId = intent.getIntExtra("excursion_id", -1);
        int notificationId = intent.getIntExtra("notification_id", -1);
        String alarmType = intent.getStringExtra("alarm_type");
        String date = intent.getStringExtra("date");



        //Determine what message to show and which date to use
        if (alarmType.equals("1")) {
            vacationTitle = intent.getStringExtra("vacation_title");
            message = vacationTitle + ": Your vacation starts today! ("+ date +")";

        }
        if (alarmType.equals("2")) {
            vacationTitle = intent.getStringExtra("vacation_title");
            message = vacationTitle + ": Your vacation ends today! ("+ date +")";

        }
        if (alarmType.equals("3")) {
            String excursionTitle = intent.getStringExtra("excursion_title");
            message = excursionTitle + ": Your excursion is today! ("+ date +")";
        }

        createNotificationChannel(context, CHANNEL_ID_A);

        if (alarmType.equals("1") || alarmType.equals("2")) {
            notificationIntent = new Intent(context, DetailedVacation.class);
            notificationIntent.putExtra("vacation_id", vacationId);
        }

        if (alarmType.equals("3")) {
            notificationIntent = new Intent(context, DetailedExcursion.class);
            notificationIntent.putExtra("excursion_id", excursionId);

        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_A)
                .setContentTitle("Vacation Planner")
                .setContentText(message)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);


    }

    private void createNotificationChannel(Context context, String CHANNEL_ID_A) {
        CharSequence name = context.getResources().getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_A, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }
}
