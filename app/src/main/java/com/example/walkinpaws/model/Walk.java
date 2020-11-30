package com.example.walkinpaws.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walkinpaws.util.GeneralUtilities;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Walk implements Model
{
    public static final String IMAGE_DIRECTORY_NAME = "walk-images";

    private final Integer id;
    private final String startTime;
    private final String endTime;
    private final String source;
    private final String destination;
    private final String imagePath;

    @Nullable
    private final String optionalMessage;

    @NonNull
    private final List<Integer> presentPetIDs;

    public Walk(@Nullable Integer id, String startTime, String endTime, String source, String destination, String imagePath, List<Integer> presentPetIDs,
                @Nullable String optionalMessage)
    {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.source = source;
        this.destination = destination;
        this.imagePath = imagePath;
        this.presentPetIDs = presentPetIDs;
        this.optionalMessage = optionalMessage;
    }

    public Walk(@NonNull Cursor cursor, SQLiteDatabase database)
    {

        id = cursor.getInt(0);
        startTime = cursor.getString(1);
        endTime = cursor.getString(2);
        source = cursor.getString(3);
        destination = cursor.getString(4);
        imagePath = cursor.getString(5);
        optionalMessage = null;

        Cursor petCursor = database.query(
                "PET_PASSEIO",
                new String[]{"CODIGO_PET"},
                "CODIGO_PASSEIO = ?",
                new String[]{String.valueOf(this.id)},
                null,
                null,
                null
        );

        this.presentPetIDs = new ArrayList<>();

        while (petCursor.moveToNext())
        {
            presentPetIDs.add(cursor.getInt(0));
        }

        petCursor.close();
    }


    public Integer getId()
    {
        return id;
    }

    @NonNull
    public String getStartTime()
    {
        return startTime;
    }

    @NonNull
    public String getEndTime()
    {
        return endTime;
    }

    @NonNull
    public String getSource()
    {
        return source;
    }

    @NonNull
    public String getDestination()
    {
        return destination;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    @Nullable
    public String getOptionalMessage()
    {
        return optionalMessage;
    }

    @NonNull
    public List<Integer> getPresentPets()
    {
        return presentPetIDs;
    }

    @Override
    public int registerFrom(@NonNull Context context)
    {

        ContentValues values = new ContentValues();

        values.put("CODIGO_PASSEIO", id);
        values.put("HORARIO_PASSEIO", startTime);
        values.put("HORARIO_CONCLUSAO_PASSEIO", endTime);
        values.put("ORIGEM_PASSEIO", source);
        values.put("DESTINO_PASSEIO", destination);
        values.put("CAMINHO_IMAGEM", imagePath);


        SQLiteDatabase database = DatabaseHandler.openFrom(context);
        int result = (int) database.insertOrThrow(
                "PASSEIO",
                null,
                values);

        if (presentPetIDs != null)
        {
            for (Integer id : presentPetIDs)
            {
                addDatabasePetReference(database, result, id);
            }
        }

        return result;
    }

    private void addDatabasePetReference(@NotNull SQLiteDatabase database, int selfID, int petID)
    {

        ContentValues values = new ContentValues();
        values.put("CODIGO_PET", petID);
        values.put("CODIGO_PASSEIO", selfID);

        database.insertOrThrow("PET_PASSEIO", null, values);
    }

    @Nullable
    public Bitmap getImageBitmapFrom(Context context) throws IOException
    {
        return new GeneralUtilities(context).getBitmap(IMAGE_DIRECTORY_NAME, this.imagePath);
    }

    @NonNull
    public static List<Walk> listFrom(@NonNull Context context)
    {
        List<Walk> walks = new ArrayList<>();

        SQLiteDatabase database = DatabaseHandler.openFrom(context);

        Cursor cursor = database.rawQuery("select * from PASSEIO;", null);

        while (cursor.moveToNext())
        {
            walks.add(new Walk(cursor, database));
        }

        cursor.close();

        return walks;
    }
}
