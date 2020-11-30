package com.example.walkinpaws;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walkinpaws.model.User;
import com.example.walkinpaws.util.GeneralUtilities;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

public class NewUserActivity extends AppCompatActivity
{
    public static final String FILE_PREFIX = "image_";

    private static final String TAG = "beep-NewUser";

    private String fileName;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText cpfEditText;
    private EditText passwordEditText;

    private GeneralUtilities util;

    private Uri imageUri;

    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        this.util = new GeneralUtilities(this);

        nameEditText = findViewById(R.id.edttxtnome);
        emailEditText = findViewById(R.id.edttxtemail);
        cpfEditText = findViewById(R.id.edttxtcpf);
        passwordEditText = findViewById(R.id.edttxtpass);

        findViewById(R.id.btnpick).setOnClickListener(this::startFileSelection);

        registerButton = findViewById(R.id.btncadastrar);
        registerButton.setOnClickListener(this::saveUser);
    }

    public void saveUser(View button)
    {
        if (imageUri != null) {

            try
            {
                saveFile(imageUri);
            } catch (IOException ex)
            {
                Log.e(TAG, "onActivityResult: ", ex);
                Snackbar.make(findViewById(android.R.id.content), "Erro na criação do arquivo",
                        Snackbar.LENGTH_LONG).show();
            }
        }

        try
        {
            String cpf = cpfEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (cpf.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty())
            {
                Snackbar.make(findViewById(android.R.id.content), "Preencha todos os dados",
                        Snackbar.LENGTH_LONG).show();
            }

            new User(cpf, name, email, false, fileName, password)
                    .registerFrom(this);

            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Registro feito com sucesso",
                    Snackbar.LENGTH_LONG
            ).show();

            registerButton.setEnabled(false);



        } catch (SQLiteException ex)
        {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Erro ao registrar",
                    Snackbar.LENGTH_LONG
            ).show();
        }


    }

    public void startFileSelection(View button)
    {
        startActivityForResult(util.getImageSelectionIntent(), 0);
    }

    private void saveFile(Uri uri) throws IOException
    {
        File output = this.util.nextFile(User.IMAGE_DIRECTORY_NAME, FILE_PREFIX);

        this.util.copyFile(uri, output);

        this.fileName = output.getName();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null)
        {
            this.imageUri = data.getData();
        }
        else
        {
            Snackbar.make(findViewById(android.R.id.content),
                    "Erro na seleção do arquivo", Snackbar.LENGTH_LONG).show();

            Log.e(TAG, "onActivityResult: ResultCode:" + resultCode + "data:" + data);
        }

    }
}