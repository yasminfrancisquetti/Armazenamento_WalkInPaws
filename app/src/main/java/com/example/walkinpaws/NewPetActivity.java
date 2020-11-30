package com.example.walkinpaws;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.walkinpaws.model.Pet;
import com.example.walkinpaws.util.GeneralUtilities;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;


public class NewPetActivity extends AppCompatActivity
{
    public static final String INPUT_LOGGED_USER_CPF = "logged_user";

    public static final String OUTPUT_PET = "pet_data";

    public static final String TAG = "beep-new-pet";

    public static final String FILE_PREFIX = "image_";

    private Uri selectedFileUri;

    private GeneralUtilities util;

    private EditText nameEditText;
    private ToggleButton speciesToggleButton;
    private ToggleButton genderToggleButton;
    private ImageView petImageView;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);


        this.util = new GeneralUtilities(this);

        findViewById(R.id.btnescolherfoto).setOnClickListener(this::startFileSelection);

        registerButton = findViewById(R.id.btnescolherfoto2);

        registerButton.setOnClickListener(this::register);

        nameEditText = findViewById(R.id.edttxtnomepet);
        speciesToggleButton = findViewById(R.id.tbtnespecie);
        genderToggleButton = findViewById(R.id.tbtngenero);
        petImageView = findViewById(R.id.petImageView);
    }

    private void startFileSelection(@Nullable View button)
    {
        if (checkPermission())
        {
            startActivityForResult(util.getImageSelectionIntent(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri;

        if (data == null || resultCode != RESULT_OK || (uri = data.getData()) == null)
        {
            Snackbar.make(findViewById(android.R.id.content), "Erro na seleção da foto",
                    Snackbar.LENGTH_LONG).show();
        }
        else
        {
            this.selectedFileUri = uri;
            this.petImageView.setImageURI(uri);
        }
    }

    private void register(@Nullable View button)
    {
        String fileName = null;

        if (selectedFileUri != null)
        {
            try
            {
                File file = util.nextExternalFile(Pet.IMAGE_DIRECTORY_NAME, FILE_PREFIX);

                this.util.copyFile(selectedFileUri, file);

                fileName = file.getName();

            }
            catch (IOException ex)
            {
                Log.e(TAG, "onActivityResult: ", ex);

                Snackbar.make(findViewById(android.R.id.content), "Erro no salvamento da foto",
                        Snackbar.LENGTH_LONG).show();
            }
        }

        String name = nameEditText.getText().toString();
        String species = speciesToggleButton.getText().toString();
        String gender = genderToggleButton.getText().toString();

        Intent caller;
        Bundle extras;

        String userCPF = null;

        if ((caller = getIntent()) != null && (extras = caller.getExtras()) != null)
        {
            userCPF = extras.getString(INPUT_LOGGED_USER_CPF);
        }

        try
        {
            Pet pet = new Pet(
                    null,
                    name,
                    species,
                    gender,
                    fileName,
                    userCPF
            );

            int id = pet.registerFrom(this);

            Intent result = getIntent().putExtra(OUTPUT_PET, pet.duplicateWith(id));

            setResult(RESULT_OK, result);

            finish();

        }
        catch (SQLiteException ex)
        {
            Log.e(TAG, "register: ", ex);

            Snackbar.make(findViewById(android.R.id.content),
                    "Erro no cadastro",
                    Snackbar.LENGTH_LONG).show();

            setResult(RESULT_CANCELED);
        }
    }

    private boolean checkPermission()
    {
        boolean hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!hasPermission)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(android.R.id.content), "Essa ação requer a permissão pedida", Snackbar.LENGTH_LONG)
            .show();
        }
    }
}