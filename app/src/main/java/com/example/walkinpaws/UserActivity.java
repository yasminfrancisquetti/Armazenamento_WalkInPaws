package com.example.walkinpaws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walkinpaws.model.User;

import java.io.IOException;

public class UserActivity extends AppCompatActivity
{

    public static final String INPUT_LOGGED_USER = "logged_user";

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView CPFEditText = findViewById(R.id.txtvwcpfusuario);
        TextView nameEditText = findViewById(R.id.txtvwnomeusuario);
        TextView emailEditText = findViewById(R.id.txtvwemailusuario);
        ImageView imageView = findViewById(R.id.imgvwuserprofile);


        loggedUser = (User) getIntent().getSerializableExtra(INPUT_LOGGED_USER);

        if (loggedUser == null)
        {
            startLoginActivity();
            return;
        }

        CPFEditText.setText(loggedUser.getCPF());
        nameEditText.setText(loggedUser.getName());
        emailEditText.setText(loggedUser.getEmail());


        try
        {
            Bitmap bitmap = loggedUser.getImageBitMapFrom(this);

            if (bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
            }
        }
        catch (IOException ex) {
            Log.e("beep-error", "onCreate: ", ex);
        }


        findViewById(R.id.btnapagarusuario).setOnClickListener(this::deleteCurrentUser);

    }

    void startLoginActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }

    void deleteCurrentUser(View button)
    {
        startLoginActivity();
    }
}