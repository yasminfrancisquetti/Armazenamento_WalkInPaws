package com.example.walkinpaws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkinpaws.adapter.PetAdapter;
import com.example.walkinpaws.model.Pet;

import java.util.List;

public class PetListActivity extends AppCompatActivity
{
    RecyclerView recyclerView;

    PetAdapter adapter;

    public static final String INPUT_LOGGED_USER_CPF = "input_logged_user_cpf";
    private String loggedUserCPF;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        Intent intent = getIntent();

        if (intent == null || (loggedUserCPF = intent.getStringExtra(INPUT_LOGGED_USER_CPF)) == null)
        {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

// try it now
        List<Pet> pets = Pet.listFrom(this, loggedUserCPF);

        adapter = new PetAdapter(pets, this);

        recyclerView = findViewById(R.id.petRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null)
        {
            Pet pet = (Pet) data.getSerializableExtra(NewPetActivity.OUTPUT_PET);

            adapter.addPet(pet);
        }
    }

    public void startNewPetActivity(View button)
    {
        Intent intent = new Intent(this, NewPetActivity.class);

        intent.putExtra(NewPetActivity.INPUT_LOGGED_USER_CPF, loggedUserCPF);

        startActivityForResult(intent, 0
        );
    }
}