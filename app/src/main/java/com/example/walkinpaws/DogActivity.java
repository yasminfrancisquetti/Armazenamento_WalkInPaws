package com.example.walkinpaws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class DogActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog);
    }

    public void direcionarSite(android.view.View btnmaisinfodog)
    {
        Uri calordog = Uri.parse("https://www.royalcanin.com/br/dogs/health-and-wellbeing/keeping-your-dog-cool-in-summer");
        Intent direcionarcalordog = new Intent(Intent.ACTION_VIEW, calordog);
        startActivity(direcionarcalordog);
    }
}