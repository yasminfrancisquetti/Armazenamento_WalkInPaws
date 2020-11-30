package com.example.walkinpaws;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkinpaws.adapter.WalkAdapter;
import com.example.walkinpaws.model.Walk;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WalkListActivity extends AppCompatActivity
{
    private static final String CHANNEL_ID = "walk_in_paws_channel_id";

    public static final String INPUT_LOCATION = "location";

    RecyclerView recyclerView;

    private String givenLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_list);

        WalkAdapter adapter = new WalkAdapter(Walk.listFrom(this), this);

        recyclerView = findViewById(R.id.walk_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);


    }


}