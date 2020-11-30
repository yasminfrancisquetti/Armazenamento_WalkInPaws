package com.example.walkinpaws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class CatActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);
    }

    public void direcionarSite(android.view.View btnmaisinfo)
    {
        Uri calorcat = Uri.parse("http://www.sanoldog.com.br/cuidados-com-os-gatos-no-verao/#:~:text=Uma%20%C3%B3tima%20dica%20%C3%A9%20colocar,cuidar%20dos%20nossos%20amigos%20peludos.");
        Intent direcionarcalorcat = new Intent(Intent.ACTION_VIEW, calorcat);
        startActivity(direcionarcalorcat);
    }
}