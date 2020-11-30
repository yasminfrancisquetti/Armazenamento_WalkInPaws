package com.example.walkinpaws.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.example.walkinpaws.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class DatabaseHandler
{

    private static final boolean debugResetDatabase = false;
    private static boolean debugInitialized;

    public static final String DATABASE_NAME = "walk_in_paws_database";
    public static final String SHARED_PREFERENCES = "walk_in_paws_preferences";
    public static final String INITIALIZED = "initialized";

    private static String rawFileContent(Context context, @RawRes int file) throws IOException
    {
        String output;

        InputStream stream = context.getResources().openRawResource(file);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];

        int length;

        while ((length = stream.read(buffer)) != -1)
        {
            out.write(buffer, 0, length);
        }

        output = out.toString(StandardCharsets.UTF_8.name());

        out.close();
        stream.close();

        return output;
    }

    private static void resetFiles(@Nullable File directory)
    {
        String[] list;

        if (directory != null && directory.exists() && (list = directory.list()) != null)
        {
            for (String name : list)
            {
                if (!new File(directory, name).delete())
                {
                    Log.e("beep-error", "resetFiles: Failed to delete " + name);
                }
            }
        }
    }

    private static void executeScript(@NotNull Context context, @RawRes int file) throws IOException
    {
        SQLiteDatabase database;

        database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
                null
        );

        String[] script;
        script = rawFileContent(context, file).split("GO *\\n");


        for (String command : script)
        {
            command = command.replace("\n", "");

            if (!command.isEmpty())
            {
                database.execSQL(command);
            }
        }
    }

    private static void initialize(@NotNull Context context) throws IOException
    {

        boolean initialized;

        SharedPreferences preferences;

        preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (!debugResetDatabase)
        {
            initialized = preferences.getBoolean(INITIALIZED, false);
        } else
        {
            initialized = debugInitialized;
        }

        if (!initialized)
        {
            context.deleteDatabase(DATABASE_NAME);

            executeScript(context, R.raw.creation_script);
            executeScript(context, R.raw.insertion_script);

            SQLiteDatabase database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);


            database.execSQL("CREATE TABLE IF NOT EXISTS PET " +
                    "( CODIGO_PET INTEGER PRIMARY KEY autoincrement, NOME_PET TEXT, " +
                    "ESPECIE_PET TEXT, GENERO_PET TEXT, CPF_USUARIO TEXT," +
                    " CAMINHO_IMAGEM TEXT );");
                    // hweo, did it wokr?
                    // if not, what exception did it throw?
                    // okegh
            // hewo
            // yes owo
            // thankx to u
            // imma try to run
            // did you send your thingy alreadeh?
            // which excxzeption
            // NO U


            database.execSQL("CREATE TABLE IF NOT EXISTS PASSEIO ( CODIGO_PASSEIO INTEGER PRIMARY " +
                    "KEY autoincrement, HORARIO_PASSEIO TEXT, HORARIO_CONCLUSAO_PASSEIO TEXT, " +
                    "ORIGEM_PASSEIO TEXT, DESTINO_PASSEIO TEXT, CAMINHO_IMAGEM TEXT ); ");

            database.execSQL("CREATE TABLE IF NOT EXISTS PET_PASSEIO ( CODIGO_PET_PASSEIO INTEGER" +
                    " PRIMARY KEY autoincrement, CODIGO_PET INT, CODIGO_PASSEIO INT );");

            preferences.edit().putBoolean(INITIALIZED, true).apply();
            debugInitialized = true;
        }
    }

    public static SQLiteDatabase openFrom(Context context)
    {
        try
        {
            initialize(context);
        } catch (IOException ex)
        {
            SQLException exception = new SQLException();

            exception.initCause(ex);

            throw exception;
        }

        return context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
}
