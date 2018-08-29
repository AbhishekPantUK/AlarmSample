package com.abhishek.jobschedulerexample.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.abhishek.jobschedulerexample.R;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);

        Button setAlarmButton = (Button)findViewById(R.id.setAlarmButton);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    scheduleJob();
                    else
                        setAlarm();*/
            }
        });
    }

    private void  setAlarm(){
        //first morning alarm
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, 10);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);

        // second eve alarm
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, 17);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);

        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, AlarmReceiver.class);

        if(alarms != null){
            PendingIntent firstCallIntent = PendingIntent.getBroadcast(this, 1010, myIntent, 0);
            PendingIntent secondCallIntent = PendingIntent.getBroadcast(this, 1011, myIntent, 0);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, firstCallIntent);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, secondCallIntent);
        }
    }
}
