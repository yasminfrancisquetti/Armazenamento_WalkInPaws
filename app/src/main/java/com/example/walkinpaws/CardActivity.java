package com.example.walkinpaws;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.walkinpaws.model.Pet;
import com.example.walkinpaws.model.Walk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class CardActivity extends AppCompatActivity
{

    public static final String INPUT_WALK = "walk";
    static final String TAG = "beep-card";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        setBackground();
        loadWalk();

    }

    private void loadWalk() {

        Intent intent = getIntent();
        Walk walk;

        if (intent == null || (walk = (Walk) intent.getSerializableExtra(INPUT_WALK)) == null) {
            Log.e(TAG, "onCreate: Missing Data");
            finish();
            return;
        }

        TextView dateEditText = findViewById(R.id.txtvwdatapasseio);
        TextView messageEditText = findViewById(R.id.txtvwmsg);
        TextView destinationEditText = findViewById(R.id.txtvwdestinofinal);
        TextView petsEditText = findViewById(R.id.txtvwpets);

        ImageView imageView = findViewById(R.id.imgvwwalk);

        dateEditText.setText(walk.getEndTime());
        messageEditText.setText(walk.getOptionalMessage());
        destinationEditText.setText(walk.getDestination());
        petsEditText.setText(TextUtils.join("\n", Pet.namesFromIDs(walk.getPresentPets(), this)));

        try {
            imageView.setImageBitmap(walk.getImageBitmapFrom(this));
        }
        catch (IOException ex) {
            Log.e(TAG, "loadWalk: ", ex);
        }

    }

    private void setBackground() {

        final String[] colors = {"#BD93F9", "#FF79C6", "#8BE9FD", "#50FA7B", "#F1FA8C", "#FFB86C", "#FF5555"};
        Random random = new Random();
        int index = random.nextInt(colors.length);
        findViewById(R.id.cnstlaybackground).setBackgroundColor(Color.parseColor(colors[index]));

    }
}