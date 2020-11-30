package com.example.walkinpaws.model;

import android.animation.FloatEvaluator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walkinpaws.util.GeneralUtilities;

import java.io.IOException;
import java.io.Serializable;
import java.util.NoSuchElementException;

import static com.example.walkinpaws.NewPetActivity.TAG;

// TODO: Add column constants

public class User implements Serializable, Model
{
    public static final String TABLE_NAME = "USUARIO";
    public static final String IMAGE_DIRECTORY_NAME = "user-images";


    private final String CPF;
    private final String name;
    private final String email;

    private final boolean isOnDarkTheme;
    private final String profilePhotoName;

    private final String password;

    public User(String CPF, String name, String email, boolean isOnDarkTheme, String profilePhoto, String password)
    {
        this.CPF = CPF;
        this.name = name;
        this.email = email;
        this.isOnDarkTheme = isOnDarkTheme;
        this.profilePhotoName = profilePhoto;
        this.password = password;
    }

    public User(Context context, String cpf, String password)
    {

        SQLiteDatabase database = DatabaseHandler.openFrom(context);


        Cursor cursor = database.query(
                TABLE_NAME,
                null,
                "CPF_USUARIO = ? and SENHA_USUARIO = ?",
                new String[]{cpf, password},
                null, null, null,
                "1"
        );

        if (!cursor.moveToFirst())
        {
            cursor.close();
            throw new NoSuchElementException(
                    "User with email '" + cpf + "' and password '" + password + "' was not found"
            );
        }

        this.CPF = cursor.getString(0);
        this.name = cursor.getString(1);
        this.email = cursor.getString(2);
        this.isOnDarkTheme = false; // TODO: Look at shared preferences owo
        this.password = cursor.getString(3);
        this.profilePhotoName = cursor.isNull(4) ? null : cursor.getString(4);

        cursor.close();
    }

    public String getCPF() {
        return CPF;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isOnDarkTheme() {
        return isOnDarkTheme;
    }

    public String getProfilePhotoName() {
        return profilePhotoName;
    }

    public String getPassword() {
        return password;
    }

    public int registerFrom(@NonNull Context context) {

        SQLiteDatabase database = DatabaseHandler.openFrom(context);

        ContentValues values = new ContentValues();

        values.put("CPF_USUARIO", this.CPF);
        values.put("NOME_USUARIO", this.name);
        values.put("NOME_USUARIO", this.name);
        values.put("EMAIL_USUARIO", this.email);
        values.put("SENHA_USUARIO", this.password);
        values.put("CAMINHO_IMAGEM_PERFIL", this.profilePhotoName);

        long result = database.insertOrThrow(
                TABLE_NAME,
                null,
                values
        );

        Log.d("userlog", "register id:" + result);

        if (result == -1)
        {
            throw new SQLException("Insertion failed");
        }

        database.close();

        return (int) result;
    }

    public void deleteUsing(Context context)
    {
        SQLiteDatabase database = DatabaseHandler.openFrom(context);

        long result = database.delete(
                TABLE_NAME,
                "CPF_USUARIO = ?",
                new String[]{CPF});

        if (result == -1) {
            throw new SQLiteException("Can't delete " + this);
        }
    }

    @Nullable
    public Bitmap getImageBitMapFrom(Context context) throws IOException
    {
        return new GeneralUtilities(context).getBitmap(IMAGE_DIRECTORY_NAME, profilePhotoName);
    }

    @Override
    public String toString()
    {
        return "User{" +
                "CPF='" + CPF + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isOnDarkTheme=" + isOnDarkTheme +
                ", profilePhotoResource='" + profilePhotoName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

