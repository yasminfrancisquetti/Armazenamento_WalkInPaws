package com.example.walkinpaws;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walkinpaws.model.Pet;
import com.example.walkinpaws.model.Walk;
import com.example.walkinpaws.util.GeneralUtilities;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FinishWalkActivity extends AppCompatActivity
{
    public static final String TAG = "fwalk";

    public static final String INPUT_START_TIME = "start_time";
    public static final String INPUT_LOCATION = "location";

    private EditText destinyEditText;
    private EditText messageEditText;
    private EditText petNameEditText;

    private List<Integer> selectedPets;

    private Uri selectedUri;

    private String givenStartTime;
    private String givenLocation;

    private GeneralUtilities utilities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_walk);

        Intent intent = getIntent();

        if (intent == null || (givenStartTime = intent.getStringExtra(INPUT_START_TIME)) == null ||
                (givenLocation = intent.getStringExtra(INPUT_LOCATION)) == null)
        {
            Log.e(TAG, "onCreate: Missing Input");
            finish();
        }

        selectedPets = new ArrayList<>();

        destinyEditText = findViewById(R.id.edttxtdestino);
        petNameEditText = findViewById(R.id.edttxtinserirpet);
        messageEditText = findViewById(R.id.edtxtmensagem);

        utilities = new GeneralUtilities(this);


        findViewById(R.id.btninserirpet).setOnClickListener(this::insertPets);
        findViewById(R.id.btnescolherimagem).setOnClickListener(this::startImageSelection);
        findViewById(R.id.btnconcluir).setOnClickListener(this::registerWalk);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void insertPets(View button)
    {
        String petName = petNameEditText.getText().toString();

        if (petName.isEmpty())
        {
            petNameEditText.setError(getString(R.string.error_missing_fields));
            return;
        }

        List<Pet> pets = Pet.searchByName(petName, this);

        if (pets.size() == 0)
        {
            petNameEditText.setError("Nenhum resultado encontrado");
        } else
        {
            for (Pet pet : pets)
            {
                selectedPets.add(pet.getID());
            }

            Snackbar.make(findViewById(android.R.id.content), "Pet inserido", Snackbar.LENGTH_LONG).show();
        }
    }

    private void startImageSelection(View button)
    {

        Intent intent = utilities.getImageSelectionIntent();

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK)
        {

            Uri uri;

            if (data != null && (uri = data.getData()) != null)
            {
                selectedUri = uri;
            }

        }
    }

    String writeImage() throws IOException
    {
        File file = utilities.nextFile(Walk.IMAGE_DIRECTORY_NAME, "walk-");

        utilities.copyFile(selectedUri, file);

        return file.getName();
    }

    private void registerWalk(View button)
    {
        String destiny = destinyEditText.getText().toString();

        if (destiny.isEmpty())
        {
            destinyEditText.setError("Favor preencher este campo");
            return;
        }

        String filename = null;

        if (selectedUri != null)
        {

            try
            {
                filename = writeImage();
            } catch (IOException ex)
            {
                Log.e(TAG, "registerWalk: ", ex);
            }
        }

        Walk walk = new Walk(
                null,
                this.givenStartTime,
                new SimpleDateFormat("dd:MM:yy").format(Calendar.getInstance().getTime()),
                givenLocation, // TODO: Location here
                destiny,
                filename,
                selectedPets,
                messageEditText.getText().toString()

        );

        walk.registerFrom(this);

        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra(CardActivity.INPUT_WALK, walk);

        startActivity(intent);


    }
}