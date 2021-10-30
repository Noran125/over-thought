package com.example.overthought;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;


public class AddingAlarmActivity extends AppCompatActivity {

    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private TextView alarmDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_alarm);


        EditText alarm_name = findViewById(R.id.alarm_name);
        Button add_date_btn = findViewById(R.id.add_date_btn);
        Button save_alarm_btn = findViewById(R.id.save_alarm_btn);
        Button cancel_alarm_btn = findViewById(R.id.cancel_alarm_btn);
        alarmDate = findViewById(R.id.alarm_date_txt);


        add_date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker();

            }
        });


        save_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAlarm();

            }
        });


        cancel_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelAlarm();

            }
        });
    }

    private void cancelAlarm() {

        Intent i = new Intent(AddingAlarmActivity.this, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        if(alarmManager == null){

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }

        alarmManager.cancel(pendingIntent);

        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();

    }


    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder(). setTimeFormat(TimeFormat.CLOCK_12H). setHour(12).setMinute(0).setTitleText("Select Alarm Time").build();

        picker.show(getSupportFragmentManager(),"foxandroid");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(picker.getHour() > 12){
                    alarmDate.setText(String.format("%02d",(picker.getHour()-12) + " : " + String.format("%02d",picker.getMinute())+" PM"));
                } else{
                    alarmDate.setText(picker.getHour()+" : "+picker.getMinute()+" AM");
                }

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

            }
        });

    }


    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(AddingAlarmActivity.this, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show();
    }



    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name= "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}