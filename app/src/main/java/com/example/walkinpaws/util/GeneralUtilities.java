package com.example.walkinpaws.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GeneralUtilities
{
    private final Context context;

    public GeneralUtilities(Context context)
    {
        this.context = context;
    }

    public Intent getImageSelectionIntent()
    {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }

    public File nextFile(File directory, String filePrefix) throws IOException
    {
        if (!directory.exists())
        {
            if (!directory.mkdir())
            {
                throw new IOException("Failed to create directory");
            }
        }

        String[] list = directory.list();

        int fileCount = list == null ? 0 : list.length;

        String name = filePrefix + fileCount + 1;

        File result = new File(directory, name);

        if (result.exists())
        {
            throw new IOException("File " + name + " already exists");
        }

        return result;
    }

    public File nextExternalFile(String directoryName, String filePrefix) throws IOException
    {
        return nextFile(new File(Environment.getExternalStorageDirectory(), directoryName), filePrefix);
    }

    public File nextFile(String directoryName, String filePrefix) throws IOException
    {
        File directory = new File(context.getFilesDir(), directoryName);
        return  nextFile(directory, filePrefix);
    }

    public void copyFile(@NonNull Uri uri, @NonNull File out) throws IOException
    {
        OutputStream output = new FileOutputStream(out);

        InputStream input = this.context.getContentResolver().openInputStream(uri);

        byte[] buffer = new byte[4096];
        int size;

        while ((size = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, size);
        }

        input.close();
        output.close();
    }

    public Bitmap getBitmap(File directory, String fileName) throws IOException {

        if (fileName == null)
        {
            return null;
        }


        if (!directory.exists())
        {
            return null;
        }

        File file = new File(directory, fileName);

        if (!file.exists())
        {
            return null;
        }

        return BitmapFactory.decodeStream(new FileInputStream(file));
    }

    public Bitmap getExternalBitmap(String directoryName, String fileName) throws IOException {

        return getBitmap(new File(Environment.getExternalStorageDirectory(), directoryName), fileName);

    }

    @Nullable
    public Bitmap getBitmap(String directoryName, String fileName) throws IOException
    {
        File directory = new File(context.getFilesDir(), directoryName);

        return getBitmap(directory, fileName);
    }

}
