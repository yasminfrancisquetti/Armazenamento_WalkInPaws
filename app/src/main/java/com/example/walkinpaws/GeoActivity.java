package com.example.walkinpaws;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GeoActivity extends AppCompatActivity
{
    Location lastLocation;
    String lastAddress;

    private static final int REQUEST_LOCATION_PERMISSION = 0;

    TextView endatual;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        endatual = findViewById(R.id.txtvwendatual);
        exibirLocalizacao();
        createNotificationChannel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults)
    {
        if (requestCode == REQUEST_LOCATION_PERMISSION)
        {
            if ((grantResults.length > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                exibirLocalizacao();
            } else
            {
                Toast permissaonegada = Toast.makeText(this, "PERMITA ACESSO PARA UTILIZAR O RECURSO!", Toast.LENGTH_LONG);
                permissaonegada.show();
            }
        }
    }

    public void exibirLocalizacao()
    {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION)
            ;
        } else
        {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            client.getLastLocation().addOnSuccessListener(location ->
            {
                if (location != null)
                {
                    lastLocation = location;
                    // OBTEM LAT E LONG


                    if (!Geocoder.isPresent())
                    {
                        //erro
                        return;
                    }

                    Geocoder coder = new Geocoder(GeoActivity.this);

                    List<Address> addresses;

                    try
                    {
                        addresses = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e)
                    {
                        Log.d("ERRO", "ERRO DISPARADO AO TENTAR ENCONTRAR LOCALIZAÇÃO");
                        Toast erroloc = Toast.makeText(GeoActivity.this, "LOCALIZAÇÃO NÃO ENCONTRADA!",
                                Toast.LENGTH_LONG);
                        erroloc.show();
                        return;
                    }
                    if (addresses.size() == 0)
                    {
                        Toast erroloc = Toast.makeText(GeoActivity.this, "LOCALIZAÇÃO NÃO ENCONTRADA!",
                                Toast.LENGTH_LONG);
                        erroloc.show();
                        return;
                    }
                    Address address = addresses.get(0);

                    String addressLine = address.getAddressLine(0);

                    if (addressLine == null)
                    {
                        Toast erroloc = Toast.makeText(GeoActivity.this, "LOCALIZAÇÃO NÃO ENCONTRADA!",
                                Toast.LENGTH_LONG);
                        erroloc.show();
                        return;
                    }

                    String strendatual = String.format(getString(R.string.end_atual), addressLine);

                    endatual.setText(strendatual);
                    lastAddress = addressLine;


                    lastLocation = location;

                    // Usando as coordenadas vindas do GeoCoder
                    // para garantir mais resultados no Google Maps
                    // (botão efetuar geolocalização)
                    Address localAddress;

                    List<Address> possibleLocalAddresses;

                    try
                    {
                        possibleLocalAddresses = coder.getFromLocationName(addressLine, 1);
                    } catch (IOException ex)
                    {
                        Log.d("ERRO", "ERRO EM REVERSE GEOCODING");
                        Toast erroloc = Toast.makeText(GeoActivity.this, "LOCALIZAÇÃO NÃO ENCONTRADA!",
                                Toast.LENGTH_LONG);
                        erroloc.show();
                        return;
                    }

                    localAddress = possibleLocalAddresses.get(0);

                    lastLocation.setLatitude(localAddress.getLatitude());
                    lastLocation.setLongitude(localAddress.getLongitude());
                } else
                {
                    Toast locnaoencontrada = Toast.makeText(GeoActivity.this, "LOCALIZAÇÃO NÃO ENCONTRADA!", Toast.LENGTH_LONG);
                    locnaoencontrada.show();
                }
            });
            client.getLastLocation().addOnFailureListener(error -> Toast.makeText(
                    GeoActivity.this, "ERRO DE CONEXÃO OU PERMISSÃO, TENTE NOVAMENTE!",
                    Toast.LENGTH_LONG).show());
        }
    }

    public void direcionarMaps(android.view.View btnefetuargeo)
    {
        Uri target;

        if (lastAddress == null || lastLocation == null)
        {
            Snackbar.make(findViewById(android.R.id.content), "Localização não encontrada", Snackbar.LENGTH_LONG).show();
        } else
        {
            target = Uri.parse("geo:" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "?q=parque");

            Intent mapsStarter = new Intent(Intent.ACTION_VIEW, target);
            startActivity(mapsStarter);

            startWalk();
        }
    }

    public void startWalkListActivity(android.view.View btnpasseios)
    {
        Intent starter = new Intent(this, WalkListActivity.class);
        starter.putExtra(WalkListActivity.INPUT_LOCATION, lastAddress);
        startActivity(starter);
    }

    private void createNotificationChannel()
    {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            CharSequence name = "walk_in_paws_channel_name";
            String description = "walk_in_paws_channel_description";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public static final String CHANNEL_ID = "walk_in_paws_notification_channel";


    void startWalk()
    {
        String description = "Clique para finalizar!";


        Intent intent = new Intent(this, FinishWalkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String date = new SimpleDateFormat("dd:MM:yy").format(Calendar.getInstance().getTime());

        intent.putExtra(FinishWalkActivity.INPUT_START_TIME, date);
        intent.putExtra(FinishWalkActivity.INPUT_LOCATION, lastAddress);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // BUGPRONE

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setColor(Color.parseColor("#50FA7B"))
                .setContentTitle("PASSEIO EM ANDAMENTO")
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(0, builder.build());
    }
}