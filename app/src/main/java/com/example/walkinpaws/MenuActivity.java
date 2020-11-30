package com.example.walkinpaws;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walkinpaws.model.User;
import com.example.walkinpaws.model.Pet;

import java.util.NoSuchElementException;

public class MenuActivity extends AppCompatActivity
{
    public static final String INPUT_LOGGED_USER = "logged_user";

    private User loggedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loggedUser = (User) getIntent().getSerializableExtra(INPUT_LOGGED_USER);

        updateButtons();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateButtons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        updateButtons();
    }

    private void updateButtons()
    {
        int petData = Pet.petDataFromOwner(loggedUser.getCPF(), this);

        boolean hascat = (petData & Pet.HAS_CATS) != 0;
        boolean hasdog = (petData & Pet.HAS_DOGS) != 0;

        if (!hascat)
        {
            findViewById(R.id.btncaticon).setAlpha(0.5f);
            findViewById(R.id.btncaticon).setClickable(false);
        }
        else
        {
            findViewById(R.id.btncaticon).setAlpha(1f);
            findViewById(R.id.btncaticon).setClickable(true);
        }

        if (!hasdog)
        {
            findViewById(R.id.btndogicon).setAlpha(0.5f);
            findViewById(R.id.btndogicon).setClickable(false);
        }
        else
        {
            findViewById(R.id.btndogicon).setAlpha(1f);
            findViewById(R.id.btndogicon).setClickable(true);
        }
    }

    public void exibirGeo(@Nullable View btngeoicon)
    {
        Intent caller = new Intent(this, GeoActivity.class);
        startActivity(caller);
    }

    public void exibirDog(@Nullable View btndogicon)
    {
        Intent caller = new Intent(this, DogActivity.class);
        startActivity(caller);
    }

    public void exibirCat(@Nullable View btncaticon)
    {
        Intent caller = new Intent(this, CatActivity.class);
        startActivity(caller);
    }

    public void exibirUser(@Nullable View btnusericon)
    {
        Intent caller = new Intent(this, UserActivity.class);
        caller.putExtra(UserActivity.INPUT_LOGGED_USER, loggedUser);
        startActivity(caller);
    }

    public void exibirPets(android.view.View btnpetsicon)
    {
        Intent caller = new Intent(this, PetListActivity.class);
        caller.putExtra(PetListActivity.INPUT_LOGGED_USER_CPF, loggedUser.getCPF());
        startActivityForResult(caller, 0);
    }
}