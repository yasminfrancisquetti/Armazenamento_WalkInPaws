package com.example.walkinpaws.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walkinpaws.util.GeneralUtilities;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pet implements Model
{
    public static final String IMAGE_DIRECTORY_NAME = "pet_images";

    public static final String TABLE_NAME = "PET";
    public static final String COLUMN_ID = "CODIGO_PET";
    public static final String COLUMN_NAME = "NOME_PET";
    public static final String COLUMN_SPECIES_NAME = "ESPECIE_PET";
    public static final String COLUMN_GENDER = "GENERO_PET";
    public static final String COLUMN_PHOTO_FILE_NAME = "CAMINHO_IMAGEM";
    public static final String COLUMN_OWNER_CPF = "CPF_USUARIO";

    private final Integer ID;
    private final String name;
    private final String speciesName;
    private final String gender;
    private final String photoFileName;

    @Nullable
    private final String ownerCPF;

    public Pet(@Nullable Integer ID, @NonNull String name, @NonNull String speciesName, @NonNull String gender, @Nullable String photoFileName, @NonNull String ownerCPF)
    {
        this.ID = ID;
        this.name = name;
        this.speciesName = speciesName;
        this.gender = gender;
        this.ownerCPF = ownerCPF;
        this.photoFileName = photoFileName;
    }

    private Pet(Cursor cursor)
    {
        this.ID = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.speciesName = cursor.getString(2);
        this.gender = cursor.getString(3);
        this.ownerCPF = cursor.getString(4);
        this.photoFileName = cursor.getString(5);
    }

    public Integer getID()
    {
        return ID;
    }

    public String getName()
    {
        return name;
    }

    public String getSpeciesName()
    {
        return speciesName;
    }

    public String getPhotoFileName()
    {
        return photoFileName;
    }

    @Nullable
    public String getOwnerCPF()
    {
        return ownerCPF;
    }

    public String getGender()
    {
        return gender;
    }

    public int registerFrom(Context context)
    {
        SQLiteDatabase database = DatabaseHandler.openFrom(context);

        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, this.ID);
        values.put(COLUMN_NAME, this.name);
        values.put(COLUMN_SPECIES_NAME, this.speciesName);
        values.put(COLUMN_GENDER, this.gender);
        values.put(COLUMN_PHOTO_FILE_NAME, this.photoFileName);
        values.put(COLUMN_OWNER_CPF, this.ownerCPF);

        long id = database.insertOrThrow(TABLE_NAME, null, values);

        if (id == -1)
        {
            Log.wtf("beep-error", "Invalid return from SQLiteDatabase::insertOrThrow");
            throw new SQLiteException("Unknown Error");
        }

        return (int) id;
    }

    public Pet duplicateWith(int id)
    {
        return new Pet(id, name, speciesName, gender, photoFileName, ownerCPF);
    }

    @NotNull
    public static List<Pet> listFrom(@NonNull Context context, @Nullable String ownerCPF)
    {
        List<Pet> list = new ArrayList<>();

        SQLiteDatabase database = DatabaseHandler.openFrom(context);

        Cursor cursor = database.query(
                TABLE_NAME,
                null,
                ownerCPF == null ? null : "CPF_USUARIO = ?",
                ownerCPF == null ? null : new String[]{ownerCPF},
                null,
                null,
                null
        );


        while (cursor.moveToNext())
        {
            list.add(new Pet(cursor));
        }


        cursor.close();

        return list;
    }

    @Nullable
    public Bitmap getImageBitmap(Context context) throws IOException
    {
        return new GeneralUtilities(context).getExternalBitmap(IMAGE_DIRECTORY_NAME, this.photoFileName);
    }


    @NonNull
    public static List<Pet> searchByName(@NonNull String name, @NonNull Context context)
    {
        List<Pet> pets = new ArrayList<>();

        Cursor cursor = DatabaseHandler.openFrom(context).rawQuery(
                "select * from PET where NOME_PET = ?", new String[] { name }
        );

        while(cursor.moveToNext()) {
            pets.add(new Pet(cursor));
        }

        cursor.close();

        return pets;
    }

    public static List<String> namesFromIDs(List<Integer> numbers, Context context) {

        List<String> names = new ArrayList<>();

        for (Integer i : numbers) {

            Cursor cursor = DatabaseHandler.openFrom(context).query(
                    "PET",
                    new String[] { "NOME_PET" },
                    "CODIGO_PET = ?",
                    new String[] { String.valueOf(i) },
                    null,
                    null,
                    null

                    );

            while (cursor.moveToNext()) {
                names.add(cursor.getString(0));
            }

            cursor.close();
        }

        return names;


    }

    public static final int NO_PETS = 0;
    public static final int HAS_DOGS = 1;
    public static final int HAS_CATS = 1 << 1;
    
    public static Integer petDataFromOwner(String cpf, Context context)
    {
        Cursor cursor = DatabaseHandler.openFrom(context).query(
                TABLE_NAME,
                new String[]{"ESPECIE_PET"},
                "CPF_USUARIO = ?",
                new String[]{cpf},
                null, null, null
        );

        int result = 0;

        while (cursor.moveToNext() && result != (HAS_DOGS | HAS_CATS))
        {
            String species = cursor.getString(0);

            result |= species.equals("CÃO") ? HAS_DOGS : 0;
            result |= species.equals("GATO") ? HAS_CATS : 0;
        }

        cursor.close();

        return result;
    }


}
