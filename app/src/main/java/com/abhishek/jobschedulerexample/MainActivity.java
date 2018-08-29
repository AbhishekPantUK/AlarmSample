package com.abhishek.jobschedulerexample;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.abhishek.jobschedulerexample.alarm.AlarmActivity;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private static int mJobId = 0;
    private ProgressHandler mProgressHandler;

    public static final int MSG_JOB_START = 0;
    public static final int MSG_JOB_STOP = 1;
    public static final int MSG_JOB_PROGRESS = 2;

    public static final String MESSENGER_KEY = "MESSENGER_KEY";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button scheduleJobButton = (Button) findViewById(R.id.button_schedule_job);
        Button cancelJobButton = (Button) findViewById(R.id.button_cancel_job);

        scheduleJobButton.setOnClickListener(this);
        cancelJobButton.setOnClickListener(this);

        mProgressHandler = new ProgressHandler(this);
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, JobScheduleService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, JobScheduleService.class);
        Messenger messenger = new Messenger(mProgressHandler);
        serviceIntent.putExtra(MESSENGER_KEY, messenger);
        startService(serviceIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.button_schedule_job:
                Log.d(TAG, "Scheduling job");
                scheduleJob();
                break;

            case R.id.button_cancel_job:
                Log.d(TAG, "Cancel all scheduled jobs");
                JobScheduler scheduler = (JobScheduler) getSystemService(
                        Context.JOB_SCHEDULER_SERVICE);
                List<JobInfo> allPendingJobs = scheduler.getAllPendingJobs();
                for (JobInfo info : allPendingJobs) {
                    int id = info.getId();
                    scheduler.cancel(id);
                }
                Toast.makeText(MainActivity.this, "All Job Canceled", Toast.LENGTH_SHORT).show();

                //or
//                mJobScheduler.cancelAll();

                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        Log.d(TAG, "Job Scheduled");

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.HOUR_OF_DAY, 10);
            cal1.set(Calendar.MINUTE, 24);
            cal1.set(Calendar.SECOND, 30);

            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.HOUR_OF_DAY, 10);
            cal2.set(Calendar.MINUTE, 25);
            cal2.set(Calendar.SECOND, 30);

            jobScheduler.schedule(getJobInfoForFutureTask(this, cal1.getTimeInMillis()));
            jobScheduler.schedule(getJobInfoForFutureTask(this, cal2.getTimeInMillis()));
            Toast.makeText(this, "New job scheduled with jobId: " + mJobId,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private JobInfo getJobInfoForFutureTask(Context context,
                                            long timeTillFutureJob) {
        ComponentName serviceComponent = new ComponentName(context, JobScheduleService.class);

        return new JobInfo.Builder(mJobId++, serviceComponent)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setOverrideDeadline(timeTillFutureJob)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build();
    }
}
