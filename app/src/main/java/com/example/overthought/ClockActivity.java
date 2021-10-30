package com.example.overthought;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class ClockActivity extends AppCompatActivity {

    FloatingActionButton add_alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);


        ListView alarms = findViewById(R.id.List);

        add_alarm  = findViewById(R.id.fab);

        add_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ClockActivity.this, AddingAlarmActivity.class));

            }
        });
    }
}