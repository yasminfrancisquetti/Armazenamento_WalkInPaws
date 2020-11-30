package com.example.walkinpaws;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walkinpaws.model.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.NoSuchElementException;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private final static boolean isDebug  = false;

    EditText cpfEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cpfEditText = findViewById(R.id.edttxtlogin);
        passwordEditText = findViewById(R.id.edttxtsenha);

        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if(sensor == null)
        {
            Snackbar.make(findViewById(android.R.id.content), "NO SENSOR",
                    Snackbar.LENGTH_LONG).show();
        }
        else
        {
            manager.registerListener((SensorEventListener) this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        boolean isClose = event.values[0] > 0;

        if(!isClose)
        {
            Uri target = Uri.parse("geo: 0,0" + "?q=clínica veterinária");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, target);
            startActivity(mapIntent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void showMenu(android.view.View btnlogin)
    {
        String cpf = cpfEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!isDebug)
        {
            try
            {
                User user = new User(this, cpf, password);

                Intent caller = new Intent(this, MenuActivity.class);

                caller.putExtra(MenuActivity.INPUT_LOGGED_USER, user);

                startActivity(caller);
            }
            catch (NoSuchElementException ex)
            {
                Snackbar.make(findViewById(android.R.id.content), "Usuário não encontrado",
                        Snackbar.LENGTH_LONG).show();
            }
        }
        else
        {
            Intent caller = new Intent(this, MenuActivity.class);
            startActivity(caller);
        }
    }

    public void startNewUserActivity(android.view.View button)
    {
        Intent caller = new Intent(this, NewUserActivity.class);
        startActivity(caller);
    }
}